    package com.emanuel.BiblioPlus.modules.users.infra.database.repositories;

    import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.util.Optional;
    import java.util.UUID;

    public interface UserRepository extends JpaRepository<UserModel, UUID> {

        Optional<UserModel> findByCpf(String cpf);

        UserDetails findByEmail(String email);

        @Query("SELECT u from UserModel u WHERE u.email = :email")
        Optional<UserModel> findUserByEmail(String email);

        @Query(value = "SELECT u from UserModel u")
        Page<UserModel> findAllWithPagination(Pageable pageable);
    }
