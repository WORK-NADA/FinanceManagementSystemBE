package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.RequestDTO.RequestLoginDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseLoginDTO;
import FinanceManangementSystem.demo.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommonService implements CommonServiceInterface{

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepo;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public ResponseLoginDTO login(RequestLoginDTO dto) {
//        System.out.println(passwordEncoder.encode("Urvi@1524"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        Optional<User> user = userRepo.findByEmail(dto.getEmail());
        String token =
                jwtUtil.generateToken(user.get());

        return new ResponseLoginDTO(token);
    }
}
