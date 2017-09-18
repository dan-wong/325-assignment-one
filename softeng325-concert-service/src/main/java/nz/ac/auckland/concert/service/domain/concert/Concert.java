package nz.ac.auckland.concert.service.domain.concert;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "CONCERTS")
public class Concert implements Comparable<Concert> {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(name = "TITLE", nullable = false)
	private String _title;

	@ElementCollection
	@CollectionTable(name = "CONCERT_DATES", joinColumns = @JoinColumn(name = "CONCERT_DATES_ID"))
	@Convert(converter = LocalDateTimeConverter.class)
	@Column(name = "DATES", nullable = false)
	private Set<LocalDateTime> _dates;

	@ElementCollection
	@CollectionTable(name = "CONCERT_TARIFS", joinColumns = @JoinColumn(name = "CONCERT_TARIFF_ID"))
	@MapKeyEnumerated(EnumType.STRING)
	@Column(name = "TARIFFS", nullable = false)
	private Map<PriceBand, BigDecimal> _priceTariffs;

	@ManyToMany
	@JoinTable(name = "CONCERT_PERFORMER")
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
