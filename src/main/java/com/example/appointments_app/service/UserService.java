package com.example.appointments_app.service;

import com.example.appointments_app.exception.AuthenticationException;
import com.example.appointments_app.model.AuthRequest;
import com.example.appointments_app.model.CustomUserDetails;
import com.example.appointments_app.model.User;
import com.example.appointments_app.model.UserIn;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User register(UserIn userIn){
        try{
            Optional<User> user = userRepository.findUserByEmail(userIn.getEmail());

            if(user.isPresent())
                throw new com.example.appointments_app.exception.AuthenticationException("Email already exists!", HttpStatus.NOT_MODIFIED);
            User newUser = userIn.toUser();
            newUser.setPassword(passwordEncoder.encode(userIn.getPassword()));
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
