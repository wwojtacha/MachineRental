package machineRental.MR.price.distance.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.price.distance.service.DateCheckerObject;
import machineRental.MR.price.PriceType;
import machineRental.MR.workDocumentEntry.WorkCode;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Data
@Table(name = "distancePrices")
public class DistancePrice extends DateCheckerObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Work code is required.")
    @Column(nullable = false)
    private WorkCode workCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @NotNull(message = "Price type is required.")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private double rangeMin;

    @NotNull
    @Column(nullable = false)
    private double rangeMax;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(nullable = false)
    private String projectCode;

//    @ManyToOne
//    @JoinColumn(name = "machine_id")
//    private Machine machine;

    @Transient
    private MultipartFile file;

    @NotNull
    @Column(nullable = false)
    private LocalDate modificationDate;
}
