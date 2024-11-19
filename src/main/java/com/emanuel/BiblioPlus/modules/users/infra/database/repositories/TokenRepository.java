package com.emanuel.BiblioPlus.modules.users.infra.database.repositories;

import com.emanuel.BiblioPlus.modules.users.infra.database.entities.TokenModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenModel, UUID> {

    Optional<TokenModel> findByTokenAndUser(String token, UserModel user);

    Optional<TokenModel> findTopByUser_IdAndRevokedFalseOrderByCreatedAtDesc(UUID userId);

    void deleteByTokenAndUser(String token, UserModel user);
}
