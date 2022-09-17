package cinema.security;

import cinema.exception.AuthenticationException;
import cinema.exception.RegistrationException;
import cinema.lib.Inject;
import cinema.lib.Service;
import cinema.model.User;
import cinema.service.ShoppingCartService;
import cinema.service.UserService;
import cinema.util.HashUtil;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LogManager.getLogger(AuthenticationServiceImpl.class);

    @Inject
    private UserService userService;
    @Inject
    private ShoppingCartService shoppingCartService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        logger.info("Start login method. Params: email = {}, password", email);
        Optional<User> optionalUserFromDb = userService.findByEmail(email);
        if (optionalUserFromDb.isPresent() && matchPasswords(password, optionalUserFromDb.get())) {
            User userFromDb = optionalUserFromDb.get();
            logger.info("User was successfully log in. Params: email = {}, password = OK",
                    email);
            return userFromDb;
        }
        logger.error("Email or password was incorrect! Params: email = {}, passord = OK", email);
        throw new AuthenticationException("Incorrect email or password!");
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        logger.info("Start register method. Params: email = {}, password", email);
        if (userService.findByEmail(email).isPresent()) {
            logger.error("User already exist! Params: email = {}", email);
            throw new RegistrationException("User with email " + email
                    + " is already registered!");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userService.add(user);
        shoppingCartService.registerNewShoppingCart(user);
        logger.info("User was successfully registered. Params: User = {}", user);
        return user;
    }

    private boolean matchPasswords(String rawPassword, User userFromDb) {
        String hashedPassword = HashUtil.hashPassword(rawPassword, userFromDb.getSalt());
        return hashedPassword.equals(userFromDb.getPassword());
    }
}
