package quicktrade.com.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quicktrade.com.entity.StockNames;

import java.util.List;

@Repository
public interface StockNameRepository extends JpaRepository<StockNames, String> {
//    List<StockNames> findBySymbolContainingIgnoreCase(String symbol);
//    List<StockNames> findByNameContainingIgnoreCase(String name);

@Query("SELECT s FROM StockNames s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :symbol, '%'))")
List<StockNames> findBySymbolContainingIgnoreCase(@Param("symbol") String symbol);


    @Query("SELECT s FROM StockNames s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<StockNames> findByNameContainingIgnoreCase(@Param("name") String name);
}
