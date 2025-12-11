package cat.udl.eps.softarch.demo.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/businesses", "/businesses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/businesses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/businesses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/businesses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/businesses/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/inventories/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/inventories").hasRole("BUSINESS")
                        .requestMatchers(HttpMethod.PUT, "/inventories/**").hasRole("BUSINESS")
                        .requestMatchers(HttpMethod.PATCH, "/inventories/**").hasRole("BUSINESS")
                        .requestMatchers(HttpMethod.DELETE, "/inventories/**").hasRole("BUSINESS")

                        .requestMatchers(HttpMethod.GET, "/identity").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users").anonymous()
                        .requestMatchers(HttpMethod.POST, "/users/*").denyAll()
                        .requestMatchers(HttpMethod.POST, "/customers").anonymous()
                        .requestMatchers(HttpMethod.POST, "/customers/*").denyAll()
                        .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.POST, "/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/*/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/*/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/*/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/*/*").authenticated()
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.realmName("demo"));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        System.out.println("Allowed Origins: " + allowedOrigins);  // Debug line

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(allowedOrigins);
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
