package com.emanuel.BiblioPlus.modules.users.infra.database.repositories;


import com.emanuel.BiblioPlus.modules.users.infra.database.entities.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressModel, UUID> {
    Optional<AddressModel> findByCep(String cep);
}
