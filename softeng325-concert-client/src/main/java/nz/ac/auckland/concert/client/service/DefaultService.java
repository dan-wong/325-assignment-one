package nz.ac.auckland.concert.client.service;

import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DefaultService implements ConcertService {
	private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";
	private NewCookie _cookie = null;

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			Builder builder = client.target(WEB_SERVICE_URI).request();
			response = builder.get();

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 200:
					// Retrieve the list of concerts
					ArrayList<ConcertDTO> concerts = response
							.readEntity(new GenericType<ArrayList<ConcertDTO>>() {
							});

					return new HashSet<>(concerts);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
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
			Builder builder = client.target(WEB_SERVICE_URI + "/performers").request();
			response = builder.get();

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 200:
					// Retrieve the list of concerts
					List<PerformerDTO> performers = response.readEntity(new GenericType<List<PerformerDTO>>() {
					});

					return new HashSet<>(performers);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public UserDTO createUser(UserDTO newUser) {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			Builder builder = client.target(WEB_SERVICE_URI + "/user").request();
			response = builder.post(Entity.entity(newUser, javax.ws.rs.core.MediaType.APPLICATION_XML));

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 201:
					saveCookie(response);
					return response.readEntity(UserDTO.class);
				case 400:
					String message = response.readEntity(String.class);
					if (message.equals(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)) {
						throw new ServiceException(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME);
					} else {
						throw new ServiceException(Messages.CREATE_USER_WITH_MISSING_FIELDS);
					}
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		if (user.getUsername() == null || user.getPassword() == null) {
			throw new ServiceException(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS);
		}

		try {
			Builder builder = client.target(WEB_SERVICE_URI + "/user/" + user.getUsername()).request();
			response = builder.get();

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 200:
					UserDTO retrievedUser = response.readEntity(UserDTO.class);

					if (retrievedUser.getUsername().equals(user.getUsername()) && retrievedUser.getPassword().equals(user.getPassword())) {
						saveCookie(response);
						return retrievedUser;
					} else {
						throw new ServiceException(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD);
					}
				case 400:
					String message = response.readEntity(String.class);
					if (message.equals(Messages.AUTHENTICATE_NON_EXISTENT_USER)) {
						throw new ServiceException(Messages.AUTHENTICATE_NON_EXISTENT_USER);
					}
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			Builder builder = client.target(WEB_SERVICE_URI + "/concert/reservation").request();

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.post(Entity.entity(reservationRequest, javax.ws.rs.core.MediaType.APPLICATION_XML));

			// Check that the HTTP response code is 201 OK.
			int responseCode = response.getStatus();

			switch (responseCode) {
				case 200:
					UserDTO retrievedUser = response.readEntity(UserDTO.class);

					if (retrievedUser.getUsername().equals(user.getUsername()) && retrievedUser.getPassword().equals(user.getPassword())) {
						saveCookie(response);
						return retrievedUser;
					} else {
						throw new ServiceException(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD);
					}
				case 400:
					String message = response.readEntity(String.class);
					if (message.equals(Messages.AUTHENTICATE_NON_EXISTENT_USER)) {
						throw new ServiceException(Messages.AUTHENTICATE_NON_EXISTENT_USER);
					}
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		if (_cookie == null) {
			throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
		}

		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			// Make an invocation on a Concert URI and specify Java-
			// serialization as the required data format.
			Builder builder = client.target(WEB_SERVICE_URI + "/user/creditcard").request();

			builder.cookie("UUID", _cookie.toString());

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.post(Entity.entity(creditCard, javax.ws.rs.core.MediaType.APPLICATION_XML));

			// Check that the HTTP response code is 201 OK.
			int responseCode = response.getStatus();

			switch (responseCode) {
				case 204:
					break;
				case 401:
					String message = response.readEntity(String.class);
					if (message.equals(Messages.AUTHENTICATE_NON_EXISTENT_USER)) {
						throw new ServiceException(Messages.AUTHENTICATE_NON_EXISTENT_USER);
					}
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		if (_cookie == null) {
			throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
		}

		Response response = null;
		Client client = ClientBuilder.newClient();

		try {
			// Make an invocation on a Concert URI and specify Java-
			// serialization as the required data format.
			Builder builder = client.target(WEB_SERVICE_URI + "/user").request();

			builder.cookie("UUID", _cookie.toString());

			// Make the service invocation via a HTTP GET message, and wait for
			// the response.
			response = builder.get();

			// Check that the HTTP response code is 201 OK.
			int responseCode = response.getStatus();

			switch (responseCode) {
				case 200:
					// Retrieve the list of concerts
					List<BookingDTO> bookings = response.readEntity(new GenericType<List<BookingDTO>>() {
					});

					return new HashSet<>(bookings);
				case 401:
					String message = response.readEntity(String.class);
					if (message.equals(Messages.AUTHENTICATE_NON_EXISTENT_USER)) {
						throw new ServiceException(Messages.AUTHENTICATE_NON_EXISTENT_USER);
					}
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			// Close the Response object.
			response.close();
			client.close();
		}
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

	private void saveCookie(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();
		if (cookies.containsKey("UUID")) {
			_cookie = cookies.get("UUID");
		}
	}

}
