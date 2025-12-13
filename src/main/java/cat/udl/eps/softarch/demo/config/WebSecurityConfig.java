package cat.udl.eps.softarch.demo.config;

import java.util.Arrays;
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
    String[] allowedOrigins;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
                        // Endpoints Públicos
                        .requestMatchers(HttpMethod.GET, "/businesses", "/businesses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/**").permitAll() // <-- NUEVO: Ver productos es público
                        .requestMatchers(HttpMethod.POST, "/users").anonymous()
                        .requestMatchers(HttpMethod.POST, "/customers").anonymous()

                        // Endpoints Bloqueados Específicamente
                        .requestMatchers(HttpMethod.POST, "/users/*").denyAll()
                        .requestMatchers(HttpMethod.POST, "/customers/*").denyAll()

                        // Gestión de Negocios (Solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/businesses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/businesses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/businesses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/businesses/**").hasRole("ADMIN")

                        // Gestión de Productos (Solo ADMIN y BUSINESS) <-- NUEVO
                        .requestMatchers(HttpMethod.POST, "/products", "/products/**").hasAnyRole("ADMIN", "BUSINESS")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "BUSINESS")
                        .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyRole("ADMIN", "BUSINESS")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "BUSINESS")

                        // Gestión de Pedidos (Roles Específicos)
                        .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("CUSTOMER")

                        // Identidad (Cualquiera Autenticado)
                        .requestMatchers(HttpMethod.GET, "/identity").authenticated()

                        // Reglas Genéricas (Resto requiere autenticación)
                        .requestMatchers(HttpMethod.POST, "/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/*/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/*/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/*/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/*/*").authenticated()
                        .anyRequest().permitAll())
                .csrf((csrf) -> csrf.disable())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic((httpBasic) -> httpBasic.realmName("demo"));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}