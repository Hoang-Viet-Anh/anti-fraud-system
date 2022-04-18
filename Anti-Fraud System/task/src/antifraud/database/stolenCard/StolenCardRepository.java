package antifraud.database.stolenCard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    boolean existsByNumber(String number);

    @Transactional
    void deleteByNumber(String number);
}
