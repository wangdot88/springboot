package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.entity.Store;
import dangod.springboot.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    public Result<Store> createStore(@RequestBody Store store) {
        return Result.success(storeService.createStore(store));
    }

    @PutMapping("/{id}")
    public Result<Store> updateStore(@PathVariable Long id, @RequestBody Store store) {
        return Result.success(storeService.updateStore(id, store));
    }

    @GetMapping("/{id}")
    public Result<Store> getStore(@PathVariable Long id) {
        return Result.success(storeService.getStoreById(id));
    }

    @GetMapping("/name/{name}")
    public Result<Store> getStoreByName(@PathVariable String name) {
        return Result.success(storeService.getStoreByName(name));
    }

    @GetMapping
    public Result<List<Store>> getAllStores() {
        return Result.success(storeService.getAllStores());
    }

    @GetMapping("/status/{status}")
    public Result<List<Store>> getStoresByStatus(@PathVariable Store.StoreStatus status) {
        return Result.success(storeService.getStoresByStatus(status));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return Result.success();
    }
}
