package com.emanuel.BiblioPlus.unit.modules.users;

import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.mappers.UserMapper;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.AddressModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.AddressRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateUserUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

        @Test
        void updateUserSuccessfully() {
            String userId = UUID.randomUUID().toString();
            UpdateUserDTO userDTO = UpdateUserDTO.builder()
                    .email("updated-email@gmail.com")
                    .cpf("27171868249")
                    .cep("01001000")
                    .birthDay("01/03/2000")
                    .build();

            UserModel user = new UserModel();
            user.setId(UUID.fromString(userId));
            user.setEmail("original-email@gmail.com");
            user.setCpf("27171868249");
            user.setAddress(UserMapper.mappingAddressDTOToAddressModel(new AddressDTO("01001000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP")));

            AddressDTO addressDTO = new AddressDTO("01001000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP");
            AddressModel updatedAddress = UserMapper.mappingAddressDTOToAddressModel(addressDTO);
            updatedAddress.setCep(userDTO.getCep());

            when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
            when(userRepository.findUserByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findByCpf(userDTO.getCpf())).thenReturn(Optional.empty());
            when(addressRepository.findByCep(userDTO.getCep())).thenReturn(Optional.of(updatedAddress));
            when(userRepository.save(any(UserModel.class))).thenReturn(user);

            UserModel updatedUser = userService.update(userId, userDTO);

            assertNotNull(updatedUser);
            assertEquals(userDTO.getEmail(), updatedUser.getEmail());
            assertEquals(userDTO.getCpf(), updatedUser.getCpf());
            assertEquals(userDTO.getCep(), updatedUser.getAddress().getCep());
        }
}
