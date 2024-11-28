package com.emanuel.BiblioPlus.unit.modules.users;

import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.AddressRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteUserUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void deleteUserThatExists(){
        String userId = UUID.randomUUID().toString();
        UserModel userToDelete  = new UserModel();
        userToDelete.setId(UUID.fromString(userId));
        userToDelete.setName("delete-user-unit-001");
        userToDelete.setEmail("delete-user-unit001@gmail.com");

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(userToDelete));


        doNothing().when(userRepository).delete(userToDelete);

        userService.delete(userId);

        verify(userRepository).delete(userToDelete);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        HttpNotFoundException expectedReturn = assertThrows(HttpNotFoundException.class, () ->  {
            userService.delete(userId.toString());
        });

        assertEquals(UserExceptionConsts.USER_NOT_FOUND, expectedReturn.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid(){
        String invalidUserId = "invalid-user-id";

        IllegalArgumentException expectedReturn = assertThrows(IllegalArgumentException.class, () -> {
            userService.delete(invalidUserId);
        });

        assertEquals("Invalid UUID string: " + invalidUserId, expectedReturn.getMessage());
    }
}
