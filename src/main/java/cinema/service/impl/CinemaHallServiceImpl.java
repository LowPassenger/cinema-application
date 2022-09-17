package cinema.service.impl;

import cinema.dao.CinemaHallDao;
import cinema.lib.Inject;
import cinema.lib.Service;
import cinema.model.CinemaHall;
import cinema.service.CinemaHallService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CinemaHallServiceImpl implements CinemaHallService {
    private static final Logger logger = LogManager.getLogger(CinemaHallServiceImpl.class);

    @Inject
    private CinemaHallDao cinemaHallDao;

    @Override
    public CinemaHall add(CinemaHall cinemaHall) {
        logger.debug("Start add method. Params: cinemaHall = {}", cinemaHall);
        return cinemaHallDao.add(cinemaHall);
    }

    @Override
    public CinemaHall get(Long id) {
        logger.debug("Start get method. Params: cinemaHall id = {}", id);
        return cinemaHallDao.get(id).get();
    }

    @Override
    public List<CinemaHall> getAll() {
        logger.debug("Start getAll method.");
        return cinemaHallDao.getAll();
    }
}
