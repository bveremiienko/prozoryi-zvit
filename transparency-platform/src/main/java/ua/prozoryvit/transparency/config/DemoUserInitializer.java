package ua.prozoryvit.transparency.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.prozoryvit.transparency.domain.AppUser;
import ua.prozoryvit.transparency.repository.AppUserRepository;
import ua.prozoryvit.transparency.service.OrganizerUserService;

@Component
public class DemoUserInitializer implements ApplicationRunner {

    private final AppUserRepository appUserRepository;
    private final DemoUserProperties demoUserProperties;
    private final PasswordEncoder passwordEncoder;
    private final OrganizerUserService organizerUserService;

    public DemoUserInitializer(
            AppUserRepository appUserRepository,
            DemoUserProperties demoUserProperties,
            PasswordEncoder passwordEncoder,
            OrganizerUserService organizerUserService) {
        this.appUserRepository = appUserRepository;
        this.demoUserProperties = demoUserProperties;
        this.passwordEncoder = passwordEncoder;
        this.organizerUserService = organizerUserService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!appUserRepository.existsByEmail(demoUserProperties.email())) {
            AppUser user = new AppUser();
            user.setEmail(demoUserProperties.email());
            user.setPasswordHash(passwordEncoder.encode(demoUserProperties.password()));
            user.setRole("ORGANIZER");
            appUserRepository.save(user);
        }
        organizerUserService.linkDemoUserToOrganizer(demoUserProperties.email(), 1L);
    }
}
