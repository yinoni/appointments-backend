package com.example.appointments_app.service;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.example.appointments_app.exception.*;
import com.example.appointments_app.jwt.JwtService;
import com.example.appointments_app.kafka.UserProducer;
import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.authentication.PhoneVerifyInput;
import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.user.*;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.BusinessRepo;
import com.example.appointments_app.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static com.example.appointments_app.redis.Redis.OTP_PREFIX;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProducer userProducer;
    private final Redis redis;
    private final SecureRandom secureRandom = new SecureRandom();
    public static final String LOGGED_OUT_SET_REDIS_KEY = "logged-out-users";
    private final JwtService jwtService;
    @Lazy
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserProducer userProducer,
                       Redis redis,
                       JwtService jwtService,
                       ModelMapper modelMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProducer = userProducer;
        this.redis = redis;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
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
                throw new com.example.appointments_app.exception.AuthenticationException("Email already exists!", HttpStatus.BAD_REQUEST, "email");

            newUser.setPassword(passwordEncoder.encode(userIn.getPassword()));

            dto = new UserEventDTO(newUser.getFullName(), newUser.getEmail(), newUser.getPhoneNumber());

            newUser = userRepository.save(newUser);
            userProducer.userRegisteredEvent(dto);
        }
        catch(AuthenticationException e){
            throw new AuthenticationException("Email already exists!", HttpStatus.BAD_REQUEST, "email");

        }
        catch (Exception e){
           throw  new AuthenticationException("Phone number is used!", HttpStatus.BAD_REQUEST, "phone");
        }
        return newUser;
    }

    /**
     *
     * @param userUpdateRequest - The fields to update
     * @return - The updated user
     */
    public UserDTO updateUser(UserUpdateRequest userUpdateRequest, Long userID){
        User prevUser = this.findById(userID);

        /*
         * TODO: Verify that the update fields are not equals to the current value of those fields from the prevUser object
         */

        modelMapper.map(userUpdateRequest, prevUser);

        return saveUser(prevUser).convertToUserDTO();
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationException("User not found", HttpStatus.BAD_REQUEST, "emailOrPassword"));

        return new CustomUserDetails(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPhoneNumber(),
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

    /***
     *
     * @param phoneNumber - The phone number that the new code will be sent to
     */
    public void resendOtpCode(String phoneNumber){
        if(redis.getOtpCode(phoneNumber) == null){
            String privateCode = generateOTPCode();
            redis.saveOtp(phoneNumber, privateCode);

            userProducer.resendOTPEvent(new OtpTaskCode(phoneNumber, privateCode));
        }
        else
            throw new OtpNotExpiredException("The OTP is not expired yet!");
    }

    public String generateOTPCode(){
        int code = secureRandom.nextInt(10000);
        return String.format("%04d", code);
    }

    public long getRemainingTimeInSeconds(String token) {
        Date expirationDate = jwtService.extractDate(token);
        long now = System.currentTimeMillis();
        long diff = expirationDate.getTime() - now;

        // מחזירים בשניות (לשימוש ב-Redis)
        return diff > 0 ? diff / 1000 : 0;
    }

    public void logout(String jwtToken){
        long expiration = getRemainingTimeInSeconds(jwtToken);

        redis.addToSet(LOGGED_OUT_SET_REDIS_KEY, jwtToken, expiration);
    }

}
