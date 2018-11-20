package machineRental.MR.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false, unique = true)
    private String mpk;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String buildingNumber;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String postalCode;



//    @OneToMany(mappedBy = "seller")
//    private Order order;

}
