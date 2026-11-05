package ma.fstg.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Encodeur de mots de passe utilisant l'algorithme BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définition des utilisateurs académiques en mémoire (In-Memory Authentication).
     * Les mots de passe sont encodés avec BCrypt pour un stockage sécurisé.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails etudiant = User.withUsername("etudiant")
                .password(passwordEncoder.encode("etudiant123"))
                .roles("ETUDIANT")
                .build();

        UserDetails enseignant = User.withUsername("professeur")
                .password(passwordEncoder.encode("prof123"))
                .roles("ENSEIGNANT", "ETUDIANT")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "ENSEIGNANT", "ETUDIANT")
                .build();

        return new InMemoryUserDetailsManager(etudiant, enseignant, admin);
    }

    /**
     * Chaîne de filtres de sécurité (SecurityFilterChain)
     * Définition des autorisations HTTP, formulaire de connexion, déconnexion et gestion des erreurs 403.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Ressources publiques accessibles à tous
                .requestMatchers("/", "/login", "/access-denied", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Restriction d'accès basée sur les rôles
                .requestMatchers("/etudiant/**").hasRole("ETUDIANT")
                .requestMatchers("/enseignant/**").hasRole("ENSEIGNANT")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Toute autre requête nécessite d'être authentifié
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                // Redirection vers une page 403 personnalisée conviviale
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
