package ua.prozoryvit.transparency.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.prozoryvit.transparency.repository.AppUserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username)
                .map(u -> new User(
                        u.getEmail(),
                        u.getPasswordHash(),
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()))))
                .orElseThrow(() -> new UsernameNotFoundException("Користувача не знайдено"));
    }
}
