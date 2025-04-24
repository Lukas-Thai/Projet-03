package com.example.back.Projet_3.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.RentalsRepository;

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
	public Rentals registerRental(Rentals rent) {
		if(rent.getPicture()==null) {
			rent.setPicture("");
		}
		if(rent.getDescription()==null) {
			rent.setDescription("");
		}
		rent.setCreated_at(LocalDateTime.now());
		return this.rentalsRep.save(rent);
	}
	public Rentals updateRentals(Rentals rent, Rentals old) {
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
