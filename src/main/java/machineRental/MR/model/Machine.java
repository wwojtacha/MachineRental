package machineRental.MR.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Size(min = 8, max = 8, message = "Field must be equal to 8 characters")
    private String internalId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String producer;

    @NotBlank
    @Column(nullable = false)
    private String model;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer productionYear;

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "In use|Out of use", message = "Must use predefined type")
    private String machineStatus;

//    @OneToOne(mappedBy = "machine")
//    private Order order;

    @ManyToOne
    @JoinColumn(name = "machine_type_id")
    private MachineType machineType;


//    @OneToMany(mappedBy = "machine")
//    private Price price;


}
