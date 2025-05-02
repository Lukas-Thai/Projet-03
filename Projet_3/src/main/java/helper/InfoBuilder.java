package helper;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.Users;

public class InfoBuilder {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	public static Map<String,Object> rentalInfoBuilder(Rentals rent){
		Map<String,Object> currentRent = new HashMap<>();
		 currentRent.put("id", rent.getId());
		 currentRent.put("name", rent.getName());
		 currentRent.put("surface", rent.getSurface());
		 currentRent.put("price", rent.getPrice());
		 currentRent.put("picture", rent.getPicture());
		 currentRent.put("description", rent.getDescription());
		 currentRent.put("owner_id",rent.getOwner().getId());
		 currentRent.put("created_at", rent.getCreated_at().format(InfoBuilder.FORMATTER));
		 currentRent.put("updated_at", rent.getUpdated_at()!=null ? rent.getUpdated_at().format(InfoBuilder.FORMATTER) : "");
		 return currentRent;
	}
    public static Map<String, Object> userInfoBuilder(Users user){
    	Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("created_at", user.getCreated_at().format(InfoBuilder.FORMATTER));
        userInfo.put("updated_at", user.getUpdated_at()!=null ? user.getUpdated_at().format(InfoBuilder.FORMATTER) : "");
        return userInfo;
    }
}
