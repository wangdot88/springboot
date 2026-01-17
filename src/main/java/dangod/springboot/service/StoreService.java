package dangod.springboot.service;

import dangod.springboot.entity.Store;
import dangod.springboot.repository.StoreRepository;
import dangod.springboot.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public Store createStore(Store store) {
        if (storeRepository.findByName(store.getName()) != null) {
            throw new BusinessException("门店名称已存在");
        }
        store.setCreateTime(new java.util.Date());
        store.setUpdateTime(new java.util.Date());
        return storeRepository.save(store);
    }

    public Store updateStore(Long id, Store store) {
        Store existingStore = storeRepository.findOne(id);
        if (existingStore == null) {
            throw new BusinessException("门店不存在");
        }
        
        if (store.getName() != null) {
            existingStore.setName(store.getName());
        }
        if (store.getAddress() != null) {
            existingStore.setAddress(store.getAddress());
        }
        if (store.getPhone() != null) {
            existingStore.setPhone(store.getPhone());
        }
        if (store.getManager() != null) {
            existingStore.setManager(store.getManager());
        }
        if (store.getLatitude() != null) {
            existingStore.setLatitude(store.getLatitude());
        }
        if (store.getLongitude() != null) {
            existingStore.setLongitude(store.getLongitude());
        }
        if (store.getStatus() != null) {
            existingStore.setStatus(store.getStatus());
        }
        
        existingStore.setUpdateTime(new java.util.Date());
        return storeRepository.save(existingStore);
    }

    public Store getStoreById(Long id) {
        Store store = storeRepository.findOne(id);
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        return store;
    }

    public Store getStoreByName(String name) {
        return storeRepository.findByName(name);
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public List<Store> getStoresByStatus(Store.StoreStatus status) {
        return storeRepository.findByStatus(status);
    }

    public void deleteStore(Long id) {
        storeRepository.delete(id);
    }
}
