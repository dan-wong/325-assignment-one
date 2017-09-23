package nz.ac.auckland.concert.service.mappers;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.domain.user.User;
import nz.ac.auckland.concert.service.services.PersistenceManager;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class UserMapper {
	private static EntityManager _em = PersistenceManager.instance().createEntityManager();

	public static UserDTO convertToDTO(User user) {
		return new UserDTO(user.getUsername(), user.getPassword(), user.getLastName(), user.getFirstName());
	}

	public static User convertToModel(UserDTO userDTO) {
		TypedQuery<User> userQuery =
				_em.createQuery("SELECT u FROM User AS u WHERE u._username = :username", User.class);
		userQuery.setParameter("username", userDTO.getUsername());
		List<User> user = userQuery.setMaxResults(1).getResultList();

		return user.size() == 0 ? null : user.get(0);
	}
}
