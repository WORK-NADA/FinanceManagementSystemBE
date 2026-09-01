package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Exceptions.DuplicateResourceException;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Model.UserAddress;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseUserDTO;
import FinanceManangementSystem.demo.Service.AdminServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class AdminService implements AdminServiceInterface {

    private final UserRepository userRepo;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public ResponseUserDTO registration(RequestUserDTO dto) {
        log.info("SERVICE - request came in registration...");

        Optional<String> name = userRepo.findByEmailOrContact(dto.getEmail(),dto.getMobileNumber());

        if(name.isPresent()){
            throw new DuplicateResourceException("User already exists...");
        }

        User user = modelMapper.map(dto,User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (dto.getUserAddress() != null) {
            UserAddress address = modelMapper.map(dto.getUserAddress(), UserAddress.class);

            address.setUser(user);

            user.setAddress(address);
        }

        user = userRepo.save(user);

        log.info("SERVICE - registered successfully...");

        ResponseUserDTO response = modelMapper.map(user,ResponseUserDTO.class);
        return response;
    }
    @Override
    @Transactional(readOnly = true)
    public java.util.List<ResponseUserDTO> listAllUsers() {
        log.info("SERVICE - request came in listAllUsers...");
        
        java.util.List<User> users = userRepo.findAllByOrderByCreatedAtDesc();
        
        log.info("SERVICE - users fetched successfully...");
        
        return users.stream()
                .map(user -> modelMapper.map(user, ResponseUserDTO.class))
                .collect(java.util.stream.Collectors.toList());
    }
}
