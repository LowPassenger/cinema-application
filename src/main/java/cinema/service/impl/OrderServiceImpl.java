package cinema.service.impl;

import cinema.dao.OrderDao;
import cinema.lib.Inject;
import cinema.lib.Service;
import cinema.model.Order;
import cinema.model.ShoppingCart;
import cinema.model.User;
import cinema.service.OrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    @Inject
    private OrderDao orderDao;

    @Override
    public Order completeOrder(ShoppingCart shoppingCart) {
        logger.debug("Start completeOrder method. Params: shoppingCart = {}", shoppingCart);
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setTickets(new ArrayList<>(shoppingCart.getTickets()));
        order.setOrderDate(LocalDateTime.now());
        orderDao.add(order);
        logger.debug("Order complete. Params: shoppingCart = {}", shoppingCart);
        return order;
    }

    @Override
    public List<Order> getOrdersHistory(User user) {
        logger.debug("Start getOrdersHistory methods. Params: User = {}", user);
        return orderDao.getByUser(user);
    }
}
