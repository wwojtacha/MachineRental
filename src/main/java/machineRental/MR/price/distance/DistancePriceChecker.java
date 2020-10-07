package machineRental.MR.price.distance;

import machineRental.MR.price.PriceChecker;
import machineRental.MR.repository.DeliveryDocumentEntryRepository;
import machineRental.MR.repository.RoadCardEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistancePriceChecker implements PriceChecker {

  @Autowired
  private RoadCardEntryRepository roadCardEntryRepository;

  @Override
  public boolean isPriceAlreadyUsed(Long priceId) {
    return roadCardEntryRepository.existsByDistancePrice_Id(priceId);
  }
}
