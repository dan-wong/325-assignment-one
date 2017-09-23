package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;
import nz.ac.auckland.concert.service.domain.concert.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JAX-RS Application subclass for the Concert Web service.
 * 
 * 
 *
 */
@ApplicationPath("/services")
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;

	private Set<Class<?>> _classes = new HashSet<Class<?>>();
	private Set<Object> _singletons = new HashSet<Object>();

	public ConcertApplication() {
		_singletons.add(new PersistenceManager());
		_classes.add(ConcertResource.class);

		EntityManager em = null;

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			//Delete stuff

			//Get all the concerts and generate seats
			TypedQuery<Concert> concertQuery =
					em.createQuery("SELECT c FROM Concert c", Concert.class);
			List<Concert> concertList = concertQuery.getResultList();

			for (Concert concert : concertList) {
				for (LocalDateTime date : concert.getDates()) {
					Set<Seat> availableSeats = generateSeats(concert.getId(), date);
					ConcertSeats concertSeats = new ConcertSeats(new ConcertSeatsKey(concert.getId(), date), availableSeats);

					em.persist(concertSeats);
				}

				//Flush persistence context
				em.flush();
				em.clear();
			}

			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	@Override
	public Set<Object> getSingletons() {
		return _singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return _classes;
	}

	private Set<Seat> generateSeats(Long concertId, LocalDateTime date) {
		Set<Seat> availableSeats = new HashSet<>();
		for (SeatRow row : SeatRow.values()) {
			for (int i = 1; i <= TheatreLayout.getNumberOfSeatsForRow(row); i++) {
				Seat seat = new Seat(new SeatKey(row, new SeatNumber(i), concertId, date));
				availableSeats.add(seat);
			}
		}

		return availableSeats;
	}
}
