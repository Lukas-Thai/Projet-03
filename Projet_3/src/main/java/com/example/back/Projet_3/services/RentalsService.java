package com.example.back.Projet_3.services;

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

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.RentalsRepository;
import com.example.back.Projet_3.model.Users;

import dto.RentalRequest;

@Service
public class RentalsService {
	private RentalsRepository rentalsRep;
	public RentalsService(RentalsRepository rentalsRep) {
		this.rentalsRep = rentalsRep;
	}
	public List<Rentals> getAllRentals(){
		return rentalsRep.findAll();
	}
	public Optional<Rentals> getRentalsById(Integer id) {
		return rentalsRep.findById(id);
	}
	public Rentals registerRental(RentalRequest rentReq, Users user, MultipartFile file) {
		Rentals rent = new Rentals();
		rent.setName(rentReq.getName());
		rent.setPrice(rentReq.getPrice());
		rent.setSurface(rentReq.getSurface());
		rent.setOwner(user);
		if (file != null && !file.isEmpty()) {
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
	        rent.setPicture("");
	    }

		if(rentReq.getDescription()==null) {
			rent.setDescription("");
		}
		else {
			rent.setDescription(rentReq.getDescription());
		}
		rent.setCreated_at(LocalDateTime.now());
		return this.rentalsRep.save(rent);
	}
	public Rentals updateRentals(RentalRequest rent, Rentals old) {
		if(rent.getName()!=null) {
			old.setName(rent.getName());
		}
		if(rent.getSurface()!=null) {
			old.setSurface(rent.getSurface());
		}
		if(rent.getPrice()!=null) {
			old.setPrice(rent.getPrice());
		}
		if(rent.getPicture()!=null) {
			old.setPicture(rent.getPicture());
		}
		if(rent.getDescription()!=null) {
			old.setDescription(rent.getDescription());
		}
		old.setUpdated_at(LocalDateTime.now());
		return this.rentalsRep.save(old);
	}
}
