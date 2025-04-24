package com.example.back.Projet_3.services;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.model.UsersRepository;


@Service
public class UserService {
    private UsersRepository usersRepository;

    private BCryptPasswordEncoder passwordEncoder;
    
    public UserService(UsersRepository usersRepository,BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
    public Optional<Users> findById(Integer id) {
        return usersRepository.findById(id);
    }
    public Users registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated_at(LocalDateTime.now());
        return usersRepository.save(user);
    }
}
