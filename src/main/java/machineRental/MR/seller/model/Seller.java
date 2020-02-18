package machineRental.MR.seller.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Seller MPK/NIP is required.")
    @Column(nullable = false, unique = true)
    private String mpk;

    @NotBlank(message = "Seller name is required.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Seller city is required.")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Seller street is required.")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Seller building number is required.")
    @Column(nullable = false)
    private String buildingNumber;

    @NotBlank(message = "Seller postal code is required.")
    @Column(nullable = false)
    private String postalCode;



//    @OneToMany(mappedBy = "seller")
//    private Order order;

}
