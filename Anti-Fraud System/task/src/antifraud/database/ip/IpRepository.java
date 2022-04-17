package antifraud.database.ip;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface IpRepository extends CrudRepository<IP, Long> {
    boolean existsByIp(String ip);

    @Transactional
    void deleteByIp(String ip);
}
