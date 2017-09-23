package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.service.domain.jpa.SeatNumberConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Composite Primary Key from
 * https://stackoverflow.com/questions/13032948/how-to-create-and-handle-composite-primary-key-in-jpa
 */
@Embeddable
public class SeatKey implements Serializable {
	@Column(name = "SEAT_ROW", nullable = false)
	private SeatRow _row;

	@Column(name = "SEAT_NUMBER", nullable = false)
	@Convert(converter = SeatNumberConverter.class)
	private SeatNumber _number;

	protected SeatKey() {
	}

	public SeatKey(SeatRow row, SeatNumber number) {
		_row = row;
		_number = number;
	}

	public SeatRow getSeatRow() {
		return _row;
	}

	public SeatNumber getSeatNumber() {
		return _number;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SeatKey))
			return false;
		if (obj == this)
			return true;

		SeatKey rhs = (SeatKey) obj;
		return new EqualsBuilder().
				append(_row, rhs._row).
				append(_number, rhs._number).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_row).
				append(_number).
				hashCode();
	}
}
