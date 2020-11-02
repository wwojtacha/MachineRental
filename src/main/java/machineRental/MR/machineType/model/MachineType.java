package machineRental.MR.machineType.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import machineRental.MR.machineType.CostCategory;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "machine_types")
public class MachineType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String machineType;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CostCategory costCategory;
}
