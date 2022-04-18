package antifraud.database.transaction;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionData, Long> {
    List<TransactionData> findByNumberAndDateDataBetween(String number, Timestamp from, Timestamp to);
}
