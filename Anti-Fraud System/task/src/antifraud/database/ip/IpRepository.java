package antifraud.database.ip;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface IpRepository extends CrudRepository<IP, Long> {
    boolean existsByIp(String ip);

    @Transactional
    void deleteByIp(String ip);
}
