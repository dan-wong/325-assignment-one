package nz.ac.auckland.concert.service.domain.concert;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class ConcertSeatsKey implements Serializable {
	@Column(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column(name = "DATE", nullable = false)
	private LocalDateTime _date;

	protected ConcertSeatsKey() {
	}

	public ConcertSeatsKey(Long concertId, LocalDateTime date) {
		_concertId = concertId;
		_date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConcertSeatsKey))
			return false;
		if (obj == this)
			return true;

		ConcertSeatsKey rhs = (ConcertSeatsKey) obj;
		return new EqualsBuilder().
				append(_concertId, rhs._concertId).
				append(_date, rhs._date).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_concertId).
				append(_date).
				hashCode();
	}
}
