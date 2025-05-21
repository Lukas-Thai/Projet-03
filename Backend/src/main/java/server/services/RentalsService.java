package server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import server.dto.RentalRequest;
import server.model.Rentals;
import server.model.RentalsRepository;
import server.model.Users;


@Service
public class RentalsService {
	private RentalsRepository rentalsRep;
	public RentalsService(RentalsRepository rentalsRep) {
		this.rentalsRep = rentalsRep;
	}
	public List<Rentals> getAllRentals(){//retourne tout les rentals en BD
		return rentalsRep.findAll();
	}
	public Optional<Rentals> getRentalsById(Integer id) {//cherche un rental avec un id fourni
		return rentalsRep.findById(id);
	}
	public Rentals registerRental(RentalRequest rentReq, Users user, MultipartFile file) {//enregistre un rental
		Rentals rent = new Rentals();
		rent.setName(rentReq.getName());
		rent.setPrice(rentReq.getPrice());
		rent.setSurface(rentReq.getSurface());
		rent.setOwner(user);
		if (file != null && !file.isEmpty()) {//si une image a été fourni avec le rental, on l'enregistre sur le serveur
	        try {
	            String uploadDir = "uploads/";
	            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir + fileName);

	            Files.createDirectories(filePath.getParent());
	            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	            rent.setPicture("/" + uploadDir + fileName);
	        } catch (IOException e) {
	            e.printStackTrace();
	            rent.setPicture(""); 
	        }
	    } else {
	        rent.setPicture("");//sinon on laisse vide
	    }

		if(rentReq.getDescription()==null) {
			rent.setDescription("");
		}
		else {
			rent.setDescription(rentReq.getDescription());
		}
		rent.setCreated_at(LocalDateTime.now());
		return this.rentalsRep.save(rent);//on sauvegarde le tout en bd
	}
	public Rentals updateRentals(RentalRequest rent, Rentals old) {//met à jour un rental
		if(rent.getName()!=null) {//on écrase les informations de l'ancien rental avec les nouvelles
			old.setName(rent.getName());
		}
		if(rent.getSurface()!=null) {
			old.setSurface(rent.getSurface());
		}
		if(rent.getPrice()!=null) {
			old.setPrice(rent.getPrice());
		}
		if(rent.getDescription()!=null) {
			old.setDescription(rent.getDescription());
		}
		old.setUpdated_at(LocalDateTime.now());
		return this.rentalsRep.save(old);
	}
}
