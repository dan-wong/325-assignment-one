package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;
import nz.ac.auckland.concert.service.domain.concert.Concert;
import nz.ac.auckland.concert.service.domain.concert.Performer;
import nz.ac.auckland.concert.service.domain.concert.Seat;
import nz.ac.auckland.concert.service.domain.concert.SeatKey;
import nz.ac.auckland.concert.service.domain.user.CreditCard;
import nz.ac.auckland.concert.service.domain.user.Reservation;
import nz.ac.auckland.concert.service.domain.user.User;
import nz.ac.auckland.concert.service.mappers.*;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.persistence.EntityManager;
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

		_em.close();
		return rb.build();
	}

	/**
	 * Returns a Response containing all the performers in the PERFORMERS table
	 *
	 * @return a list of all the concerts
	 */
	@GET
	@Path("/performers")
	public Response getAllPerformers() {
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

		_em.close();
		return rb.build();
	}

	@POST
	@Path("/user")
	public Response createUser(UserDTO userDTO) {
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

		_em.getTransaction().begin();
		_em.persist(user);
		_em.getTransaction().commit();

		Response.ResponseBuilder rb = new ResponseBuilderImpl();
		rb.entity(userDTO);
		rb.cookie(cookie);
		rb.status(201);

		_em.close();
		return rb.build();
	}

	@GET
	@Path("/user/{username}")
	public Response getUser(@PathParam("username") String username) {
		_em.getTransaction().begin();

		TypedQuery<User> userQuery =
				_em.createQuery("SELECT u FROM User AS u WHERE u._username = :username", User.class);
		userQuery.setParameter("username", username);
		List<User> userList = userQuery.setMaxResults(1).getResultList();

		_em.close();

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
	}

	@POST
	@Path("/user/creditcard")
	public Response registerCreditCard(@CookieParam("UUID") Cookie cookie, CreditCardDTO creditCardDTO) {
		CreditCard creditCard = CreditCardMapper.convertToModel(creditCardDTO);

		_em.getTransaction().begin();

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

		_em.close();
		return rb.build();
	}

	@GET
	@Path("/user")
	public Response getBookings(@CookieParam("UUID") Cookie cookie) {
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
		rb.entity(bookingDTOS);
		rb.status(200);

		_em.close();
		return rb.build();
	}

	@POST
	@Path("/reservation")
	public Response createReservation(@CookieParam("UUID") Cookie cookie, ReservationRequestDTO reservationRequest) {
		_em.getTransaction().begin();

		//Check user authentication is OK
		User user = authenticateUser(cookie);

		if (user == null) {
			throw new BadRequestException(Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(Messages.BAD_AUTHENTICATON_TOKEN)
					.build());
		}

		int numberOfSeats = reservationRequest.getNumberOfSeats();
		PriceBand priceBand = reservationRequest.getSeatType();
		Long concertId = reservationRequest.getConcertId();
		LocalDateTime date = reservationRequest.getDate();

		//Check if a concert exists at this time
		TypedQuery<Concert> concertQuery =
				_em.createQuery("SELECT c FROM Concert c WHERE c._id = :id", Concert.class);
		concertQuery.setParameter("id", concertId);
		List<Concert> concerts = concertQuery.getResultList();

		if (concerts != null) {
			if (!concerts.get(0).getDates().contains(date)) {
				throw new BadRequestException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
						.build());
			}
		} else {
			throw new BadRequestException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
					.build());
		}

		//Create a Set of seats that are to be reserved
		Set<Seat> reservationSeats = new HashSet<>();

		//Get all the reserved seats for this particular concert
		TypedQuery<Seat> seatQuery =
				_em.createQuery("SELECT s FROM Seat s WHERE s._id._concertId = :concertId AND s._id._date = :date", Seat.class);
		seatQuery.setParameter("concertId", concertId);
		seatQuery.setParameter("date", date);
		List<Seat> seats = seatQuery.getResultList(); //List of taken seats for the concert

		Set<SeatRow> rows = TheatreLayout.getRowsForPriceBand(priceBand);

		outer_loop:
		for (SeatRow row : rows) {
			int seatsForRow = TheatreLayout.getNumberOfSeatsForRow(row);
			for (int i = 0; i < seatsForRow; i++) {
				Seat seat = new Seat(new SeatKey(row, new SeatNumber(i), concertId, date));

				if (!seats.contains(seat)) {
					reservationSeats.add(seat);
				}

				if (reservationSeats.size() == numberOfSeats) {
					break outer_loop;
				}
			}
		}

		//Check if seats have been found, if not throw exception
		if (reservationSeats.size() != numberOfSeats) {
			throw new BadRequestException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION)
					.build());
		}

		Reservation reservation = new Reservation(reservationSeats, concertId, date);
		_em.persist(reservation);

		Set<SeatDTO> reservationSeatsDTO = reservationSeats.stream()
				.map(SeatMapper::convertToDTO)
				.collect(Collectors.toSet());

		ReservationDTO reservationDTO = new ReservationDTO(reservation.getId(), reservationRequest, reservationSeatsDTO);
		Response.ResponseBuilder rb = new ResponseBuilderImpl();
		rb.entity(reservationDTO);
		rb.status(201);

		_em.close();
		return rb.build();
	}

	private User authenticateUser(Cookie cookie) {
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
