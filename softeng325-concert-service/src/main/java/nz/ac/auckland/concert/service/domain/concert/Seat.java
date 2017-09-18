package nz.ac.auckland.concert.service.domain.concert;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Seat {
	//Composite Primary Key
	@EmbeddedId
	private SeatKey _id;

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
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_id).
				hashCode();
	}
}
