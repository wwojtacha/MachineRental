package machineRental.MR.model;

import lombok.Data;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "Settled|Unsettled")
    private String orderStatus;

    @NotNull
    @Column(nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private ZonedDateTime endDate;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = "Day|Week|Month|Custom")
    private String priceType;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

}
