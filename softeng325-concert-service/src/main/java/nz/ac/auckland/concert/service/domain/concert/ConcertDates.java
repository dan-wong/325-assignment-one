package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@Embeddable
@Table(name = "CONCERT_DATES")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcertDates {
	@Column(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column(name = "CONCERT_DATE", nullable = false)
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;

	protected ConcertDates() {
	}
}
