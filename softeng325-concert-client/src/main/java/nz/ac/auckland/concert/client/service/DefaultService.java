package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultService implements ConcertService {
	private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			// Make an invocation on a Concert URI and specify Java-
			// serialization as the required data format.
			Builder builder = client.target(WEB_SERVICE_URI).request();

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.get();

			// Check that the HTTP response code is 200 OK.
			int responseCode = response.getStatus();

			if (responseCode == 200) {
				// Retrieve the list of concerts
				ArrayList<ConcertDTO> concerts = response
						.readEntity(new GenericType<ArrayList<ConcertDTO>>() {
						});

				return new HashSet<>(concerts);
			} else {
				throw new ServiceException(responseCode + "");
			}
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			// Make an invocation on a Concert URI and specify Java-
			// serialization as the required data format.
			Builder builder = client.target(WEB_SERVICE_URI + "/performers").request();

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.get();

			// Check that the HTTP response code is 200 OK.
			int responseCode = response.getStatus();

			if (responseCode == 200) {
				// Retrieve the list of concerts
				List<PerformerDTO> performers = response
						.readEntity(new GenericType<List<PerformerDTO>>() {
						});

				return new HashSet<>(performers);
			} else {
				throw new ServiceException(responseCode + "");
			}
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

}
