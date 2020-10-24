package machineRental.MR.dailyReport;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.estimate.model.EstimatePosition;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="daily_reports" )
public class DailyReport {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Column
  private LocalDate date;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "estimatePosition_id")
  private EstimatePosition estimatePosition;

  @NotEmpty
  @Column
  private String location;

  @NotEmpty
  @Column
  private String startPoint;

  @NotEmpty
  @Column
  private String endPoint;

  @NotEmpty
  @Column
  private String side;

  @NotNull
  @Column
  private double quantity;

  @NotNull
  @Column
  private String measureUnit;

  @NotEmpty
  @Column
  private String remarks;



}
