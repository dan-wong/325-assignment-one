package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.common.types.PriceBand;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@Embeddable
@Table(name = "CONCERT_TARIFS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcertTariffs {
	@Column(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column(name = "COST", nullable = false)
	private BigDecimal _cost;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRICE_BAND", nullable = false)
	private PriceBand _priceBand;

	protected ConcertTariffs() {
	}
}
