package cinema.dao.impl;

import cinema.dao.UserDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.User;
import cinema.util.HibernateUtil;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class UserDaoImpl implements UserDao {
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public User add(User user) {
        logger.info("Start add User. Params: User = {}", user);
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session start");
            transaction = session.beginTransaction();
            logger.debug("Transaction begin");
            session.save(user);
            logger.info("User add sucessfully. Params: User = {}", user);
            transaction.commit();
            logger.debug("Transaction commit");
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Oooops! Transaction rollback!");
            }
            logger.error("Can't add User to DB. Params: User = {}", user);
            throw new DataProcessingException("Can't insert a user: " + user, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.info("Start find User by email. Params: email = {}", email);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User u "
                    + "WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            Optional<User> optionalFindUserByEmail = query.uniqueResultOptional();
            logger.info("User was successfully find by email. Params: User = {}",
                    optionalFindUserByEmail.get());
            return optionalFindUserByEmail;
        } catch (Exception e) {
            logger.error("Can't find User by email. Params: email = {}", email);
            throw new DataProcessingException("Can't find a user by email: " + email, e);
        }
    }
}
