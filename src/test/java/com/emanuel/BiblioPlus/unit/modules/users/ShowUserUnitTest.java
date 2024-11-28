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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowUserUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void returnUserThatExists(){
        UUID userId = UUID.randomUUID();
        UserModel userToRetrieve  = new UserModel();
        userToRetrieve.setId(userId);
        userToRetrieve.setName("create-user-unit-001");
        userToRetrieve.setEmail("create-user-unit001@gmail.com");
        userToRetrieve.setCpf("12345678901");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToRetrieve));

        UserModel foundUser = userService.show(userId.toString());

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals(userToRetrieve.getName(), foundUser.getName());
        assertEquals(userToRetrieve.getEmail(), foundUser.getEmail());
        assertEquals(userToRetrieve.getCpf(), foundUser.getCpf());
    }

    @Test
    void returnUserThatNotExistsShouldThrowException(){
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        HttpNotFoundException expectedReturn = assertThrows(HttpNotFoundException.class, () ->  {
            userService.show(userId.toString());
        });
        assertEquals(UserExceptionConsts.USER_NOT_FOUND, expectedReturn.getMessage());
    }

    @Test
    void returnUserWithInvalidIdReturnIllegalArgumentException(){
        String invalidUserId = "invalid-user-id";

        IllegalArgumentException expectedReturn = assertThrows(IllegalArgumentException.class, () -> {
            userService.show(invalidUserId);
        });

        assertEquals("Invalid UUID string: " + invalidUserId, expectedReturn.getMessage());
    }
}
