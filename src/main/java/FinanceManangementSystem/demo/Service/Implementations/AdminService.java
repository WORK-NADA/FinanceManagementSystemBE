package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseUserDTO;
import FinanceManangementSystem.demo.Security.JwtUtil;
import FinanceManangementSystem.demo.Service.AdminServiceInterface;
import FinanceManangementSystem.demo.UserRole.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService implements AdminServiceInterface {

    @Autowired
    UserRepository userRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public ResponseUserDTO registration(RequestUserDTO dto) {
        Optional<String> name = userRepo.findByEmailOrContact(dto.getContact(),dto.getEmail());

        if(!name.isEmpty()){
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
