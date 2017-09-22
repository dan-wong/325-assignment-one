package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "BOOKING")
public class Booking {
	@Id
	@GeneratedValue
	private Long _id;

	@ManyToOne
	private Concert _concert;

	@Column(name = "DATE_TIME", nullable = false)
	private LocalDateTime _dateTime;

	@OneToMany
	@JoinColumn(name = "SEAT_ID")
	private Set<Seat> _seats;

	@Column(name = "PRICE_BAND", nullable = false)
	private PriceBand _priceBand;

	protected Booking() {
	}

	public Booking(Long id, Concert concert, LocalDateTime localDateTime, Set<Seat> seats, PriceBand priceBand) {
		_id = id;
		_concert = concert;
		_dateTime = localDateTime;
		_seats = seats;
		_priceBand = priceBand;
	}

	public Concert getConcert() {
		return _concert;
	}

	public LocalDateTime getDateTime() {
		return _dateTime;
	}

	public Set<Seat> getSeats() {
		return _seats;
	}

	public PriceBand getPriceBand() {
		return _priceBand;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Booking))
			return false;
		if (obj == this)
			return true;

		Booking rhs = (Booking) obj;
		return new EqualsBuilder()
				.append(_id, rhs._id)
				.append(_concert, rhs._concert)
				.append(_dateTime, rhs._dateTime)
				.append(_seats, rhs._seats)
				.append(_priceBand, rhs._priceBand).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				.append(_concert).append(_dateTime).append(_seats)
				.append(_priceBand).hashCode();
	}
}
