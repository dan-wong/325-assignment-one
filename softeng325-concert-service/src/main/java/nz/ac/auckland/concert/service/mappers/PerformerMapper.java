package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.service.domain.concert.Concert;
import nz.ac.auckland.concert.service.domain.concert.Performer;
import nz.ac.auckland.concert.service.services.PersistenceManager;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerformerMapper {
	private static EntityManager _em = PersistenceManager.instance().createEntityManager();

	public static PerformerDTO convertToDTO(Performer performer) {
		Set<Long> concertIds = new HashSet<>();
		for (Concert concert : performer.getConcerts()) {
			concertIds.add(concert.getId());
		}

		return new PerformerDTO(performer.getId(), performer.getName(), performer.getS3ImageUri(), performer.getGenre(), concertIds);
	}

	public static Performer convertToModel(PerformerDTO performerDTO) {
		TypedQuery<Concert> concertQuery =
				_em.createQuery("SELECT c FROM CONCERTS c WHERE c.id in :ids", Concert.class);
		concertQuery.setParameter("ids", performerDTO.getConcertIds());
		List<Concert> concerts = concertQuery.getResultList();

		return new Performer(performerDTO.getId(), performerDTO.getName(), performerDTO.getImageName(), performerDTO.getGenre(), new HashSet<>(concerts));
	}
}
