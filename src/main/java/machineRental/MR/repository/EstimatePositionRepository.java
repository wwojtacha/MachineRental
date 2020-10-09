package machineRental.MR.repository;

import java.util.List;
import machineRental.MR.estimate.model.EstimatePosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstimatePositionRepository extends JpaRepository<EstimatePosition, Long> {


  Page<EstimatePosition> findByNameContainingAndCostCode_ProjectCodeContainingAndCostCode_CostTypeContainingAndRemarksContaining(String name, String projectCode, String costType, String remarks, Pageable pageable);

  boolean existsByNameAndCostCode_ProjectCode(String name, String projectCode);

  List<EstimatePosition> findByCostCode_Id(Long id);
}
