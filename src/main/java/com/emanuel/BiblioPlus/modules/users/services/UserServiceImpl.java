package com.emanuel.BiblioPlus.modules.users.services;

import com.emanuel.BiblioPlus.modules.users.clients.ViaCepClient;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.mappers.UserMapper;
import com.emanuel.BiblioPlus.modules.users.infra.core.UserService;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserModel create(CreateUserDTO createUserDto){

        Optional<UserModel> userCpfAlreadyExists = userRepository
                .findByCpf(createUserDto.getCpf());


        if(userCpfAlreadyExists.isPresent()) {
            throw new HttpBadRequestException(UserExceptionConsts.CPF_ALREADY_EXISTS);

        }

        UserDetails userEmailAlreadyExists = userRepository
                .findByEmail(createUserDto.getEmail());

        if(userEmailAlreadyExists != null) {
            throw new HttpBadRequestException(UserExceptionConsts.EMAIL_ALREADY_EXISTS);
        }

        AddressDTO address = viaCepClient.getAddress(createUserDto.getCep());

        UserModel userToCreate = UserMapper.mappingCreateUserDTOToUserModel(createUserDto);

        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));

        UserMapper.mappingAddressDTOToUserAddressProperties(address, userToCreate);

        return userRepository.save(userToCreate);
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
        return userRepository.findAllWithPagination(PageRequest.of(page, size));
    }

    @Transactional
    @Override
    public UserModel update(String id, UpdateUserDTO userDTO) {

        UserModel user = show(id);

        Optional<UserModel> userEmailAlreadyUsed = userRepository
                .findUserByEmail(userDTO.getEmail());


        if(userEmailAlreadyUsed.isPresent() && !Objects.equals(userEmailAlreadyUsed.get().getId(), user.getId())) {
            throw new HttpBadRequestException(UserExceptionConsts.EMAIL_ALREADY_EXISTS);
        }

        Optional<UserModel> userCpfAlreadyUsed = userRepository.findByCpf(userDTO.getCpf());

        if(userCpfAlreadyUsed.isPresent() && !Objects.equals(userCpfAlreadyUsed.get().getId(), user.getId())) {
            throw new HttpBadRequestException(UserExceptionConsts.CPF_ALREADY_EXISTS);
        }

        if(!Objects.equals(userDTO.getCep(), user.getCep())) {
            AddressDTO addressDTO = viaCepClient.getAddress(userDTO.getCep());
            UserMapper.mappingAddressDTOToUserAddressProperties(addressDTO, user);
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
    public UserModel updatePassword(String id, String oldPassword, String newPassword ,String passwordConfirmation) {
        if(!passwordConfirmation.equals(newPassword)) throw new HttpBadRequestException(UserExceptionConsts.PASSWORD_MUST_EQUAL);

        UserModel user = show(id);

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) throw new HttpBadRequestException(UserExceptionConsts.PASSWORD_DOES_NOT_MATCH);

        user.setPassword(passwordEncoder.encode(newPassword));

        return user;
    }
}
