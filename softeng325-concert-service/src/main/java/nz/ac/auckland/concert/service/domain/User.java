package nz.ac.auckland.concert.service.domain;

import javax.persistence.*;

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

	protected User() {
	}
}
