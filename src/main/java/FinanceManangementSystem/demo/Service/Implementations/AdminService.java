package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseUserDTO;
import FinanceManangementSystem.demo.Service.AdminServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class AdminService implements AdminServiceInterface {

    private final UserRepository userRepo;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public ResponseUserDTO registration(RequestUserDTO dto) {
        log.info("SERVICE - request came in registration...");

        Optional<String> name = userRepo.findByEmailOrContact(dto.getEmail(),dto.getMobileNumber());

        if(name.isPresent()){
            throw new RuntimeException("User already exists...");
        }

        User user = modelMapper.map(dto,User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getAddress()!=null){
            user.getAddress().setUser(user);
        }
        user = userRepo.save(user);

        log.info("SERVICE - registered successfully...");

        ResponseUserDTO response = modelMapper.map(user,ResponseUserDTO.class);
        return response;
    }
}
