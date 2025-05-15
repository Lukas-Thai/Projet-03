package com.example.back.Projet_3.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.back.Projet_3.model.Messages;
import com.example.back.Projet_3.model.MessagesRepository;
import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.Users;

@Service
public class MessagesService {
	private MessagesRepository messageRep;
	public MessagesService(MessagesRepository mr) {
		this.messageRep = mr;
	}
	
	public Messages createMessage(Users user, Rentals rental, String content) {//enregistre un message en base de données
		Messages newMes = new Messages();
		newMes.setSender(user);
		newMes.setRental(rental);
		newMes.setMessage(content);
		newMes.setCreated_at(LocalDateTime.now());
		return this.messageRep.save(newMes);
	}
}
