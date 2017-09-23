package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.concert.Concert;
import nz.ac.auckland.concert.service.domain.concert.Performer;
import nz.ac.auckland.concert.service.services.PersistenceManager;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mapper class which maps the ConcertDTO to Concert Domain Model class and vice versa
 */
public class ConcertMapper {
	private static EntityManager _em = PersistenceManager.instance().createEntityManager();

	public static ConcertDTO convertToDTO(Concert concert) {
		Set<Long> performerIds = new HashSet<>();
		for (Performer performer : concert.getPerformers()) {
			performerIds.add(performer.getId());
		}

		return new ConcertDTO(concert.getId(), concert.getTitle(), concert.getDates(), concert.getPriceTariffs(), performerIds);
	}

	public static Concert convertToModel(ConcertDTO concertDTO) {
		HashMap<PriceBand, BigDecimal> priceTariffs = new HashMap<>();
		for (PriceBand pb : PriceBand.values()) {
			priceTariffs.put(pb, concertDTO.getTicketPrice(pb));
		}

		TypedQuery<Performer> performerQuery =
				_em.createQuery("SELECT p FROM Performer p WHERE p.id in :ids", Performer.class);
		performerQuery.setParameter("ids", concertDTO.getPerformerIds());
		List<Performer> performers = performerQuery.getResultList();

		return new Concert(concertDTO.getId(), concertDTO.getTitle(), concertDTO.getDates(), priceTariffs, new HashSet<>(performers));
	}
}
