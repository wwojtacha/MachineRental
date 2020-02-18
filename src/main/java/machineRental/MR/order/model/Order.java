package machineRental.MR.order.model;

import java.time.LocalDate;
import javax.validation.constraints.Min;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import machineRental.MR.client.model.Client;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.seller.model.Seller;

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
    private String status;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = "Day|Week|Month|Custom")
    private String priceType;

    @NotNull
    @Min(value = 1, message = "Order quantity cannot be lower then 0")
    @Column(nullable = false)
    private int quantity = 1;

    @NotNull
    @Min(value = 0, message = "Price cannot be lower than 0.")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private BigDecimal value = BigDecimal.valueOf(-666);

    @NotNull
    @Column(nullable = false)
    private Boolean dbPrice = false;

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
