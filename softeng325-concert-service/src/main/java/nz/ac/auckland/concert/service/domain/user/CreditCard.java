package nz.ac.auckland.concert.service.domain.user;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CREDIT_CARD")
public class CreditCard {
	@Id
	@GeneratedValue
	private Long _id;

	@Enumerated
	@Column(name = "TYPE", nullable = false)
	private CreditCardDTO.Type _type;

	@Column(name = "NAME", nullable = false)
	private String _name;

	@Column(name = "NUMBER", nullable = false)
	private String _number;

	@Column(name = "EXPIRY_DATE", nullable = false)
	private LocalDate _expiryDate;

	public CreditCard() {
	}

	public CreditCard(CreditCardDTO.Type type, String name, String number, LocalDate expiryDate) {
		_type = type;
		_name = name;
		_number = number;
		_expiryDate = expiryDate;
	}

	public CreditCardDTO.Type getType() {
		return _type;
	}

	public String getName() {
		return _name;
	}

	public String getNumber() {
		return _number;
	}

	public LocalDate getExpiryDate() {
		return _expiryDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CreditCard))
			return false;
		if (obj == this)
			return true;

		CreditCard rhs = (CreditCard) obj;
		return new EqualsBuilder().
				append(_type, rhs._type).
				append(_name, rhs._name).
				append(_number, rhs._number).
				append(_expiryDate, rhs._expiryDate).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_type).
				append(_name).
				append(_number).
				append(_expiryDate).
				hashCode();
	}
}
