package dangod.springboot.entity.equipment;

import dangod.springboot.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_equipment")
public class Equipment extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "available_quantity")
    private Integer availableQuantity;

    @Column(name = "borrowed_quantity")
    private Integer borrowedQuantity;

    @Column(name = "repair_quantity")
    private Integer repairQuantity;

    @Column(name = "min_stock")
    private Integer minStock;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "purchase_date")
    private java.time.LocalDate purchaseDate;

    @Column(name = "price")
    private Double price;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
