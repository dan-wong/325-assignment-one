package nz.ac.auckland.concert.common.dto;

import nz.ac.auckland.concert.common.jaxb.LocalDateAdapter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * DTO class to represent credit cards. 
 * 
 * A CreditCardDTO describes a credit card in terms of:
 * _type       type of credit card, Visa or Mastercard.
 * _name       the name of the person who owns the credit card.
 * _number     16-digit credit card number. 
 * _expiryDate the credit card's expiry date. 
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCardDTO {
	private Type _type;

	private String _name;

	private String _number;

	@XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
	private LocalDate _expiryDate;

	public enum Type {Visa, Master}
	
	public CreditCardDTO() {}
	
	public CreditCardDTO(Type type, String name, String number, LocalDate expiryDate) {
		_type = type;
		_name = name;
		_number = number;
		_expiryDate = expiryDate;
	}
	
	public Type getType() {
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
		if (!(obj instanceof CreditCardDTO))
            return false;
        if (obj == this)
            return true;

        CreditCardDTO rhs = (CreditCardDTO) obj;
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
