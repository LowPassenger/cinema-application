package cinema.dao.impl;

import cinema.dao.OrderDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.Order;
import cinema.model.User;
import cinema.util.HibernateUtil;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class OrderDaoImpl implements OrderDao {
    private static final Logger logger = LogManager.getLogger(OrderDaoImpl.class);

    @Override
    public Order add(Order order) {
        logger.info("Start add Order to DB. Params: order = {}", order);
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session open");
            transaction = session.beginTransaction();
            logger.debug("Transaction begin");
            session.save(order);
            logger.info("Order successfully added to DB. Params: order = {}", order);
            transaction.commit();
            logger.debug("Transaction commit");
            return order;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Oooops! Transaction rollback!");
            }
            logger.error("Can't add Order to DB. Params: order = {}", order);
            throw new DataProcessingException("Can't add order " + order, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }

    @Override
    public List<Order> getByUser(User user) {
        String query = "FROM Order o "
                + "LEFT JOIN FETCH o.tickets t "
                + "LEFT JOIN FETCH o.user u "
                + "LEFT JOIN FETCH t.movieSession ms "
                + "LEFT JOIN FETCH ms.movie "
                + "LEFT JOIN FETCH ms.cinemaHall "
                + "WHERE o.user = :user ";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> getOrderByUser = session.createQuery(query, Order.class);
            getOrderByUser.setParameter("user", user);
            return getOrderByUser.getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get Order by User " + user, e);
        }
    }
}
