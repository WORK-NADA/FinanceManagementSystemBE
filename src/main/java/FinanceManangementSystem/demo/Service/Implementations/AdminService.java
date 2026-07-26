package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseUserDTO;
import FinanceManangementSystem.demo.Security.JwtUtil;
import FinanceManangementSystem.demo.Service.AdminServiceInterface;
import FinanceManangementSystem.demo.Model.Enums.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService implements AdminServiceInterface {

    private final UserRepository userRepo;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public AdminService(UserRepository userRepo,ModelMapper modelMapper,BCryptPasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,JwtUtil jwtUtil){
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseUserDTO registration(RequestUserDTO dto) {
        Optional<String> name = userRepo.findByEmailOrContact(dto.getContact(),dto.getEmail());

        if(name.isPresent()){
            throw new RuntimeException("User already exists...");
        }

        User user = modelMapper.map(dto,User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.Client);
        if(user.getAddress()!=null){
            user.getAddress().setUser(user);
        }
        userRepo.save(user);

        ResponseUserDTO response = modelMapper.map(user,ResponseUserDTO.class);
        return response;
    }
}
