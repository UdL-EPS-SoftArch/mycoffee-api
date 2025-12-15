package cat.udl.eps.softarch.demo.config;
import cat.udl.eps.softarch.demo.domain.Admin;
import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AdminRepository;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;

@Configuration
public class DBInitialization {
    @Value("${default-password}")
    String defaultPassword;
    @Value("${spring.profiles.active:}")
    private String activeProfiles;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final BusinessRepository businessRepository;

    public DBInitialization(UserRepository userRepository, AdminRepository adminRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.businessRepository = businessRepository;
    }

    @PostConstruct
    public void initializeDatabase() {
        // Default user
        if (!userRepository.existsById("demo")) {
            User user = new User();
            user.setEmail("demo@sample.app");
            user.setId("demo");
            user.setPassword(defaultPassword);
            user.encodePassword();
            user.setRoles(Set.of("CUSTOMER"));
            userRepository.save(user);
        }

        if (!adminRepository.existsById("admin")) {
            Admin admin = new Admin();
            admin.setEmail("admin@sample.app");
            admin.setId("admin");
            admin.setPassword(defaultPassword);
            admin.encodePassword();
            admin.setRoles(Set.of("ADMIN"));
            adminRepository.save(admin);
        }

        if (!businessRepository.existsById("manager")) {
            Business business = new Business();
            business.setId("manager");
            business.setEmail("manager@coffee.com");
            business.setPassword(defaultPassword);
            business.encodePassword();
            business.setName("Best Coffee Shop");
            business.setAddress("Main Street 123");
            businessRepository.save(business);
        }
        if (Arrays.asList(activeProfiles.split(",")).contains("test")) {
            // Testing instances
            if (!userRepository.existsById("test")) {
                User user = new User();
                user.setEmail("test@sample.app");
                user.setId("test");
                user.setPassword(defaultPassword);
                user.encodePassword();
                user.setRoles(Set.of("CUSTOMER"));
                userRepository.save(user);
            }
            // Admin user for testing
            if (!adminRepository.existsById("admin")) {
                Admin admin = new Admin();
                admin.setEmail("admin@sample.app");
                admin.setId("admin");
                admin.setPassword(defaultPassword);
                admin.encodePassword();
                admin.setRoles(Set.of("ADMIN"));
                adminRepository.save(admin);
            }
        }
    }
}
