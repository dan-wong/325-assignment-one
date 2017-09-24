package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.concert.*;
import nz.ac.auckland.concert.service.domain.user.CreditCard;
import nz.ac.auckland.concert.service.domain.user.Reservation;
import nz.ac.auckland.concert.service.domain.user.User;
import nz.ac.auckland.concert.service.mappers.*;
import nz.ac.auckland.concert.service.util.TheatreUtility;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/concerts")
@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
public class ConcertResource {
	private EntityManager _em = PersistenceManager.instance().createEntityManager();

	/**
	 * Returns a Response containing all the concerts in the CONCERTS table
	 *
	 * @return a list of all the concerts
	 */
	@GET
	public Response getAllConcerts() {
		try {
			_em.getTransaction().begin();

			TypedQuery<Concert> concertQuery =
					_em.createQuery("select c from Concert c", Concert.class);
			List<Concert> concerts = concertQuery.getResultList();

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			if (concerts == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				List<ConcertDTO> concertDTOS = concerts.stream()
						.map(ConcertMapper::convertToDTO)
						.collect(Collectors.toList());

				GenericEntity<List<ConcertDTO>> genericEntity = new GenericEntity<List<ConcertDTO>>(concertDTOS) {
				};

				rb.entity(genericEntity);
				rb.status(200);
			}

			return rb.build();
		} finally {
			_em.close();
		}
	}

	/**
	 * Returns a Response containing all the performers in the PERFORMERS table
	 *
	 * @return a list of all the concerts
	 */
	@GET
	@Path("/performers")
	public Response getAllPerformers() {
		try {
			_em.getTransaction().begin();

			TypedQuery<Performer> performerQuery =
					_em.createQuery("select p from Performer p", Performer.class);
			List<Performer> performers = performerQuery.getResultList();

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			if (performers == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				List<PerformerDTO> performerDTOS = performers.stream()
						.map(PerformerMapper::convertToDTO)
						.collect(Collectors.toList());

				GenericEntity<List<PerformerDTO>> genericEntity = new GenericEntity<List<PerformerDTO>>(performerDTOS) {
				};

				rb.entity(genericEntity);
				rb.status(200);
			}

			return rb.build();
		} finally {
			_em.close();
		}
	}

	@POST
	@Path("/user")
	public Response createUser(UserDTO userDTO) {
		try {
			_em.getTransaction().begin();

			User user = UserMapper.convertToModel(userDTO);

			//If the conversion returns a User object, that means the username is taken
			//Throw an exception
			if (user != null) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)
						.build());
			}

			//Check all fields are filled
			//If not throw an exception
			if (userDTO.getFirstname() == null || userDTO.getLastname() == null ||
					userDTO.getPassword() == null || userDTO.getUsername() == null) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.CREATE_USER_WITH_MISSING_FIELDS)
						.build());
			}

			user = new User(userDTO.getUsername(), userDTO.getPassword(), userDTO.getFirstname(), userDTO.getLastname());

			//Generate a random UUID and set the User field to that value
			UUID uuid = UUID.randomUUID();
			user.setUUID(uuid);

			//Create a new cookie with the UUID
			NewCookie cookie = makeCookie(uuid);

			_em.persist(user);
			_em.getTransaction().commit();

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.entity(userDTO);
			rb.cookie(cookie);
			rb.status(201);

			return rb.build();
		} finally {
			_em.close();
		}
	}

	@GET
	@Path("/user/{username}")
	public Response getUser(@PathParam("username") String username) {
		try {
			_em.getTransaction().begin();

			TypedQuery<User> userQuery =
					_em.createQuery("SELECT u FROM User AS u WHERE u._username = :username", User.class);
			userQuery.setParameter("username", username);
			List<User> userList = userQuery.setMaxResults(1).getResultList();

			User user = userList.size() == 0 ? null : userList.get(0);

			if (user == null) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.AUTHENTICATE_NON_EXISTENT_USER)
						.build());
			}

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.entity(UserMapper.convertToDTO(user));
			rb.cookie(makeCookie(user.getUUID()));
			rb.status(200);

			return rb.build();
		} finally {
			_em.close();
		}
	}

	@POST
	@Path("/user/creditcard")
	public Response registerCreditCard(@CookieParam("UUID") Cookie cookie, CreditCardDTO creditCardDTO) {
		try {
			_em.getTransaction().begin();

			CreditCard creditCard = CreditCardMapper.convertToModel(creditCardDTO);

			User user = authenticateUser(cookie);

			if (user == null) {
				throw new BadRequestException(Response
						.status(Response.Status.UNAUTHORIZED)
						.entity(Messages.AUTHENTICATE_NON_EXISTENT_USER)
						.build());
			}

			user.addCreditCard(creditCard);

			_em.persist(user);
			_em.getTransaction().commit();

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.status(204);

			return rb.build();
		} finally {
			_em.close();
		}
	}

	@GET
	@Path("/user")
	public Response getBookings(@CookieParam("UUID") Cookie cookie) {
		try {
			_em.getTransaction().begin();

			User user = authenticateUser(cookie);

			if (user == null) {
				throw new BadRequestException(Response
						.status(Response.Status.UNAUTHORIZED)
						.entity(Messages.AUTHENTICATE_NON_EXISTENT_USER)
						.build());
			}

			List<BookingDTO> bookingDTOS = user.getBookings().stream()
					.map(BookingMapper::convertToDTO)
					.collect(Collectors.toList());

			GenericEntity<List<BookingDTO>> genericEntity = new GenericEntity<List<BookingDTO>>(bookingDTOS) {
			};

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.entity(genericEntity);
			rb.status(200);

			return rb.build();
		} finally {
			_em.close();
		}
	}

	@POST
	@Path("/reservation")
	public Response createReservation(@CookieParam("UUID") Cookie cookie, ReservationRequestDTO reservationRequest) {
		try {
			_em.getTransaction().begin();

			//Check user authentication is OK
			User user = authenticateUser(cookie);

			if (user == null) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.BAD_AUTHENTICATON_TOKEN)
						.build());
			}

			int numberOfSeats = reservationRequest.getNumberOfSeats();
			PriceBand priceBand = reservationRequest.getSeatType();
			Long concertId = reservationRequest.getConcertId();
			LocalDateTime date = reservationRequest.getDate();

			//Find the corresponding ConcertSeats entity
			TypedQuery<ConcertSeats> concertSeatsQuery =
					_em.createQuery("SELECT c FROM ConcertSeats c WHERE c._id._concertId = :concertId AND c._id._date = :concertDate", ConcertSeats.class);
			concertSeatsQuery.setParameter("concertId", concertId);
			concertSeatsQuery.setParameter("concertDate", date);

			List<ConcertSeats> concertSeats = concertSeatsQuery.setMaxResults(1).getResultList();

			if (concertSeats.size() == 0) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
						.build());
			}

			Set<SeatDTO> bookedSeats = concertSeats.get(0).getBookedSeats().stream()
					.map(SeatMapper::convertToDTO)
					.collect(Collectors.toSet());

			Set<SeatDTO> reserveSeats = TheatreUtility.findAvailableSeats(numberOfSeats, priceBand, bookedSeats);
			if (reserveSeats.size() != numberOfSeats) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION)
						.build());
			}

			Set<Seat> seats = new HashSet<>();
			for (SeatDTO seat : reserveSeats) {
				TypedQuery<Seat> seatQuery =
						_em.createQuery("SELECT s FROM Seat s WHERE s._id._concertId = :concertId AND s._id._date = :date AND s._id._row = :row AND s._id._number = :number", Seat.class)
								.setLockMode(LockModeType.OPTIMISTIC);
				seatQuery.setParameter("concertId", concertId);
				seatQuery.setParameter("date", date);
				seatQuery.setParameter("row", seat.getRow());
				seatQuery.setParameter("number", seat.getNumber());

				seats.add(seatQuery.getSingleResult());
			}

			concertSeats.get(0).getAvailableSeats().removeAll(seats);
			concertSeats.get(0).getBookedSeats().addAll(seats);
			_em.persist(concertSeats.get(0));

			Reservation reservation = new Reservation(concertId, seats, date);
			_em.persist(reservation);
			_em.getTransaction().commit();

			ReservationDTO reservationDTO = new ReservationDTO(reservation.getId(), reservationRequest, reserveSeats);

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.entity(reservationDTO);
			rb.status(201);

			return rb.build();
		} finally {
			_em.close();
		}
	}

	@PUT
	@Path("/reservation")
	public Response confirmReservation(@CookieParam("UUID") Cookie cookie, ReservationDTO reservationDTO) {
		try {
			_em.getTransaction().begin();

			//Check user authentication is OK
			User user = authenticateUser(cookie);

			if (user == null) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.BAD_AUTHENTICATON_TOKEN)
						.build());
			} else if (user.getCreditCards().size() == 0) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.CREDIT_CARD_NOT_REGISTERED)
						.build());
			}

			//Get the reservation domain object
			Reservation reservation = _em.find(Reservation.class, reservationDTO.getId());

			if (System.currentTimeMillis() - reservation.getTimeOfRequest() > ConcertApplication.RESERVATION_EXPIRY_TIME_IN_SECONDS * 1000) {
				_em.remove(reservation);
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.EXPIRED_RESERVATION)
						.build());
			}

			Concert concert = _em.find(Concert.class, reservation.getConcertId());
			Booking booking = new Booking(concert, reservation.getDateOfConcert(), reservation.getSeats(), reservationDTO.getReservationRequest().getSeatType());

			user.addBooking(booking);
			_em.persist(user);

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.status(204);

			return rb.build();
		} finally {
			_em.close();
		}
	}

	private User authenticateUser(Cookie cookie) {
		if (cookie == null) {
			return null;
		}

		//Find a user associated with this cookie
		TypedQuery<User> userQuery =
				_em.createQuery("SELECT u FROM User u WHERE u._uuid = :uuid", User.class);
		userQuery.setParameter("uuid", getCookieValue(cookie));
		List<User> userList = userQuery.setMaxResults(1).getResultList();

		return userList.size() == 0 ? null : userList.get(0);
	}

	private UUID getCookieValue(Cookie cookie) {
		return UUID.fromString(cookie.getValue().substring(6));
	}

	private NewCookie makeCookie(UUID uuid) {
		return new NewCookie("UUID", uuid.toString());
	}
}
