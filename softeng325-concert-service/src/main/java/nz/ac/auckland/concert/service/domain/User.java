package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.service.domain.concert.Booking;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "USER")
public class User {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(name = "username", nullable = false)
	private String _username;

	@Column(name = "password", nullable = false)
	private String _password;

	@Column(name = "firstname", nullable = false)
	private String _firstName;

	@Column(name = "lastname", nullable = false)
	private String _lastName;

	@ElementCollection
	private Set<Booking> _bookings;

	protected User() {
	}
}
