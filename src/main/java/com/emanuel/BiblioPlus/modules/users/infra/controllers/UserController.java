package com.emanuel.BiblioPlus.modules.users.infra.controllers;

import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserPasswordDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.UserResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.mappers.UserMapper;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "users", description = "this endpoint can create, read, update and delete a user")
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "create a new user", description = "Resource for create a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User create successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid field inserted.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Cpf already used",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
            })
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid CreateUserDTO body) {
        UserModel userCreated = userService.create(body);

        logger.info("user created successfully. id: {}", userCreated.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.mappingUserEntityToUserResponseDTO(userCreated));
    }

    @Operation(summary = "Return a list of paginated users", description = "Resource that return a list of users",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users returned successfully",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "5") final Integer size) {

        Page<UserModel> users = userService.list(pageNumber, size);

        return ResponseEntity.ok(UserMapper.mappingPaginatedUsersToUserResponseDTO(users));
    }

    @Operation(summary = "Show a existent user", description = "Resource that return a existent user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> show(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id){

        UserModel user = userService.show(id);

        return ResponseEntity.ok(UserMapper.mappingUserEntityToUserResponseDTO(user));
    }

    @Operation(summary = "Update a existent user", description = "Resource that can update a existent user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Cpf already used",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id,
            @RequestBody
            @Valid
            UpdateUserDTO body) {


        UserModel user = userService.update(id, body);

        return ResponseEntity.ok(UserMapper.mappingUserEntityToUserResponseDTO(user));
    }


    @Operation(summary = "Delete a existent user", description = "Resource that delete a existent user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id inserted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user password property", description = "Resource that update a user password property",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password Updated Successfully."),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "User don't have permission to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "the password provided does not match with the current password or newPassword and passwordConfirmation does not match",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))

            })

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id,
            @Valid @RequestBody UpdateUserPasswordDTO body){
        userService.updatePassword(id, body.getOldPassword(), body.getNewPassword(), body.getPasswordConfirmation());

        return ResponseEntity.noContent().build();
    }


}
