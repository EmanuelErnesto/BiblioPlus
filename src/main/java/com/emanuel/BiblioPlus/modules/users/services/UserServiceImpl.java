package com.emanuel.BiblioPlus.modules.users.services;

import com.emanuel.BiblioPlus.modules.users.clients.ViaCepClient;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.mappers.UserMapper;
import com.emanuel.BiblioPlus.modules.users.infra.core.UserService;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.AddressModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.AddressRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserModel create(CreateUserDTO createUserDto) {
        checkIfUserExists(createUserDto);

        AddressModel address = getAddress(createUserDto.getCep());

        UserModel userToCreate = UserMapper.mappingCreateUserDTOToUserModel(createUserDto);
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        userToCreate.setAddress(address);

        return userRepository.save(userToCreate);
    }

    @Transactional(readOnly = true)
    public UserModel getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new HttpNotFoundException(UserExceptionConsts.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public UserModel show(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new HttpNotFoundException(UserExceptionConsts.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserModel> list(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    @Override
    public UserModel update(String id, UpdateUserDTO userDTO) {
        UserModel user = show(id);
        checkIfEmailOrCpfExists(userDTO, user);

        if (!Objects.equals(userDTO.getCep(), user.getAddress().getCep())) {
            AddressModel newAddress = getAddress(userDTO.getCep());
            user.setAddress(newAddress);
        }

        UserMapper.mappingDataFromUpdateUserToUserEntity(userDTO, user);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(String id) {
        UserModel user = show(id);
        userRepository.delete(user);
    }

    @Transactional
    @Override
    public UserModel updatePassword(String id, String oldPassword, String newPassword, String passwordConfirmation) {
        UserModel user = show(id);

        if (!newPassword.equals(passwordConfirmation)) {
            throw new HttpBadRequestException(UserExceptionConsts.PASSWORD_MUST_EQUAL);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new HttpBadRequestException(UserExceptionConsts.PASSWORD_DOES_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    private void checkIfUserExists(CreateUserDTO createUserDto) {
        if (userRepository.findByCpf(createUserDto.getCpf()).isPresent()) {
            throw new HttpBadRequestException(UserExceptionConsts.CPF_ALREADY_EXISTS);
        }

        if (userRepository.findByEmail(createUserDto.getEmail()) != null) {
            throw new HttpBadRequestException(UserExceptionConsts.EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkIfEmailOrCpfExists(UpdateUserDTO userDTO, UserModel user) {
        userRepository.findUserByEmail(userDTO.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new HttpBadRequestException(UserExceptionConsts.EMAIL_ALREADY_EXISTS);
                });

        userRepository.findByCpf(userDTO.getCpf())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new HttpBadRequestException(UserExceptionConsts.CPF_ALREADY_EXISTS);
                });
    }

    private AddressModel getAddress(String cep) {
        return addressRepository.findByCep(cep)
                .orElseGet(() -> {
                    AddressDTO addressDTO = viaCepClient.getAddress(cep);
                    AddressModel address = UserMapper.mappingAddressDTOToAddressModel(addressDTO);
                    address.setCep(cep);
                    return addressRepository.save(address);
                });
    }
}
