package ua.prozoryvit.transparency.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignStatus;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findBySlug(String slug);

    @Query("SELECT c FROM Campaign c JOIN FETCH c.fundraiserOrganizer WHERE c.id = :id")
    Optional<Campaign> findByIdWithOrganizer(Long id);

    @Query("SELECT c FROM Campaign c JOIN FETCH c.fundraiserOrganizer WHERE c.slug = :slug")
    Optional<Campaign> findBySlugWithOrganizer(String slug);

    boolean existsBySlug(String slug);

    List<Campaign> findByStatusOrderByUpdatedAtDesc(CampaignStatus status);

    List<Campaign> findAllByOrderByUpdatedAtDesc();

    @Query("SELECT c FROM Campaign c JOIN FETCH c.fundraiserOrganizer ORDER BY c.updatedAt DESC")
    List<Campaign> findAllWithOrganizerOrderByUpdatedAtDesc();

    @Query("SELECT c FROM Campaign c JOIN FETCH c.fundraiserOrganizer WHERE c.fundraiserOrganizer.id = :organizerId ORDER BY c.updatedAt DESC")
    List<Campaign> findByOrganizerIdWithOrganizer(Long organizerId);

    long countByFundraiserOrganizerId(Long organizerId);

    long countByFundraiserOrganizerIdAndStatus(Long organizerId, CampaignStatus status);
}
