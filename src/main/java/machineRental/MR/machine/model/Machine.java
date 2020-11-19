package machineRental.MR.machine.model;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import machineRental.MR.client.model.Client;
import machineRental.MR.machineType.model.MachineType;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
//@EqualsAndHashCode(of = "uuid")
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Type(type = "uuid-char")
//    @Column(name = "uuid")
//    private final UUID uuid = UUID.randomUUID();

    @NotBlank(message = "Machine reqiures its unique number!")
    @Column(nullable = false, unique = true)
    @Size(min = 1, max = 8, message = "Machine number must be equal to 8 characters.")
    private String internalId;

    @NotBlank(message = "Machine must have a name.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Please enter producer of the machine.")
    @Column(nullable = false)
    private String producer;

    @NotBlank(message = "Please enter machine model.")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Please enter year of production.")
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer productionYear;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client owner;

    @NotBlank()
    @Column(nullable = false)
    @Pattern(regexp = "In use|Out of use", message = "Must use predefined type")
    private String machineStatus;

    @NotNull(message = "Plese enter quantity.")
    @Column(nullable = false)
    @Min(0)
    private int totalPhysicalQuantity;

//    @OneToOne(mappedBy = "machine")
//    private Order order;

    @ManyToOne
    @JoinColumn(name = "machine_type_id")
    private MachineType machineType;


//    @OneToMany(mappedBy = "machine")
//    private Price sellPrice;


}
