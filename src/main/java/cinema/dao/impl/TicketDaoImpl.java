package cinema.dao.impl;

import cinema.dao.TicketDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.Ticket;
import cinema.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class TicketDaoImpl implements TicketDao {
    private static final Logger logger = LogManager.getLogger(TicketDaoImpl.class);

    @Override
    public Ticket add(Ticket ticket) {
        logger.info("Start add ticket to DB. Params: ticket = {}", ticket);
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            logger.debug("Session open");
            transaction = session.beginTransaction();
            logger.debug("Transaction open");
            session.save(ticket);
            transaction.commit();
            logger.debug("Transaction commit");
            return ticket;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.debug("Ooops! Transaction rollback!");
            }
            throw new DataProcessingException("Can't insert a ticket: " + ticket, e);
        } finally {
            if (session != null) {
                session.close();
                logger.debug("Session close!");
            }
        }
    }
}
