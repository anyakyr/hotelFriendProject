package businessobjects;

import lombok.Data;
import lombok.ToString;
import utility.PropertyReader;

@Data
@ToString
public class User {

    private String password;
    private String email;


    public User(String fileLocation) {
        PropertyReader propertyReader = new PropertyReader(fileLocation);
        this.email = propertyReader.getValue("email");
        this.password = propertyReader.getValue("password");

    }
}