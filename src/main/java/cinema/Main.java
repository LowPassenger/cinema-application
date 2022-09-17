package cinema;

import cinema.lib.Injector;
import cinema.model.CinemaHall;
import cinema.model.Movie;
import cinema.model.MovieSession;
import cinema.model.Order;
import cinema.model.ShoppingCart;
import cinema.model.User;
import cinema.service.CinemaHallService;
import cinema.service.MovieService;
import cinema.service.MovieSessionService;
import cinema.service.OrderService;
import cinema.service.ShoppingCartService;
import cinema.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    private static final String INJECTOR_INSTANCE = "cinema";
    private static final Injector injector = Injector.getInstance(INJECTOR_INSTANCE);

    public static void main(String[] args) {
        MovieService movieService = (MovieService) injector.getInstance(MovieService.class);

        Movie fastAndFurious = new Movie("Fast and Furious");
        fastAndFurious.setDescription("An action film about street racing, heists, and spies.");
        Movie alien = new Movie("Alien");
        alien.setDescription("The best space monster movie.");
        movieService.add(fastAndFurious);
        movieService.add(alien);
        System.out.println(movieService.get(fastAndFurious.getId()));
        System.out.println(movieService.get(alien.getId()));
        movieService.getAll().forEach(System.out::println);

        CinemaHall firstCinemaHall = new CinemaHall();
        firstCinemaHall.setCapacity(100);
        firstCinemaHall.setDescription("first hall with capacity 100");

        CinemaHall secondCinemaHall = new CinemaHall();
        secondCinemaHall.setCapacity(200);
        secondCinemaHall.setDescription("second hall with capacity 200");

        CinemaHallService cinemaHallService = (CinemaHallService) injector
                .getInstance(CinemaHallService.class);
        cinemaHallService.add(firstCinemaHall);
        cinemaHallService.add(secondCinemaHall);

        System.out.println(cinemaHallService.getAll());
        System.out.println(cinemaHallService.get(firstCinemaHall.getId()));

        MovieSession tomorrowMovieSession = new MovieSession();
        tomorrowMovieSession.setCinemaHall(firstCinemaHall);
        tomorrowMovieSession.setMovie(fastAndFurious);
        tomorrowMovieSession.setShowTime(LocalDateTime.now().plusDays(1L));

        MovieSession yesterdayMovieSession = new MovieSession();
        yesterdayMovieSession.setCinemaHall(firstCinemaHall);
        yesterdayMovieSession.setMovie(fastAndFurious);
        yesterdayMovieSession.setShowTime(LocalDateTime.now().minusDays(1L));

        MovieSession alienTomorrowMovieSession = new MovieSession();
        alienTomorrowMovieSession.setCinemaHall(secondCinemaHall);
        alienTomorrowMovieSession.setMovie(alien);
        alienTomorrowMovieSession.setShowTime(LocalDateTime.now().plusDays(1L));

        MovieSession alienYesterdayMovieSession = new MovieSession();
        alienYesterdayMovieSession.setCinemaHall(secondCinemaHall);
        alienYesterdayMovieSession.setMovie(alien);
        alienYesterdayMovieSession.setShowTime(LocalDateTime.now().minusDays(1L));

        MovieSessionService movieSessionService = (MovieSessionService) injector
                .getInstance(MovieSessionService.class);
        movieSessionService.add(tomorrowMovieSession);
        movieSessionService.add(yesterdayMovieSession);
        movieSessionService.add(alienTomorrowMovieSession);
        movieSessionService.add(alienYesterdayMovieSession);

        System.out.println(movieSessionService.get(yesterdayMovieSession.getId()));
        System.out.println(movieSessionService.findAvailableSessions(
                        fastAndFurious.getId(), LocalDate.now()));

        User user = new User();
        user.setPassword("goodPassword");
        user.setEmail("user@gmail.com");
        UserService userService = (UserService) injector.getInstance(UserService.class);
        userService.add(user);
        System.out.println(userService.findByEmail("user@gmail.com").get());

        ShoppingCartService shoppingCartService = (ShoppingCartService) injector
                .getInstance(ShoppingCartService.class);
        shoppingCartService.registerNewShoppingCart(user);
        shoppingCartService.addSession(yesterdayMovieSession, user);
        shoppingCartService.addSession(tomorrowMovieSession, user);
        ShoppingCart currentUserShoppingCart = shoppingCartService.getByUser(user);
        System.out.println(currentUserShoppingCart);

        OrderService orderService = (OrderService) injector.getInstance(OrderService.class);
        Order currentOrder = orderService.completeOrder(currentUserShoppingCart);
        System.out.println(currentOrder);
        shoppingCartService.clearShoppingCart(currentUserShoppingCart);
        shoppingCartService.addSession(alienTomorrowMovieSession, user);
        shoppingCartService.addSession(alienYesterdayMovieSession, user);
        orderService.completeOrder(currentUserShoppingCart);
        System.out.println(orderService.getOrdersHistory(user));
    }
}
