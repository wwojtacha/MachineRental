package machineRental.MR.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "clients")
public class Client {

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

    @NotBlank(message = "Field cannot be empty")
    @Email(message = "Email address must be valid")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String contactPerson;

    @NotBlank(message = "Field cannot be empty")
    @Column(nullable = false)
    private String phoneNumber;

//    @OneToMany(mappedBy = "client")
//    private Order order;
}
