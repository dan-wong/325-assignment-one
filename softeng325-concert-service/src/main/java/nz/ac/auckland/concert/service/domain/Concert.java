package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Class to represent a Concert. A Concert is characterised by an unique ID,
 * title, date and time, and a featuring Performer.
 * <p>
 * Concert implements Comparable with a natural ordering based on its title.
 * Hence, in a List, Concert instances can be sorted into alphabetical order
 * based on their title value.
 */
@Entity
@Table(name = "CONCERT")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Concert implements Comparable<Concert> {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(nullable = false)
	private String _title;

	@ElementCollection
	@CollectionTable(name = "CONCERT_DATES")
	@JoinColumn(name = "id", nullable = false)
	private Set<Concert_Date> _dates;

	@ElementCollection
	@CollectionTable(name = "CONCERT_TARIFS")
	@JoinColumn(name = "id", nullable = false)
	private Map<PriceBand, BigDecimal> _tariff;

	@ElementCollection
	@CollectionTable(name = "CONCERT_PERFORMER")
	@JoinColumn(name = "id", nullable = false)
	private Set<Performer> _performers;

	// Required for JPA and JAXB.
	protected Concert() {
	}

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		_id = id;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public Set<Concert_Date> getDates() {
		return _dates;
	}

	public void setDates(Set<Concert_Date> dates) {
		_dates = dates;
	}

	public Map<PriceBand, BigDecimal> getTariff() {
		return _tariff;
	}

	public void setTariffs(Map<PriceBand, BigDecimal> tariff) {
		_tariff = tariff;
	}

	public Set<Performer> getPerformers() {
		return _performers;
	}

	public void setPerformers(Set<Performer> performers) {
		_performers = performers;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Concert))
			return false;
		if (obj == this)
			return true;

		Concert rhs = (Concert) obj;
		return new EqualsBuilder().
				append(_title, rhs.getTitle()).
				append(_dates, rhs.getDates()).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_title).hashCode();
	}

	@Override
	public int compareTo(Concert concert) {
		return _title.compareTo(concert.getTitle());
	}
}
