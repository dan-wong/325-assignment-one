package nz.ac.auckland.concert.service.domain.user;

import nz.ac.auckland.concert.service.domain.concert.Booking;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@Entity
@Table(name = "USER")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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

	@ElementCollection
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "id", nullable = false)
	private Set<Booking> _bookings;

	@Embedded
	@Column(name = "CREDIT_CARD")
	private CreditCard _creditCard;

	protected User() {
	}
}
