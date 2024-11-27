package com.emanuel.BiblioPlus.modules.users.domain.mappers;


import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AddressDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.PaginatedUsersResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.UserResponseDTO;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.AddressModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserRole;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserMapper {

    public static AddressModel mappingAddressDTOToAddressModel(AddressDTO addressDTO) {
       return AddressModel.builder()
                .patio(isNullOrEmpty(addressDTO.getLogradouro()) ? "N/A" : addressDTO.getLogradouro())
                .complement(isNullOrEmpty(addressDTO.getComplemento()) ? "N/A" : addressDTO.getComplemento())
                .neighborhood(isNullOrEmpty(addressDTO.getBairro()) ? "N/A" : addressDTO.getBairro())
                .locality(isNullOrEmpty(addressDTO.getEstado()) ? "N/A" : addressDTO.getEstado())
                .uf(isNullOrEmpty(addressDTO.getUf()) ? "N/A" : addressDTO.getUf())
                .build();
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
            userResponseDTO.setRole(userCreated.getRole().name());
            AddressModel address = userCreated.getAddress();
            userResponseDTO.setCep(address.getCep());
            userResponseDTO.setPatio(address.getPatio());
            userResponseDTO.setComplement(address.getComplement());
            userResponseDTO.setNeighborhood(address.getNeighborhood());
            userResponseDTO.setLocality(address.getLocality());
            userResponseDTO.setUf(address.getUf());
            return userResponseDTO;
        }


    public static PaginatedUsersResponseDTO mappingPaginatedUsersToUserResponseDTO(Page<UserModel> paginatedUsers) {
        return PaginatedUsersResponseDTO
                .builder()
                .users(paginatedUsers
                        .getContent()
                        .stream()
                        .map(UserMapper::mappingUserEntityToUserResponseDTO)
                        .toList())
                .current_page(paginatedUsers.getNumber())
                .total_items(paginatedUsers.getTotalElements())
                .total_pages(paginatedUsers.getTotalPages())
                .build();
    }

    public static void mappingDataFromUpdateUserToUserEntity(UpdateUserDTO userDTO, UserModel user) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setCpf(userDTO.getCpf());
        user.setBirthDay(LocalDate.parse(userDTO.getBirthDay(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
