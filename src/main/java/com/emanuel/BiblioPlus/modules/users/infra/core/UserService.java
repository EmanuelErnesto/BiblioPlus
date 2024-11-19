package com.emanuel.BiblioPlus.modules.users.infra.core;

import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import org.springframework.data.domain.Page;


public interface UserService {
    UserModel create(CreateUserDTO createUserDto);
    UserModel show(String id);
    Page<UserModel> list(int page, int size);
    UserModel update(String id, UpdateUserDTO userDTO);
    void delete(String id);
    UserModel updatePassword(String id, String oldPassword, String newPassword,String passwordConfirmation);
}
