package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.service.domain.jpa.SeatNumberConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Seat {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false)
	private SeatRow _row;

	@Column(nullable = false)
	@Convert(converter = SeatNumberConverter.class)
	private SeatNumber _number;

	@ManyToOne
	private Concert _concert;
	private LocalDateTime _date;

	protected Seat() {
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
			return false;
		if (obj == this)
			return true;

		Seat rhs = (Seat) obj;
		return new EqualsBuilder().
				append(_id, rhs._id).
				append(_row, rhs._row).
				append(_number, rhs._number).
				append(_concert, rhs._concert).
				append(_date, rhs._date).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_id).
				append(_row).
				append(_number).
				hashCode();
	}
}
