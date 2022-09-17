package cinema.dao.impl;

import cinema.dao.ShoppingCartDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.ShoppingCart;
import cinema.model.User;
import cinema.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class ShoppingCartDaoImpl implements ShoppingCartDao {
    private static final Logger logger = LogManager.getLogger(ShoppingCartDaoImpl.class);

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        logger.info("Start add shoppingCart to DB. Params: shoppingCart = {}", shoppingCart);
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session open");
            transaction = session.beginTransaction();
            logger.debug("Transaction begin");
            session.save(shoppingCart);
            logger.info("ShoppingCart sucessfully added to DB. Params: shoppingCart = {}",
                    shoppingCart);
            transaction.commit();
            logger.debug("Transaction commit");
            return shoppingCart;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Ooooops! Transaction rollback!");
            }
            logger.error("Can't add shoppingCart to DB. Params: shoppingCart = {}", shoppingCart);
            throw new DataProcessingException("Can't insert a shopping cart: " + shoppingCart, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }

    @Override
    public ShoppingCart getByUser(User user) {
        logger.info("Start get shoppingCart by user. Params: user = {}", user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ShoppingCart> query = session.createQuery("FROM ShoppingCart sc "
                    + "LEFT JOIN FETCH sc.tickets t "
                    + "LEFT JOIN FETCH t.movieSession ms "
                    + "LEFT JOIN FETCH ms.movie "
                    + "LEFT JOIN FETCH ms.cinemaHall "
                    + "WHERE sc.user =:user", ShoppingCart.class);
            query.setParameter("user", user);
            ShoppingCart shoppingCartFromDbByUser = query.uniqueResult();
            logger.info("Get shoppingCart by User. Params: shoppingCart = {}",
                    shoppingCartFromDbByUser);
            return shoppingCartFromDbByUser;
        } catch (Exception e) {
            logger.error("Can't get shoppingCart by User. Params: User = {}", user);
            throw new DataProcessingException("Can't find a shopping cart by user: " + user, e);
        }
    }

    @Override
    public void update(ShoppingCart shoppingCart) {
        logger.info("Start update shoppingCart. Params: shoppingCart = {}", shoppingCart);
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session open");
            transaction = session.beginTransaction();
            logger.debug("Transaction begin");
            session.update(shoppingCart);
            transaction.commit();
            logger.debug("Transaction commit");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Oooops! Transaction rollback!");
            }
            logger.error("Can't update shopping cart. Params: shoppingCart = {}", shoppingCart);
            throw new DataProcessingException("Can't update a shopping cart: " + shoppingCart, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }
}
