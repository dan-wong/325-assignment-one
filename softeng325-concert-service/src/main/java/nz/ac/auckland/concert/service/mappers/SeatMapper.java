package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.concert.Seat;
import nz.ac.auckland.concert.service.domain.concert.SeatKey;

public class SeatMapper {
	public static SeatDTO convertToDTO(Seat seat) {
		SeatKey seatKey = seat.getSeatKey();
		return new SeatDTO(seatKey.getSeatRow(), seatKey.getSeatNumber());
	}
}
