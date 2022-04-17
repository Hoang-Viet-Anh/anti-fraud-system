package antifraud.database.stolenCard;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    boolean existsByNumber(String number);

    @Transactional
    void deleteByNumber(String number);
}
