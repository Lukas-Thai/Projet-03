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
    
    public Optional<Users> findByEmail(String email) {//cherche un utilisateur à partir de son email
        return usersRepository.findByEmail(email);
    }
    public Optional<Users> findById(Integer id) {//cherche un utilisateur à partir de son id
        return usersRepository.findById(id);
    }
    public Users registerUser(Users user) {//enregistre un utilisateur en bd
    	user.setEmail(user.getEmail().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));//hashage du mnot de passe 
        user.setCreated_at(LocalDateTime.now());
        return usersRepository.save(user);
    }
}
