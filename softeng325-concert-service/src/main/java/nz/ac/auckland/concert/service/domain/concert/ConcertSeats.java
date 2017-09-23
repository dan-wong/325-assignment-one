package nz.ac.auckland.concert.service.domain.concert;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "CONCERT_SEATS")
public class ConcertSeats {
	@Id
	@GeneratedValue
	private Long _id;

	@ManyToOne
	@JoinColumn(name = "CONCERT_ID")
	private Concert _concert;

	@Column(name = "DATE", nullable = false)
	private LocalDateTime _date;

	@OneToMany(mappedBy = "_concert")
	private Set<Seat> _availableSeats;

	@OneToMany(mappedBy = "_concert")
	private Set<Seat> _bookedSeats;

	protected ConcertSeats() {
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
				append(_concert, rhs._concert).
				append(_date, rhs._date).
				append(_availableSeats, rhs._availableSeats).
				append(_bookedSeats, rhs._bookedSeats).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(_id)
				.append(_concert)
				.append(_date)
				.append(_availableSeats)
				.append(_bookedSeats)
				.hashCode();
	}
}
