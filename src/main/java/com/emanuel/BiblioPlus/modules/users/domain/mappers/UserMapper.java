package com.emanuel.BiblioPlus.modules.users.domain.mappers;


import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.UserResponseDTO;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserRole;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserMapper {

    public static void mappingAddressDTOToUserAddressProperties(AddressDTO addressDTO, UserModel user) {
        user.setPatio(isNullOrEmpty(addressDTO.getLogradouro()) ? "N/A" : addressDTO.getLogradouro());
        user.setComplement(isNullOrEmpty(addressDTO.getComplemento()) ? "N/A" : addressDTO.getComplemento());
        user.setNeighborhood(isNullOrEmpty(addressDTO.getBairro()) ? "N/A" : addressDTO.getBairro());
        user.setLocality(isNullOrEmpty(addressDTO.getEstado()) ? "N/A" : addressDTO.getEstado());
        user.setUf(isNullOrEmpty(addressDTO.getUf()) ? "N/A" : addressDTO.getUf());
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static UserModel mappingCreateUserDTOToUserModel(CreateUserDTO userDTO) {
        var user = new UserModel();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setCpf(userDTO.getCpf());
        user.setPassword(userDTO.getPassword());
        user.setBirthDay(LocalDate.parse(userDTO.getBirthDay(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setCep(userDTO.getCep());
        user.setRole(UserRole.CLIENT);
        return user;
    }

    public static UserResponseDTO mappingUserEntityToUserResponseDTO(UserModel userCreated) {
        var userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userCreated.getId());
        userResponseDTO.setName(userCreated.getName());
        userResponseDTO.setEmail(userCreated.getEmail());
        userResponseDTO.setCpf(userCreated.getCpf());
        userResponseDTO.setBirthDay(userCreated.getBirthDay());
        userResponseDTO.setCep(userCreated.getCep());
        userResponseDTO.setRole(userCreated.getRole().name());
        userResponseDTO.setPatio(userCreated.getPatio());
        userResponseDTO.setComplement(userCreated.getComplement());
        userResponseDTO.setNeighborhood(userCreated.getNeighborhood());
        userResponseDTO.setLocality(userCreated.getLocality());
        userResponseDTO.setUf(userCreated.getUf());
        return userResponseDTO;
    }

    public static Map<String, Object> mappingPaginatedUsersToUserResponseDTO(Page<UserModel> paginatedUsers) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("users", paginatedUsers.
                getContent().
                stream().
                map(UserMapper::mappingUserEntityToUserResponseDTO));
        response.put("current-page", paginatedUsers.getTotalPages());
        response.put("total-items", paginatedUsers.getNumber());
        response.put("total-pages", paginatedUsers.getTotalPages());

        return response;
    }

    public static void mappingDataFromUpdateUserToUserEntity(UpdateUserDTO userDTO, UserModel user) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setCpf(userDTO.getCpf());
        user.setBirthDay(LocalDate.parse(userDTO.getBirthDay(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setCep(userDTO.getCep());
    }
}
