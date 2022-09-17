package cinema.dao.impl;

import cinema.dao.CinemaHallDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.CinemaHall;
import cinema.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class CinemaHallDaoImpl implements CinemaHallDao {
    private static final Logger logger = LogManager.getLogger(CinemaHallDaoImpl.class);

    @Override
    public CinemaHall add(CinemaHall cinemaHall) {
        logger.info("Start add CinemaHall. Params: cinemaHall = {}", cinemaHall);
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session start");
            transaction = session.beginTransaction();
            logger.debug("Transaction start");
            session.save(cinemaHall);
            logger.info("CinemaHall successfully added to DB. Params: cinemaHall = {}",
                    cinemaHall);
            transaction.commit();
            logger.debug("Transaction commit");
            return cinemaHall;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Ooops! Transaction rollback!");
            }
            logger.error("Can't insert a cinemaHall. Params: cinemaHall = {}", cinemaHall);
            throw new DataProcessingException("Can't insert a cinema hall: " + cinemaHall, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Close session!");
            }
        }
    }

    @Override
    public Optional<CinemaHall> get(Long id) {
        logger.info("Start get CinemaHall. Params: cinemaHall id = {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<CinemaHall> cinemaHallFromDb = Optional
                    .ofNullable(session.get(CinemaHall.class, id));
            logger.info("Get cinemaHall from DB. Params: cinemaHall = {}", cinemaHallFromDb);
            return cinemaHallFromDb;
        } catch (Exception e) {
            logger.error("Can't get a cinemaHall. Params: cinemaHall id = {}", id);
            throw new DataProcessingException("Can't get a cinema hall by id: " + id, e);
        }
    }

    @Override
    public List<CinemaHall> getAll() {
        logger.info("Start get all cinemaHalls from DB");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaQuery<CinemaHall> criteriaQuery = session.getCriteriaBuilder()
                    .createQuery(CinemaHall.class);
            criteriaQuery.from(CinemaHall.class);
            List<CinemaHall> resultListFromDb = session
                    .createQuery(criteriaQuery).getResultList();
            logger.info("Get resultList from DB. Params: list of cinemaHalls = {}",
                    resultListFromDb);
            return resultListFromDb;
        } catch (Exception e) {
            logger.error("Can't get all cinema halls");
            throw new DataProcessingException("Can't get all cinema halls", e);
        }
    }
}
