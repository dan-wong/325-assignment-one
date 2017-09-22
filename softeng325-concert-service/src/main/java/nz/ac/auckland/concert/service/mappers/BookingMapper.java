package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.concert.Booking;
import nz.ac.auckland.concert.service.domain.concert.Concert;

import java.util.Set;
import java.util.stream.Collectors;

public class BookingMapper {
	public static BookingDTO convertToDTO(Booking booking) {
		Concert concert = booking.getConcert();
		Set<SeatDTO> seats = booking.getSeats().stream()
				.map(SeatMapper::convertToDTO)
				.collect(Collectors.toSet());

		return new BookingDTO(concert.getId(), concert.getTitle(), booking.getDateTime(), seats, booking.getPriceBand());
	}
}
