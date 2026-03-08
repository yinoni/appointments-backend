package com.example.appointments_app.service;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.example.appointments_app.exception.AuthenticationException;
import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.exception.InvalidOTPException;
import com.example.appointments_app.exception.UserNotFoundException;
import com.example.appointments_app.kafka.UserProducer;
import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.authentication.PhoneVerifyInput;
import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.model.user.UserEventDTO;
import com.example.appointments_app.model.user.UserIn;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.BusinessRepo;
import com.example.appointments_app.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static com.example.appointments_app.redis.Redis.OTP_PREFIX;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProducer userProducer;
    private final Redis redis;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserProducer userProducer,
                       Redis redis){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProducer = userProducer;
        this.redis = redis;
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public User findByPhone(String phone){
        return userRepository.findUserByPhoneNumber(phone).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User register(UserIn userIn){
        User newUser = userIn.toUser();
        try{
            Optional<User> user = userRepository.findUserByEmail(userIn.getEmail());
            UserEventDTO dto;
            if(user.isPresent())
                throw new com.example.appointments_app.exception.AuthenticationException("Email already exists!", HttpStatus.NOT_MODIFIED);

            newUser.setPassword(passwordEncoder.encode(userIn.getPassword()));

            dto = new UserEventDTO(newUser.getFullName(), newUser.getEmail(), newUser.getPhoneNumber());

            newUser = userRepository.save(newUser);
            userProducer.userRegisteredEvent(dto);
        }
        catch(AuthenticationException e){
            throw new AuthenticationException("Email already exists!", HttpStatus.BAD_REQUEST);

        }
        catch (Exception e){
           throw  new AuthenticationException("Phone number is used!", HttpStatus.BAD_REQUEST);
        }
        return newUser;
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

    /***
     *
     * @param phoneVerifyInput - Contains the phone number and the 4-digit code that has been sent to this phone number
     * This function verify the phone number
     */
    public void verifyPhoneNumber(PhoneVerifyInput phoneVerifyInput){
        String phone = phoneVerifyInput.getPhoneNumber();
        String code = phoneVerifyInput.getCode();
        String otpCode = OTP_PREFIX + phone;

        int attempts = redis.incrementAndGetCounter(phone);

        if(attempts > 5)
            throw new InvalidOTPException("Too many attempts!");

        String savedCode = redis.getOtpCode(phone);

        if(savedCode == null)
            throw new InvalidOTPException("The code has expired!");

        if (!savedCode.equals(code))
            throw new InvalidOTPException("Incorrect code! Attempts left: " + (5 - attempts));

        User user = findByPhone(phone);

        userProducer.phoneVerifiedEvent(new UserEventDTO(user.getFullName(), user.getEmail(), user.getPhoneNumber()));
        redis.deleteKey(otpCode);
        redis.deleteKey(otpCode + ":counter");
    }

}
