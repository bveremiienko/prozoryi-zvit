package ua.prozoryvit.transparency.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.prozoryvit.transparency.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.fundraiserOrganizer WHERE u.email = :email")
    Optional<AppUser> findByEmailWithOrganizer(String email);

    boolean existsByEmail(String email);
}
