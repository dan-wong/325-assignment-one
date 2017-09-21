package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.service.domain.user.CreditCard;

public class CreditCardMapper {
	public static CreditCardDTO convertToDTO(CreditCard creditCard) {
		return new CreditCardDTO(creditCard.getType(), creditCard.getName(), creditCard.getName(), creditCard.getExpiryDate());
	}
}
