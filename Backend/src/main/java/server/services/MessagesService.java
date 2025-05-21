package server.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import server.model.Messages;
import server.model.MessagesRepository;
import server.model.Rentals;
import server.model.Users;


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
