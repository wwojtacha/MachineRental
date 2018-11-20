package machineRental.MR.model;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "prices")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @NotNull
    @Column(nullable = false)
    private BigDecimal priceFor7Days;

    @NotNull
    @Column(nullable = false)
    private BigDecimal priceFor30Days;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;


}
