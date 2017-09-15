package nz.ac.auckland.concert.service.domain.user;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVATION")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(name = "SEATTYPE", nullable = false)
	private PriceBand _seatType;

	@Column(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column(name = "DATE_TIME", nullable = false)
	private LocalDateTime _date;

	public Reservation() {
	}

	public Long getId() {
		return _id;
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
				append(_seatType, rhs._seatType).
				append(_concertId, rhs._concertId).
				append(_date, rhs._date).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_id).
				append(_seatType).
				append(_concertId).
				append(_date).
				hashCode();
	}
}
