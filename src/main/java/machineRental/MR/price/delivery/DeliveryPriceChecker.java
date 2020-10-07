package machineRental.MR.price.delivery;

import machineRental.MR.price.PriceChecker;
import machineRental.MR.repository.DeliveryDocumentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPriceChecker implements PriceChecker {

  @Autowired
  private DeliveryDocumentEntryRepository deliveryDocumentEntryRepository;

  @Override
  public boolean isPriceAlreadyUsed(Long priceId) {
    return deliveryDocumentEntryRepository.existsByDeliveryPrice_Id(priceId);
  }
}
