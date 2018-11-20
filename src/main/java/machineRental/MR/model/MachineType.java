package machineRental.MR.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "machine_types")
public class MachineType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String machineType;
}
