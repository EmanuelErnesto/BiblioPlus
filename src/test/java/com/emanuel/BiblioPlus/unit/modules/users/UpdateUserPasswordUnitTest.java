package com.emanuel.BiblioPlus.unit.modules.users;

import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.AddressRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
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
public class UpdateUserPasswordUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updatePasswordSuccessfully() {
        String userId = UUID.randomUUID().toString();
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String passwordConfirmation = "newPassword123";

        UserModel user = new UserModel();
        user.setId(UUID.fromString(userId));
        user.setPassword(passwordEncoder.encode(oldPassword));

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel updatedUser = invocation.getArgument(0);
            updatedUser.setPassword(passwordEncoder.encode(newPassword));
            return updatedUser;
        });

        UserModel updatedUser = userService.updatePassword(userId, oldPassword, newPassword, passwordConfirmation);

        assertNotNull(updatedUser);
        assertEquals(passwordEncoder.encode(newPassword), updatedUser.getPassword());
    }



    @Test
    void updatePasswordThrowsExceptionWhenPasswordsDoNotMatch() {
        String userId = UUID.randomUUID().toString();
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String passwordConfirmation = "differentPassword123";

        UserModel user = new UserModel();
        user.setId(UUID.fromString(userId));
        user.setPassword(passwordEncoder.encode(oldPassword));

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

        assertThrows(HttpBadRequestException.class, () -> {
            userService.updatePassword(userId, oldPassword, newPassword, passwordConfirmation);
        });
    }

    @Test
    void updatePasswordThrowsExceptionWhenOldPasswordIsIncorrect() {
        String userId = UUID.randomUUID().toString();
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword123";
        String passwordConfirmation = "newPassword123";

        UserModel user = new UserModel();
        user.setId(UUID.fromString(userId));
        user.setPassword(passwordEncoder.encode("correctOldPassword"));

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

        assertThrows(HttpBadRequestException.class, () -> {
            userService.updatePassword(userId, oldPassword, newPassword, passwordConfirmation);
        });
    }


}
