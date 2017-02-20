package db.setup;

import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.model.SomeEntity;

/**
 * Session Bean implementation class StartEJB
 */
@Singleton
@Startup
public class CreateDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateDatabase.class);

    /**
     * A entity manager where commits can safely be done without the container complaining that the transactions are
     * managed by contianer. It allows for the creation of the DB schema. Uses a NON XA NON CONTAINER MANAGED entity
     * manager.
     */
    // @PersistenceContext(unitName = "CreateDbPU")
    // private EntityManager em;

    @PersistenceUnit(unitName = "CreateDbPU")
    private EntityManagerFactory emf;

    @PostConstruct
    public void startTestcase() {
        String canonicalNameToEntity = SomeEntity.class.getCanonicalName();
        String path = "/" + canonicalNameToEntity.replace(".", "/") + ".class";
        URL pathToEntity = this.getClass().getResource(path);
        LOGGER.info("Path: {} ", pathToEntity);
        LOGGER.info("START: -------------------------------------");
        LOGGER.info(
                "The create database singleton simply injecting into itself the CREATE DB PU. By doing this the persistence.xml is tuned with the recreate schema should create all necessary DB tables.");
        EntityManager em = null;
        try {
            // (a) create an entity magager
            em = emf.createEntityManager();

            // (b) flush the entity manager
            em.getFlushMode();
            // (c) verfify our db schema got created
            SomeEntity entity;
            List<Long> result = em.createQuery("SELECT count(e) FROM SomeEntity e").getResultList();
            LOGGER.info("Total number of entities is {} ", result);
        } catch (Exception e) {
            LOGGER.error("Unexpected error took place as a result of doing a dummy entity manager getFlushMode.", e);
            throw new RuntimeException(
                    "Unexpected error took place as a result of doing a dummy entity manager flush call", e);
        } finally {
            em.close();
            LOGGER.info("END: -------------------------------------");
        }
    }

    /**
     * Default constructor.
     */
    public CreateDatabase() {
    }

}
