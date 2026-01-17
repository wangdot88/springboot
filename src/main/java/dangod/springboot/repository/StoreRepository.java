package dangod.springboot.repository;

import dangod.springboot.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByStoreCode(String storeCode);
    Store findByStoreName(String storeName);
    List<Store> findByStatus(Integer status);
}
