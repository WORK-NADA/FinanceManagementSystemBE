package FinanceManangementSystem.demo.Init;

import FinanceManangementSystem.demo.Enums.UserRole;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Model.UserAddress;
import FinanceManangementSystem.demo.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateAdminIfNot implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CreateAdminIfNot(UserRepository userRepository,
                            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        log.info("DEFAULT ADMIN - checking default admin...");

        if (userRepository.count() == 0) {

            User admin = new User();

            admin.setOwnerName("Ashish Patel");
            admin.setUsername("Ashish@11");
            admin.setEmail("ashish@gmail.com");
            admin.setPassword(passwordEncoder.encode("Ashish@123"));
            admin.setMobileNumber("9974729064");
            admin.setRole(UserRole.ADMIN);

            UserAddress address = new UserAddress();

            address.setHouseNo("530");
            address.setSocietyName("Mor");
            address.setArea("Olpad");
            address.setCity("Surat");
            address.setPincode("394530");
            address.setState("Gujarat");
            // No need to set country.
            // @PrePersist will automatically set it to "India".

            // Establish bidirectional relationship
            address.setUser(admin);
            admin.setAddress(address);

            userRepository.save(admin);

            log.info("✅ Default Admin Created Successfully...");
        }
    }
}