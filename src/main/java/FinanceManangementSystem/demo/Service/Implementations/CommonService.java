package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestLoginDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseLoginDTO;
import FinanceManangementSystem.demo.Security.JwtUtil;
import FinanceManangementSystem.demo.Service.CommonServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommonService implements CommonServiceInterface {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepo;

    @Override
    public ResponseLoginDTO login(RequestLoginDTO dto) {
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
