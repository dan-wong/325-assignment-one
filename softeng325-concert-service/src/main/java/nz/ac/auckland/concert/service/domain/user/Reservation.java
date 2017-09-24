package nz.ac.auckland.concert.service.domain.user;

import nz.ac.auckland.concert.service.domain.concert.Seat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "RESERVATION")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {
	@Id
	@GeneratedValue
	private Long _id;

	@ElementCollection
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "SEATS", nullable = false)
	private Set<Seat> _seats;

	@Column(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column(name = "DATE_TIME", nullable = false)
	private LocalDateTime _date;

	@Column(name = "REQUEST", nullable = false)
	private Long _timeOfRequest;

	protected Reservation() {
	}

	public Reservation(Long concertId, Set<Seat> seats, LocalDateTime date) {
		_seats = seats;
		_concertId = concertId;
		_date = date;
		_timeOfRequest = System.currentTimeMillis();
	}

	public Long getId() {
		return _id;
	}

	public Long getConcertId() {
		return _concertId;
	}

	public Set<Seat> getSeats() {
		return _seats;
	}

	public LocalDateTime getDateOfConcert() {
		return _date;
	}

	public Long getTimeOfRequest() {
		return _timeOfRequest;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Reservation))
			return false;
		if (obj == this)
			return true;

		Reservation rhs = (Reservation) obj;
		return new EqualsBuilder().
				append(_id, rhs._id).
				append(_seats, rhs._seats).
				append(_concertId, rhs._concertId).
				append(_date, rhs._date).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_id).
				append(_seats).
				append(_concertId).
				append(_date).
				hashCode();
	}
}
