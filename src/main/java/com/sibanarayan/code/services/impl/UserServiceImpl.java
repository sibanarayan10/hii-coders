package com.sibanarayan.code.services.impl;

import com.sibanarayan.code.entities.User;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.enums.UserRole;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.UserResponse;
import com.sibanarayan.code.repository.UserRepository;
import com.sibanarayan.code.security.JwtFilter;
import com.sibanarayan.code.services.UserService;
import com.sibanarayan.code.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final JwtFilter filter;

    public boolean createUser(CreateUserRequest request){
        userRepository
                .findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException(
                            "User with this email already exists"
                    );
                });

        String encodedPw= passwordEncoder.encode(request.getPassword());
        User user=User.builder().
                name(request.getName()).
                email(request.getEmail()).
                phone(request.getPhone().toString()).
                role(UserRole.USER).
                password(encodedPw).build();
        userRepository.save(user);
        return true;
    }
    public UserResponse loginUser(LoginRequest request, HttpServletResponse response){
        String email= request.getEmail();
        Optional<User> optUser=userRepository.findByEmail(email);

        if(optUser.isEmpty()){
           throw new ResourceNotFoundException("User not found");
        }

        User user=optUser.get();

        String encodedPw=user.getPassword();
        String password= request.getPassword();

        boolean isMatch= passwordEncoder.matches(password,encodedPw);

        if(!isMatch){
            throw new RuntimeException("Wrong credentials!");
        }

        String token = jwtUtility.generateToken(email,user.getId());
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .recordStatus(RecordStatus.ACTIVE)
                .build();
    }

    public UserResponse getMe( String email){

        User user=getUser(email);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    public List<UserResponse> getUsers(String email){
        getUser(email);

        List<User> users= userRepository.findAll();

        return users.stream().filter(user->!user.getEmail().equals(email))
                .map(user->UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .recordStatus(user.getRecordStatus())
                .role(user.getRole())
                .build()).toList();
    }


    private User getUser(String email){
        Optional<User> optUser=userRepository.findByEmail(email);

        if(optUser.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        return optUser.get();
    }


}
