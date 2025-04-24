package com.example.back.Projet_3.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.model.UsersRepository;


public class CustomUserDetailService implements UserDetailsService {
	@Autowired
	private UsersRepository userRepo;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Users user = userRepo.findByEmail(email).get();
		return new User(user.getName(),user.getPassword(), null);
	}

}
