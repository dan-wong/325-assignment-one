package nz.ac.auckland.concert.service.domain.user;

import nz.ac.auckland.concert.service.domain.concert.Booking;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "USER")
public class User {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long _id;

	@Column(name = "USERNAME", nullable = false)
	private String _username;

	@Column(name = "PASSWORD", nullable = false)
	private String _password;

	@Column(name = "FIRST_NAME", nullable = false)
	private String _firstName;

	@Column(name = "LAST_NAME", nullable = false)
	private String _lastName;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "id", nullable = false)
	private Set<Booking> _bookings;

	@Column(name = "CREDIT_CARD")
	private CreditCard _creditCard;

	protected User() {
	}

	public User(String username, String password, String firstName, String lastName) {
		_username = username;
		_password = password;
		_firstName = firstName;
		_lastName = lastName;
	}

	public Long getID() {
		return _id;
	}

	public String getUsername() {
		return _username;
	}

	public String getPassword() {
		return _password;
	}

	public String getFirstName() {
		return _firstName;
	}

	public String getLastName() {
		return _lastName;
	}

	public Set<Booking> getBookings() {
		return _bookings;
	}

//	public CreditCard getCreditCard() {
//		return _creditCard;
//	}
}
