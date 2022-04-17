package antifraud.database.user;

import antifraud.database.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}
