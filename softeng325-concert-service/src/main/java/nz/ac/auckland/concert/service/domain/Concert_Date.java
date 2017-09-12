package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@Embeddable
@Table(name = "CONCERT_DATES")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Concert_Date {
	@JoinColumn(name = "id", nullable = false)
	private Concert _concert;

	@Column(nullable = false)
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;

	// Required for JPA and JAXB.
	protected Concert_Date() {
	}

	public Concert_Date(Concert concert, LocalDateTime date) {
		_concert = concert;
		_date = date;
	}

	public Concert getConcert() {
		return _concert;
	}

	public LocalDateTime getDate() {
		return _date;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Concert_Date))
			return false;
		if (obj == this)
			return true;

		Concert_Date rhs = (Concert_Date) obj;
		return new EqualsBuilder().
				append(_concert, rhs._concert).
				append(_date, rhs._date).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_concert).
				append(_date).
				toHashCode();
	}
}
