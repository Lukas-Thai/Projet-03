package com.example.back.Projet_3.Controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.JWTService;
import com.example.back.Projet_3.services.RentalsService;
import com.example.back.Projet_3.services.UserService;

import dto.RentalRequest;
import helper.InfoBuilder;


@RestController
@RequestMapping("/api/rentals")
public class RentalController {
	@Autowired
	private RentalsService RentalServe;
	@Autowired
	private UserService UserServe;
    @Autowired
    private JWTService jwtService;
	@Autowired
	public RentalController(RentalsService rs, UserService us, JWTService jwt) {
		this.RentalServe = rs;
		this.UserServe= us;
		this.jwtService=jwt;
	}
	@GetMapping
    public ResponseEntity<Map<String,Object>> getAllRentals(){
		 List<Rentals> rentList = this.RentalServe.getAllRentals();
		 ArrayList<Map<String,Object>> result = new ArrayList<>();
		 for(Rentals rent:rentList) {
			 result.add(InfoBuilder.rentalInfoBuilder(rent));
		 }
	 return ResponseEntity.ok(Map.of("rentals",result));
	}
	@GetMapping("/:id")
	public ResponseEntity<Map<String,Object>> getRentalById(@RequestParam Integer id){
		Rentals rent = this.RentalServe.getRentalsById(id).orElseGet(null);
		if(rent==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","The id does not correspond to any existing rental"));
		}
		return ResponseEntity.ok(InfoBuilder.rentalInfoBuilder(rent));
	}
	
	@PostMapping
	public ResponseEntity<Map<String, String>> createRental(
	    @RequestHeader("Authorization") String authHeader,
	    @RequestPart("name") String name,
	    @RequestPart("surface") Float surface,
	    @RequestPart("price") Float price,
	    @RequestPart("description") String description,
	    @RequestPart(value = "picture", required = false) MultipartFile picture   // ton fichier (ex: image)
	) {
	    if (picture != null) {
	        System.out.println("Received file: " + picture.getOriginalFilename());
	        System.out.println("File size: " + picture.getSize());
	    } else {
	        System.out.println("No file received.");
	    }
	    String token = authHeader.replace("Bearer ", "");
	    String email = jwtService.verifyToken(token);
	    RentalRequest rent = new RentalRequest();
	    rent.setName(name);
	    rent.setSurface(surface);
	    rent.setDescription(description);
	    rent.setPrice(price);
	    if (email.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
	    }

	    Users user = this.UserServe.findByEmail(email).orElse(null);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
	    }

	    if (rent == null || rent.getName() == null || rent.getSurface() == null || rent.getPrice() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the rent needs a name, a surface and a price"));
	    }

	    this.RentalServe.registerRental(rent, user, picture);

	    return ResponseEntity.ok(Map.of("message", "Rental created !"));
	}
	@PutMapping("/:id")
	public ResponseEntity<Map<String,String>> updateRental(@RequestHeader("Authorization") String authHeader,@RequestBody RentalRequest rent,@RequestParam Integer id){
        String token = authHeader.replace("Bearer ", "");
    	String email = jwtService.verifyToken(token);
    	if(email=="") {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
    	}
    	Users user = this.UserServe.findByEmail(email).orElse(null);
        if(user==null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
        }
        if(rent == null) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","No rental to save"));
        }
        Rentals old = this.RentalServe.getRentalsById(id).orElseGet(null);
        if(old == null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Rental not found"));
        }
        if(old.getOwner().getEmail() != email) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "You do not own this rental"));
        }
        this.RentalServe.updateRentals(rent,old);
        return ResponseEntity.ok(Map.of("message","Rental updated !"));
	}
}
