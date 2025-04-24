package com.example.back.Projet_3.Controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.JWTService;
import com.example.back.Projet_3.services.RentalsService;
import com.example.back.Projet_3.services.UserService;

import java.util.HashMap;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
	@Autowired
	private RentalsService RentalServe;
	@Autowired
	private UserService UserServe;
    @Autowired
    private JWTService jwtService;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	@Autowired
	public RentalController(RentalsService rs, UserService us, JWTService jwt) {
		this.RentalServe = rs;
		this.UserServe= us;
		this.jwtService=jwt;
	}
	@GetMapping("")
    public ResponseEntity<?> getAllRentals(){
		 List<Rentals> rentList = this.RentalServe.getAllRentals();
		 ArrayList<Map<String,Object>> result = new ArrayList<>();
		 for(Rentals rent:rentList) {
			 Map<String,Object> currentRent = new HashMap<>();
			 currentRent.put("id", rent.getId());
			 currentRent.put("name", rent.getName());
			 currentRent.put("surface", rent.getSurface());
			 currentRent.put("price", rent.getPrice());
			 currentRent.put("picture", rent.getPicture());
			 currentRent.put("description", rent.getDescription());
			 currentRent.put("owner_id",rent.getOwner().getId());
			 currentRent.put("created_at", rent.getCreated_at().format(this.formatter));
			 currentRent.put("updated_at", rent.getUpdated_at()!=null ? rent.getUpdated_at().format(this.formatter) : "");
			 result.add(currentRent);
		 }
	 return ResponseEntity.ok(Map.of("rentals",result));
	}
	@GetMapping("/:id")
	public ResponseEntity<?> getRentalById(@RequestParam Integer id){
		Rentals rent = this.RentalServe.getRentalsById(id).orElseGet(null);
		if(rent==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","The id does not correspond to any existing rental"));
		}
		 Map<String,Object> currentRent = new HashMap<>();
		 currentRent.put("id", rent.getId());
		 currentRent.put("name", rent.getName());
		 currentRent.put("surface", rent.getSurface());
		 currentRent.put("price", rent.getPrice());
		 currentRent.put("picture", rent.getPicture());
		 currentRent.put("description", rent.getDescription());
		 currentRent.put("owner_id",rent.getOwner().getId());
		 currentRent.put("created_at", rent.getCreated_at().format(this.formatter));
		 currentRent.put("updated_at", rent.getUpdated_at()!=null ? rent.getUpdated_at().format(this.formatter) : "");
		return ResponseEntity.ok(currentRent);
	}
	@PostMapping("")
	public ResponseEntity<?> createRental(@RequestHeader("Authorization") String authHeader,@RequestBody Rentals rent){
        String token = authHeader.replace("Bearer ", "");
    	String email = jwtService.verifyToken(token);
    	if(email=="") {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
    	}
    	Users user = this.UserServe.findByEmail(email).orElse(null);
        if(user==null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
        }
		if(rent.getName() == null || rent.getSurface() == null || rent.getPrice() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","the rent needs a name, a surface and a price"));
		}
		if(rent.getOwner()== null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","the rent needs an owner"));
		}
		rent.setOwner(UserServe.findById(rent.getId()).orElseGet(null));
		if(rent.getOwner()==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","the owner does not exist"));
		}
		rent.setOwner(user);
		this.RentalServe.registerRental(rent);
		return ResponseEntity.ok(Map.of("message","Rental created !"));
	}
	@PutMapping("/id")
	public ResponseEntity<?> updateRental(@RequestHeader("Authorization") String authHeader,@RequestBody Rentals rent,@RequestParam Integer id){
        String token = authHeader.replace("Bearer ", "");
    	String email = jwtService.verifyToken(token);
    	if(email=="") {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
    	}
    	Users user = this.UserServe.findByEmail(email).orElse(null);
        if(user==null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
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
