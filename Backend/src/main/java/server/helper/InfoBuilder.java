package server.helper;

import java.time.format.DateTimeFormatter;

import server.dto.RentalResponse;
import server.dto.UserResponse;
import server.model.Rentals;
import server.model.Users;


public class InfoBuilder {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	public static RentalResponse rentalInfoBuilder(Rentals rent){//permet de convertir un objet rental en map pour l'envoi du résultat d'une requete API
	    RentalResponse dto = new RentalResponse();
	    dto.setId(rent.getId());
	    dto.setName(rent.getName());
	    dto.setSurface(rent.getSurface());
	    dto.setPrice(rent.getPrice());
	    dto.setPicture(rent.getPicture());
	    dto.setDescription(rent.getDescription());
	    dto.setOwner_id(rent.getOwner() != null ? rent.getOwner().getId() : null);
	    dto.setCreated_at(rent.getCreated_at().format(FORMATTER));
	    dto.setUpdated_at(rent.getUpdated_at() != null ? rent.getUpdated_at().format(FORMATTER) : "");
	    return dto;
	}
    public static UserResponse userInfoBuilder(Users user){//permet de convertir un objet user en map pour l'envoi du résultat d'une requete API
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreated_at(user.getCreated_at().format(FORMATTER));
        dto.setUpdated_at(user.getUpdated_at() != null ? user.getUpdated_at().format(FORMATTER) : "");
        return dto;
    }
}
