package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NEWS_ITEMS")
public class NewsItem {
	@Id
	@GeneratedValue
	private Long _id;

	@Column(name = "TIMESTAMPE", nullable = false)
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _timestamp;

	@Column(name = "CONTENT", nullable = false)
	private String _content;

	public NewsItem() {
	}

	public NewsItem(Long id, LocalDateTime timestamp, String content) {
		_id = id;
		_timestamp = timestamp;
		_content = content;
	}

	public Long getId() {
		return _id;
	}

	public LocalDateTime getTimetamp() {
		return _timestamp;
	}

	public String getContent() {
		return _content;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NewsItem))
			return false;
		if (obj == this)
			return true;

		NewsItem rhs = (NewsItem) obj;
		return new EqualsBuilder().
				append(_timestamp, rhs._timestamp).
				append(_content, rhs._content).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_timestamp).
				append(_content).
				hashCode();
	}
}
