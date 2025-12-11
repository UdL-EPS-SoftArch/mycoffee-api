package cat.udl.eps.softarch.demo.config;
import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class DBInitialization {
    @Value("${default-password}")
    String defaultPassword;
    @Value("${spring.profiles.active:}")
    private String activeProfiles;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public DBInitialization(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @PostConstruct
    public void initializeDatabase() {
        if (!userRepository.existsById("demo")) {
            User user = new User();
            user.setEmail("demo@sample.app");
            user.setId("demo");
            user.setPassword(defaultPassword);
            user.encodePassword();
            userRepository.save(user);
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
            if (!userRepository.existsById("test")) {
                User user = new User();
                user.setEmail("test@sample.app");
                user.setId("test");
                user.setPassword(defaultPassword);
                user.encodePassword();
                userRepository.save(user);
            }
        }
    }
}