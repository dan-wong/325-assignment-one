package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;

@Embeddable
public class Concert_Date {
	@JoinColumn(name = "id", nullable = false)
	private Concert _concert;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;

	// Required for JPA and JAXB.
	protected Concert_Date() {
	}
}
