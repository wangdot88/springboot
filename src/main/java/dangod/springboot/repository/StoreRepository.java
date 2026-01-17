package dangod.springboot.repository;

import dangod.springboot.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByStatus(Store.StoreStatus status);

    Store findByName(String name);
}
