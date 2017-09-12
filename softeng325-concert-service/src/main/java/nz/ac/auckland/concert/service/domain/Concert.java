package nz.ac.auckland.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
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
	private Set<Concert_Date> _dates = new HashSet<>();

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name = "id", nullable = false)
	private Performer _performer;

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

	public void setPerformer(Performer performer) {
		_performer = performer;
	}

	public Performer getPerformer() {
		return _performer;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Concert, id: ");
		buffer.append(_id);
		buffer.append(", title: ");
		buffer.append(_title);
		buffer.append(", date: ");
		buffer.append(_dates.toString());
		buffer.append(", featuring: ");
		buffer.append(_performer.getName());

		return buffer.toString();
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
