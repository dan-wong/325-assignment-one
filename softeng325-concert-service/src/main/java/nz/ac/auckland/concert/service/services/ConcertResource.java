package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.service.domain.concert.Concert;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/concerts")
@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
public class ConcertResource {
	private EntityManager _em = PersistenceManager.instance().createEntityManager();

	/**
	 * Retrieves a representation of a Concert, identified by its unique ID.
	 * The HTTP response message should have a status code of either 200 or 404, depending on
	 * whether the specified Concert is found.
	 *
	 * @param id
	 * @return the concert corresponding to the id
	 */
	@GET
	@Path("{id}")
	public Response retrieveConcert(@PathParam("id") long id) {
		// Start a new transaction.
		_em.getTransaction().begin();

		Concert concert = _em.find(Concert.class, id);

		Response.ResponseBuilder rb = new ResponseBuilderImpl();
		if (concert == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			rb.entity(concert);
			rb.status(200);
		}

		_em.close();
		return rb.build();
	}

	/**
	 * Returns a Response containing all the concerts in the CONCERTS table
	 *
	 * @return a list of all the concerts
	 */
	@GET
	public Response getAllConcerts() {
		_em.getTransaction().begin();

		TypedQuery<Concert> concertQuery =
				_em.createQuery("SELECT c FROM CONCERTS c", Concert.class);
		List<Concert> concerts = concertQuery.getResultList();

		Response.ResponseBuilder rb = new ResponseBuilderImpl();
		if (concerts == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			rb.entity(concerts);
			rb.status(200);
		}

		_em.close();
		return rb.build();
	}

	/**
	 * Creates a Concert. The body of the HTTP request message contains
	 * a representation of the new Concert (less unique ID) to create. The service generates the
	 * Concert’s ID via the database, and returns a HTTP response of 201 with a Location header
	 * storing the URI for the newly created Concert.
	 */
	@POST
	public Response createConcert(Concert concert) {
		// Start a new transaction.
		_em.getTransaction().begin();

		//Persist the new concert
		_em.persist(concert);
		_em.getTransaction().commit();

		Response.ResponseBuilder rb = new ResponseBuilderImpl();
		rb.location(URI.create("/concerts/" + concert.getId()));
		rb.status(201);

		_em.close();
		return rb.build();
	}

	/**
	 * Updates an existing Concert. A representation of the modified Concert
	 * is stored in the body of the HTTP request message. Being an existing Concert that was
	 * earlier created by the Web service, it should include a unique ID value. The HTTP status
	 * code should be 204 on success, or 404 where the Concert isn’t known to the Web service.
	 *
	 * @param concert
	 * @return
	 */
	@PUT
	public Response updateConcert(Concert concert) {
		// Start a new transaction.
		_em.getTransaction().begin();

		Concert existingConcert = _em.find(Concert.class, concert.getId());
		if (existingConcert == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			_em.merge(concert);
			_em.getTransaction().commit();

			Response.ResponseBuilder rb = new ResponseBuilderImpl();
			rb.status(204);

			_em.close();
			return rb.build();
		}
	}

	/**
	 * Deletes a Concert, where the Concert to delete is specified by a
	 * unique ID. This operation returns either 204 or 404, depending on whether the Concert
	 * exists.
	 */
	@DELETE
	@Path("{id}")
	public Response deleteConcert(@PathParam("id") long id) {
		// Start a new transaction.
		_em.getTransaction().begin();

		Concert concert = _em.find(Concert.class, id);
		if (concert == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			_em.remove(concert);
			_em.getTransaction().commit();

			Response.ResponseBuilder rb = Response.noContent();
			_em.close();
			return rb.build();
		}
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
}
