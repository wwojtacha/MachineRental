package machineRental.MR.client.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Client MPK is required.")
    @Column(nullable = false, unique = true)
    private String mpk;

    @NotBlank(message = "Client name is required.")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Client city is required.")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Client street is required.")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Client building number is required.")
    @Column(nullable = false)
    private String buildingNumber;

    @NotBlank(message = "Client postal code is required.")
    @Column(nullable = false)
    private String postalCode;

    @NotBlank(message = "Client e-mail address is required")
    @Email(message = "Email address must be valid")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Client contact person is required.")
    @Column(nullable = false)
    private String contactPerson;

    @NotBlank(message = "Client phone number is required.")
    @Column(nullable = false)
    private String phoneNumber;

//    @OneToMany(mappedBy = "client")
//    private Order order;
}
