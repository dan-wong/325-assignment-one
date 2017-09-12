package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Embeddable
@Table(name = "BOOKING")
public class Booking {
	@Column(nullable = false)
	private Long _concertId;

	@Column(nullable = false)
	private String _concertTitle;

	@Column(nullable = false)
	private LocalDateTime _dateTime;

	@ElementCollection
	@Column(nullable = false)
	private Set<Seat> _seats;

	@Column(nullable = false)
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
		return new EqualsBuilder().append(_concertId, rhs._concertId)
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
