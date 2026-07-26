package FinanceManangementSystem.demo.Init;

import FinanceManangementSystem.demo.Model.Enums.UserRole;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Model.UserAddress;
import FinanceManangementSystem.demo.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreateAdminIfNot implements CommandLineRunner {

    private final UserRepository userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    public CreateAdminIfNot(UserRepository userRepo,BCryptPasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepo.count() == 0){
            User admin = new User("Urvi Gondaliya","urvi@gmail.com",passwordEncoder.encode("Urvi@2415"), 9892648658L, UserRole.Admin,
                    new UserAddress(
                            29,"Avadh Bunglows","Mota varaccha","Surat",394101,"Gujarat")
                    );

            userRepo.save(admin);
            System.out.println("Default Admin Created.");
        }
    }
}
