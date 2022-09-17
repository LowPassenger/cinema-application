package cinema.dao.impl;

import cinema.dao.MovieSessionDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.MovieSession;
import cinema.util.HibernateUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class MovieSessionDaoImpl implements MovieSessionDao {
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59);

    private static final Logger logger = LogManager.getLogger(MovieSessionDaoImpl.class);

    @Override
    public MovieSession add(MovieSession movieSession) {
        logger.info("Start add movieSession. Params: movieSession = {}", movieSession);
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session open");
            transaction = session.beginTransaction();
            logger.debug("Transaction begin");
            session.save(movieSession);
            logger.info("MovieSession successfully added. Params: movieSession = {}",
                    movieSession);
            transaction.commit();
            logger.debug("Transaction commit");
            return movieSession;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Oooops! Transaction rollback");
            }
            logger.error("Can't add movieSession. Params: movieSession = {}", movieSession);
            throw new DataProcessingException("Can't insert a movie session: " + movieSession, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }

    @Override
    public List<MovieSession> findAvailableSessions(Long movieId, LocalDate date) {
        logger.info("Start find of available sessions. Params: movie id = {}, "
                + "for date = {}", movieId, date);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<MovieSession> criteriaQuery =
                    criteriaBuilder.createQuery(MovieSession.class);
            Root<MovieSession> root = criteriaQuery.from(MovieSession.class);
            Predicate moviePredicate = criteriaBuilder.equal(root.get("movie"), movieId);
            Predicate datePredicate = criteriaBuilder.between(root.get("showTime"),
                    date.atStartOfDay(), date.atTime(END_OF_DAY));
            Predicate allConditions = criteriaBuilder.and(moviePredicate, datePredicate);
            criteriaQuery.select(root).where(allConditions);
            root.fetch("movie");
            root.fetch("cinemaHall");
            List<MovieSession> availableSessionsList = session.createQuery(criteriaQuery)
                    .getResultList();
            logger.info("Get all sessions for movie. Params: movie id = {}, for date = {},"
                    + " available sessions = {}", movieId, date, availableSessionsList);
            return availableSessionsList;
        } catch (Exception e) {
            logger.error("Can't get all sessions for movie. Params: movie id = {}, "
                    + "for date = {}", movieId, date);
            throw new DataProcessingException("Can't get available sessions for movie with id: "
                    + movieId + " for date: " + date, e);
        }
    }

    @Override
    public Optional<MovieSession> get(Long id) {
        logger.info("Start get movieSession. Params: movieSession id = {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MovieSession> query = session.createQuery("FROM MovieSession ms "
                    + "JOIN FETCH ms.movie m "
                    + "JOIN FETCH ms.cinemaHall ch "
                    + "WHERE ms.id = :id", MovieSession.class);
            query.setParameter("id", id);
            Optional<MovieSession> movieSessionFromDb = query.uniqueResultOptional();
            logger.info("Get movieSession from DB. Params: movieSession = {}", movieSessionFromDb);
            return movieSessionFromDb;
        } catch (Exception e) {
            logger.error("Can't get movieSession from DB. Params:"
                    + " movieSession id = {}", id);
            throw new DataProcessingException("Can't get a movie session by id: " + id, e);
        }
    }
}
