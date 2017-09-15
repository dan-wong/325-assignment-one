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

	@Column(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column(name = "CONCERT_TITLE", nullable = false)
	private String _concertTitle;

	@Column(name = "DATE_TIME", nullable = false)
	private LocalDateTime _dateTime;

	@ElementCollection
	@Column(name = "SEATS", nullable = false)
	private Set<Seat> _seats;

	@Column(name = "PRICE_BAND", nullable = false)
	private PriceBand _priceBand;

	protected Booking() {
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
				.append(_concertId, rhs._concertId)
				.append(_concertTitle, rhs._concertTitle)
				.append(_dateTime, rhs._dateTime)
				.append(_seats, rhs._seats)
				.append(_priceBand, rhs._priceBand).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(_concertId)
				.append(_concertTitle).append(_dateTime).append(_seats)
				.append(_priceBand).hashCode();
	}
}
