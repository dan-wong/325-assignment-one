package nz.ac.auckland.concert.client.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultService implements ConcertService {
	private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";
	private NewCookie _cookie = null;

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Response response = null;
		Client client = null;

		try {
			client = ClientBuilder.newClient();

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
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Response response = null;
		Client client = null;

		try {
			client = ClientBuilder.newClient();

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
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public UserDTO createUser(UserDTO newUser) {
		Response response = null;
		Client client = null;

		try {
			client = ClientBuilder.newClient();

			Builder builder = client.target(WEB_SERVICE_URI + "/user").request();
			response = builder.post(Entity.entity(newUser, javax.ws.rs.core.MediaType.APPLICATION_XML));

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 201:
					saveCookie(response);
					return response.readEntity(UserDTO.class);
				case 400:
					String message = response.readEntity(String.class);
					throw new ServiceException(message);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		Response response = null;
		Client client = null;

		if (user.getUsername() == null || user.getPassword() == null) {
			throw new ServiceException(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS);
		}

		try {
			client = ClientBuilder.newClient();

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
					throw new ServiceException(message);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// AWS S3 access credentials for concert images.
		String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";
		String AWS_SECRET_ACCESS_KEY = "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";

		// Name of the S3 bucket that stores images.
		String AWS_BUCKET = "concert.aucklanduni.ac.nz";

		TransferManager mgr = null;

		try {
			// Create an AmazonS3 object that represents a connection with the
			// remote S3 service.
			BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
					AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
			AmazonS3 s3 = AmazonS3ClientBuilder
					.standard()
					.withRegion(Regions.AP_SOUTHEAST_2)
					.withCredentials(
							new AWSStaticCredentialsProvider(awsCredentials))
					.build();

			// Find images names stored in S3.
			ObjectListing ol = s3.listObjects(AWS_BUCKET);
			List<S3ObjectSummary> objects = ol.getObjectSummaries();
			List<String> imageNames = objects.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());

			String imageName = performer.getImageName();

			if (!imageNames.contains(imageName)) {
				throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
			}

			// Download the images.
			mgr = TransferManagerBuilder
					.standard()
					.withS3Client(s3)
					.build();

			File f = new File(imageName);

			Download xfer = mgr.download(AWS_BUCKET, imageName, f);

			try {
				return ImageIO.read(f);
			} catch (IOException e) {
				throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} catch (AmazonServiceException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		} finally {
			if (mgr != null) {
				mgr.shutdownNow();
			}
		}
	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		Response response = null;
		Client client = null;

		if (_cookie == null) {
			throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
		}

		try {
			client = ClientBuilder.newClient();

			if (reservationRequest.getNumberOfSeats() == 0 ||
					reservationRequest.getDate() == null ||
					reservationRequest.getConcertId() == null ||
					reservationRequest.getSeatType() == null) {
				throw new ServiceException(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS);
			}

			Builder builder = client.target(WEB_SERVICE_URI + "/reservation").request();
			builder.cookie("UUID", _cookie.toString());
			response = builder.post(Entity.entity(reservationRequest, javax.ws.rs.core.MediaType.APPLICATION_XML));

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 201:
					return response.readEntity(ReservationDTO.class);
				case 400:
					String message = response.readEntity(String.class);
					throw new ServiceException(message);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		Response response = null;
		Client client = null;

		if (_cookie == null) {
			throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
		}

		try {
			client = ClientBuilder.newClient();

			Builder builder = client.target(WEB_SERVICE_URI + "/reservation").request();
			builder.cookie("UUID", _cookie.toString());
			response = builder.put(Entity.entity(reservation, javax.ws.rs.core.MediaType.APPLICATION_XML));

			int responseCode = response.getStatus();

			switch (responseCode) {
				case 204:
					return;
				case 400:
					String message = response.readEntity(String.class);
					throw new ServiceException(message);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		Response response = null;
		Client client = null;

		if (_cookie == null) {
			throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
		}

		try {
			client = ClientBuilder.newClient();

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
					return;
				case 401:
					String message = response.readEntity(String.class);
					throw new ServiceException(message);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
		}
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		Response response = null;
		Client client = null;

		if (_cookie == null) {
			throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
		}

		try {
			client = ClientBuilder.newClient();

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
					throw new ServiceException(message);
				default:
					throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
			}
		} finally {
			if (response != null) {
				response.close();
			}

			if (client != null) {
				client.close();
			}
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
