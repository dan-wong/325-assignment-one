package nz.ac.auckland.concert.service.domain.concert;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CONCERT_SEATS")
public class ConcertSeats {
	@EmbeddedId
	private ConcertSeatsKey _id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "AVAILABLE_SEATS")
	private Set<Seat> _availableSeats;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "BOOKED_SEATS")
	private Set<Seat> _bookedSeats;

	protected ConcertSeats() {
	}

	public ConcertSeats(ConcertSeatsKey id, Set<Seat> availableSeats) {
		_id = id;
		_availableSeats = availableSeats;
		_bookedSeats = new HashSet<>();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConcertSeats))
			return false;
		if (obj == this)
			return true;

		ConcertSeats rhs = (ConcertSeats) obj;
		return new EqualsBuilder().
				append(_id, rhs._id).
				append(_availableSeats, rhs._availableSeats).
				append(_bookedSeats, rhs._bookedSeats).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(_id)
				.append(_availableSeats)
				.append(_bookedSeats)
				.hashCode();
	}
}
