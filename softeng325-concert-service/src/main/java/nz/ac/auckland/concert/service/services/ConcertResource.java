package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.concert.Concert;
import nz.ac.auckland.concert.service.domain.concert.Performer;
import nz.ac.auckland.concert.service.domain.user.CreditCard;
import nz.ac.auckland.concert.service.domain.user.User;
import nz.ac.auckland.concert.service.mappers.ConcertMapper;
import nz.ac.auckland.concert.service.mappers.CreditCardMapper;
import nz.ac.auckland.concert.service.mappers.PerformerMapper;
import nz.ac.auckland.concert.service.mappers.UserMapper;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.List;
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

		//Find a user associated with this cookie
		TypedQuery<User> userQuery =
				_em.createQuery("SELECT u FROM User AS u WHERE u._uuid = :uuid", User.class);
		userQuery.setParameter("uuid", UUID.fromString(cookie.getValue().substring(6)));
		List<User> userList = userQuery.setMaxResults(1).getResultList();

		User user = userList.size() == 0 ? null : userList.get(0);

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

	/**
	 * Deletes all Concerts, and returns a 204 status code.
	 *
	 * @return
	 */
	@DELETE
	public Response deleteAllConcerts() {
		// Start a new transaction.
		_em.getTransaction().begin();

		TypedQuery<Concert> concertQuery =
				_em.createQuery("select c from Concert c", Concert.class);
		List<Concert> concerts = concertQuery.getResultList();

		for (Concert concert : concerts) {
			_em.remove(concert);
		}

		Response.ResponseBuilder rb = new ResponseBuilderImpl();
		rb.status(204);

		_em.close();
		return rb.build();
	}

	private NewCookie makeCookie(UUID uuid) {
		return new NewCookie("UUID", uuid.toString());
	}
}
