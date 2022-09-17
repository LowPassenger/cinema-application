package cinema.service.impl;

import cinema.dao.MovieSessionDao;
import cinema.lib.Inject;
import cinema.lib.Service;
import cinema.model.MovieSession;
import cinema.service.MovieSessionService;
import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class MovieSessionServiceImpl implements MovieSessionService {
    private static final Logger logger = LogManager.getLogger(MovieServiceImpl.class);

    @Inject
    private MovieSessionDao sessionDao;

    @Override
    public List<MovieSession> findAvailableSessions(Long movieId, LocalDate date) {
        logger.debug("Start findAvailableSessions method. Params: movie id = {},"
                + "current date = {}", movieId, date);
        return sessionDao.findAvailableSessions(movieId, date);
    }

    @Override
    public MovieSession get(Long id) {
        logger.debug("Start get method. Params: id = {}", id);
        return sessionDao.get(id).get();
    }

    @Override
    public MovieSession add(MovieSession session) {
        logger.debug("Start add method. Params: movieSession = {}", session);
        return sessionDao.add(session);
    }
}
