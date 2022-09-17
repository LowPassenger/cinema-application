package cinema.dao.impl;

import cinema.dao.MovieDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.Movie;
import cinema.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class MovieDaoImpl implements MovieDao {
    private static final Logger logger = LogManager.getLogger(MovieDaoImpl.class);

    @Override
    public Movie add(Movie movie) {
        logger.info("Start add movie. Params movie = {}", movie);
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session start");
            transaction = session.beginTransaction();
            logger.debug("Transaction start");
            session.save(movie);
            logger.info("Movie successfully added to DB. Params: movie = {}", movie);
            transaction.commit();
            logger.debug("Transaction commit");
            return movie;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Oooops! Transaction rollback");
            }
            logger.error("Can't insert movie. Params: movie = {}", movie);
            throw new DataProcessingException("Can't insert a movie: " + movie, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }

    @Override
    public Optional<Movie> get(Long id) {
        logger.info("Start get movie. Params: movie id = {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<Movie> movieFromDB = Optional.ofNullable(session.get(Movie.class, id));
            logger.info("Get movie from DB. Params: movie = {}", movieFromDB);
            return movieFromDB;
        } catch (Exception e) {
            logger.error("Can't get movie from DB. Params: movie id = {}", id);
            throw new DataProcessingException("Can't get a movie by id: " + id, e);
        }
    }

    @Override
    public List<Movie> getAll() {
        logger.info("Start get all movies from DB");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaQuery<Movie> criteriaQuery = session.getCriteriaBuilder()
                    .createQuery(Movie.class);
            criteriaQuery.from(Movie.class);
            List<Movie> moviesFromDb = session.createQuery(criteriaQuery).getResultList();
            logger.info("Get all movies from DB. Params: resultList = {}", moviesFromDb);
            return moviesFromDb;
        } catch (Exception e) {
            logger.error("Can't get all movies from DB!");
            throw new DataProcessingException("Can't get all movies", e);
        }
    }
}
