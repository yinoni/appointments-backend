package com.example.appointments_app.service;

import com.example.appointments_app.exception.AuthenticationException;
import com.example.appointments_app.kafka.UserProducer;
import com.example.appointments_app.model.*;
import com.example.appointments_app.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProducer userProducer;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserProducer userProducer){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProducer = userProducer;
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public User findByPhone(String phone){
        return userRepository.findUserByPhoneNumber(phone).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }


    public User register(UserIn userIn){
        try{
            Optional<User> user = userRepository.findUserByEmail(userIn.getEmail());
            UserEventDTO dto;
            if(user.isPresent())
                throw new com.example.appointments_app.exception.AuthenticationException("Email already exists!", HttpStatus.NOT_MODIFIED);
            User newUser = userIn.toUser();
            newUser.setPassword(passwordEncoder.encode(userIn.getPassword()));

            dto = new UserEventDTO(newUser.getFullName(), newUser.getEmail(), newUser.getPhoneNumber());

            userProducer.userRegisteredEvent(dto);

            return userRepository.save(newUser);
        }
        catch(AuthenticationException e){
            throw new AuthenticationException("Email already exists!", HttpStatus.BAD_REQUEST);

        }
        catch (Exception e){
           throw  new AuthenticationException("Phone number is used!", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationException("User not found", HttpStatus.BAD_REQUEST));

        return new CustomUserDetails(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                Collections.emptyList()
        );
    }
}
