package ua.prozoryvit.transparency.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.prozoryvit.transparency.domain.FundraiserOrganizer;

public interface FundraiserOrganizerRepository extends JpaRepository<FundraiserOrganizer, Long> {

    Optional<FundraiserOrganizer> findBySlug(String slug);

    List<FundraiserOrganizer> findAllByOrderByNameAsc();
}
