package airline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserManager {
    private List<User> users = Collections.synchronizedList(new ArrayList<>());

    public UserManager() {
        users.add(new User("Demo User", "demo@fly.com", "Demo@123", "9876543210"));
    }

    public synchronized boolean register(String name, String email, String password, String phone) {

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }

        users.add(new User(name, email, password, phone));
        return true;
    }

    public synchronized User login(String email, String password) {

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)
                    && u.getPassword().equals(password)) {
                return u;
            }
        }

        return null;
    }
}
