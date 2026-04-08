package br.com.vendemais.config;

import br.com.vendemais.security.JWTAuthenticationFilter;
import br.com.vendemais.security.JWTAuthorizationFilter;
import br.com.vendemais.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String[] PUBLIC_MATCHES = {
            "/h2-console/**"
    };

    @Autowired
    private Environment env;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    @Profile("test")
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {

        // Liberando acesso ao H2-console em perfil test
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        }

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http)) // Mantém o CORS ligado para o seu Frontend local funcionar!
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Libera todas as rotas

        System.out.println("ATENÇÃO: Segurança desligada para ambiente de testes!");

        return http.build();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {

        // Liberando acesso ao H2-console em perfil prod
        if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        }

        // Filtro de autenticação (login)
        JWTAuthenticationFilter authenticationFilter =
                new JWTAuthenticationFilter("/login", authenticationManager, jwtUtil);

        // Filtro de autorização (JWT em requests)
        JWTAuthorizationFilter authorizationFilter =
                new JWTAuthorizationFilter(jwtUtil, userDetailsService);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_MATCHES).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(false); // importante se for usar cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
