package cinema.service.impl;

import cinema.dao.MovieDao;
import cinema.lib.Inject;
import cinema.lib.Service;
import cinema.model.Movie;
import cinema.service.MovieService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class MovieServiceImpl implements MovieService {
    private static final Logger logger = LogManager.getLogger(MovieServiceImpl.class);

    @Inject
    private MovieDao movieDao;

    @Override
    public Movie add(Movie movie) {
        logger.debug("Start add method. Params: movie = {}", movie);
        return movieDao.add(movie);
    }

    @Override
    public Movie get(Long id) {
        logger.debug("Start get method. Params: movie id = {}", id);
        return movieDao.get(id).get();
    }

    @Override
    public List<Movie> getAll() {
        logger.debug("Start getAll method");
        return movieDao.getAll();
    }
}
