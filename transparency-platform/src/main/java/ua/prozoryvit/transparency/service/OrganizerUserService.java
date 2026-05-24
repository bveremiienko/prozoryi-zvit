package ua.prozoryvit.transparency.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.prozoryvit.transparency.domain.AppUser;
import ua.prozoryvit.transparency.domain.FundraiserOrganizer;
import ua.prozoryvit.transparency.repository.AppUserRepository;
import ua.prozoryvit.transparency.repository.FundraiserOrganizerRepository;

@Service
public class OrganizerUserService {

    private final AppUserRepository appUserRepository;
    private final FundraiserOrganizerRepository organizerRepository;

    public OrganizerUserService(
            AppUserRepository appUserRepository, FundraiserOrganizerRepository organizerRepository) {
        this.appUserRepository = appUserRepository;
        this.organizerRepository = organizerRepository;
    }

    @Transactional(readOnly = true)
    public FundraiserOrganizer requireCurrentOrganizer() {
        AppUser user = requireCurrentUser();
        if (user.getFundraiserOrganizer() == null) {
            throw new IllegalStateException("Обліковий запис не прив'язано до організації");
        }
        return user.getFundraiserOrganizer();
    }

    @Transactional
    public void linkDemoUserToOrganizer(String email, long organizerId) {
        appUserRepository.findByEmail(email).ifPresent(user -> {
            if (user.getFundraiserOrganizer() == null) {
                FundraiserOrganizer organizer = organizerRepository.findById(organizerId)
                        .orElseThrow();
                user.setFundraiserOrganizer(organizer);
                appUserRepository.save(user);
            }
        });
    }

    private AppUser requireCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Не авторизовано");
        }
        return appUserRepository.findByEmailWithOrganizer(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Користувача не знайдено"));
    }
}
