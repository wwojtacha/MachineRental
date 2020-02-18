package machineRental.MR.price.model;


import javax.validation.constraints.Pattern;
import lombok.Data;
import machineRental.MR.machine.model.Machine;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "prices")
public class Price {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;

    @Id
    @Column(name="id", unique=true)
    private String id;

    @NotNull
    @Min(value = 1900, message = "Minimum year value is 1900")
    @Max(value = 2100, message = "Maximum year value is 2100")
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = "Day|Week|Month")
    private String priceType;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

//    @NotNull
//    @Column(nullable = false)
//    private BigDecimal priceFor7Days;
//
//    @NotNull
//    @Column(nullable = false)
//    private BigDecimal priceFor30Days;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @Transient
    private MultipartFile file;


}
