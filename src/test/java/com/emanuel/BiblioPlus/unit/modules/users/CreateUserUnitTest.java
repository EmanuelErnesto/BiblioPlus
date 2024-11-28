package com.emanuel.BiblioPlus.unit.modules.users;

import com.emanuel.BiblioPlus.modules.users.clients.ViaCepClient;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.mappers.UserMapper;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.AddressModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.AddressRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateUserUnitTest {

    @Mock
    private ViaCepClient viaCepClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createUserDTO = new CreateUserDTO();
        createUserDTO.setCpf("12345678901");
        createUserDTO.setEmail("test@domain.com");
        createUserDTO.setPassword("password123");
        createUserDTO.setCep("12345678");
    }

    @Test
    void createUserWithValidData() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("create-user-001")
                .email("create-user-001@gmail.com")
                .cpf("27171868249")
                .birthDay("01/02/2000")
                .password("password123")
                .cep("01001000")
                .build();

        AddressDTO addressDTO = new AddressDTO("01001000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP");


        AddressModel addressModel = UserMapper.mappingAddressDTOToAddressModel(addressDTO);
        addressModel.setCep(userDTO.getCep());

        UserModel userToCreate = UserMapper.mappingCreateUserDTOToUserModel(userDTO);
        userToCreate.setAddress(addressModel);


        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(userDTO.getPassword());
        when(userRepository.findByCpf(userDTO.getCpf())).thenReturn(Optional.empty());
        when(addressRepository.findByCep(userDTO.getCep())).thenReturn(Optional.empty());
        when(viaCepClient.getAddress(userDTO.getCep())).thenReturn(addressDTO);
        when(addressRepository.save(any(AddressModel.class))).thenReturn(addressModel);
        when(userRepository.save(any(UserModel.class))).thenReturn(userToCreate);

        UserModel createdUser = userService.create(userDTO);

        verify(userRepository).save(any(UserModel.class));
        verify(addressRepository).save(any(AddressModel.class));

        Assertions.assertThat(createdUser.getName()).isEqualTo(userToCreate.getName());
        Assertions.assertThat(createdUser.getEmail()).isEqualTo(userToCreate.getEmail());
        Assertions.assertThat(createdUser.getCpf()).isEqualTo(userToCreate.getCpf());
        Assertions.assertThat(createdUser.getAddress().getCep()).isEqualTo(addressDTO.getCep());
    }



    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        when(userRepository.findByCpf(createUserDTO.getCpf())).thenReturn(Optional.of(new UserModel()));

        HttpBadRequestException thrown = assertThrows(HttpBadRequestException.class, () -> userService.create(createUserDTO));
        assertEquals(UserExceptionConsts.CPF_ALREADY_EXISTS, thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.findByCpf(createUserDTO.getCpf())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(createUserDTO.getEmail())).thenReturn(new UserModel());

        HttpBadRequestException thrown = assertThrows(HttpBadRequestException.class, () -> userService.create(createUserDTO));
        assertEquals(UserExceptionConsts.EMAIL_ALREADY_EXISTS, thrown.getMessage());
    }


}
