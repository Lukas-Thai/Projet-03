package server.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import server.dto.RentalResponse;
import server.dto.RentalRequest;
import server.helper.InfoBuilder;
import server.model.Rentals;
import server.model.Users;
import server.services.RentalsService;
import server.services.UserService;



@RestController
@RequestMapping("/api/rentals")
public class RentalController {
	@Autowired
	private RentalsService RentalServe;
	@Autowired
	private UserService UserServe;
	@Autowired
	public RentalController(RentalsService rs, UserService us) {
		this.RentalServe = rs;
		this.UserServe= us;
	}
	@Operation(summary = "Fetch all rentals stored in the database")
	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping
    public ResponseEntity<Map<String,ArrayList<RentalResponse>>> getAllRentals(){
		 List<Rentals> rentList = this.RentalServe.getAllRentals();//on récupère tout les rentals en BD
		 ArrayList<RentalResponse> result = new ArrayList<>();
		 for(Rentals rent:rentList) {
			 result.add(InfoBuilder.rentalInfoBuilder(rent));// on formate les données pour chaque rental 
		 }
	 return ResponseEntity.ok(Map.of("rentals",result));//on retourne le tout
	}
	@Operation(summary = "Get data about a rental with its id")
	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/{id}")
	public ResponseEntity<?> getRentalById(@PathVariable Integer id){
		Rentals rent = this.RentalServe.getRentalsById(id).orElseGet(null);//on cherche si un rental existe avec l'id fourni
		if(rent==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","The id does not correspond to any existing rental"));
		}
		return ResponseEntity.ok(InfoBuilder.rentalInfoBuilder(rent));//on retourne les données formatées
	}
	@Operation(summary = "Allow the user to register a rental")
	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> createRental(
	    Authentication auth,
	    @RequestParam("name") String name,
	    @RequestParam("surface") Float surface,
	    @RequestParam("price") Float price,
	    @RequestParam("description") String description,
	    @RequestParam(value = "picture", required = false) MultipartFile picture  
	) {
	    String email = auth.getName();//récupèration de l'email de l'user avec son token
	    RentalRequest rent = new RentalRequest();//on construit une classe pour vérifier que les données sont correctes
	    rent.setName(name);
	    rent.setSurface(surface);
	    rent.setDescription(description);
	    rent.setPrice(price);
	    if (email.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
	    }

	    Users user = this.UserServe.findByEmail(email).orElse(null);//récupèration de l'user à partir de son email
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
	    }

	    if (rent == null || rent.getName() == null || rent.getSurface() == null || rent.getPrice() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the rent needs a name, a surface and a price"));
	    }
	    if(rent.getPrice()<0) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the price cannot be negative"));
	    }
	    if(rent.getSurface()<=0) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the surface must be higher than 0"));
	    }

	    this.RentalServe.registerRental(rent, user, picture);//enregistrement du rental

	    return ResponseEntity.ok(Map.of("message", "Rental created !"));
	}
	@Operation(summary = "Allow the user to update a rental")
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String,String>> updateRental(Authentication auth,
			@RequestParam("name") String name,
		    @RequestParam("surface") Float surface,
		    @RequestParam("price") Float price,
		    @RequestParam("description") String description,
		    @PathVariable Integer id){
    	String email = auth.getName();//récupèration de l'email de l'user avec son token
    	if(email=="") {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
    	}
    	Users user = this.UserServe.findByEmail(email).orElse(null);//récupèration de l'user avec son email
        if(user==null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
        }
        Rentals old = this.RentalServe.getRentalsById(id).orElseGet(null);//on vérifie que l'on modifie un rental déjà existant
        if(old == null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Rental not found"));
        }
        if(!old.getOwner().getEmail().trim().equals(email.trim())) {//vérification que l'user est bien le propriétaire du rental
        	System.out.println(old.getOwner().getEmail().trim());
        	System.out.println(email.trim());
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "You do not own this rental"));
        }
        RentalRequest rent = new RentalRequest();//vérification des données
        rent.setName(name);
        rent.setSurface(surface);
        rent.setPrice(price);
        rent.setDescription(description);
	    if (rent == null || rent.getName() == null || rent.getSurface() == null || rent.getPrice() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the rent needs a name, a surface and a price"));
	    }
	    if(rent.getPrice()<0) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the price cannot be negative"));
	    }
	    if(rent.getSurface()<=0) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "the surface must be higher than 0"));
	    }
        this.RentalServe.updateRentals(rent,old);//mise à jour du rental existant
        return ResponseEntity.ok(Map.of("message","Rental updated !"));
	}
}
