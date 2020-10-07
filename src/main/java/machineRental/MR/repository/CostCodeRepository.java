package machineRental.MR.repository;

import machineRental.MR.costcode.model.CostCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostCodeRepository extends JpaRepository<CostCode, Long> {

  boolean existsByFullCode(String fullCode);

  boolean existsByProjectCode(String projectCode);

  Page<CostCode> findByProjectCodeContainingAndCostTypeContaining(String projectCode, String costType, Pageable pageable);

  CostCode findByFullCode(String fullCode);
}
