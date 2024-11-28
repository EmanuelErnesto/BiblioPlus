package com.emanuel.BiblioPlus.unit.modules.users;

import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.AddressRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListUserUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldReturnPagedUsersSuccessfully() {
        int page = 0;
        int size = 10;
        UserModel user1 = new UserModel();
        user1.setId(UUID.randomUUID());
        user1.setName("User 1");
        user1.setEmail("user1@example.com");

        UserModel user2 = new UserModel();
        user2.setId(UUID.randomUUID());
        user2.setName("User 2");
        user2.setEmail("user2@example.com");

        Page<UserModel> userPage = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.findAll(PageRequest.of(page, size))).thenReturn(userPage);

        Page<UserModel> result = userService.list(page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(user1.getName(), result.getContent().get(0).getName());
        assertEquals(user2.getName(), result.getContent().get(1).getName());
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsers() {
        int page = 0;
        int size = 10;

        Page<UserModel> emptyPage = Page.empty();

        when(userRepository.findAll(PageRequest.of(page, size))).thenReturn(emptyPage);

        Page<UserModel> result = userService.list(page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
