package machineRental.MR.price.delivery;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.client.model.Client;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.price.PriceChecker;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.delivery.model.DeliveryPrice;
import machineRental.MR.price.distance.service.DateChecker;
import machineRental.MR.price.hour.exception.OverlappingDatesException;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.repository.DeliveryDocumentEntryRepository;
import machineRental.MR.repository.DeliveryPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPriceChecker implements PriceChecker {

  @Autowired
  private DeliveryDocumentEntryRepository deliveryDocumentEntryRepository;

  @Autowired
  private DeliveryPriceRepository deliveryPriceRepository;

  private DateChecker dateChecker = new DateChecker();

  @Override
  public boolean isPriceAlreadyUsed(Long priceId) {
    return deliveryDocumentEntryRepository.existsByDeliveryPrice_Id(priceId);
  }

  public void checkEditability(Long id, DeliveryPrice currentDeliveryPrice, DeliveryPrice editedDeliveryPrice) {
    List<DeliveryDocumentEntry> deliveryDocumentEntires = deliveryDocumentEntryRepository.findAllByDeliveryPrice_Id(id);

//    deliveryDocumentEntries found by DeliveryPrice id all concern the same contractor
    String contractorMpk = "";
    if (!deliveryDocumentEntires.isEmpty()) {
      Client contractor = deliveryDocumentEntires.iterator().next().getContractor();
      contractorMpk = contractor.getMpk();
    }
    checkDeliveryPriceUniquness(currentDeliveryPrice, editedDeliveryPrice, contractorMpk);

    checkDeliveryPriceMatch(editedDeliveryPrice, deliveryDocumentEntires);
//    no exception thrown upto now so edited price can be updated
  }

  private void checkDeliveryPriceUniquness(DeliveryPrice currentDeliveryPrice, DeliveryPrice editedDeliveryPrice, String contractorMpk) {
    //    uniqueness of editedDeliveryPrice needs to be checked ony against existing hour prices for a given machine. All prices for different machines will be unique by definition.
    List<DeliveryPrice> deliveryPricesByContractor = deliveryPriceRepository.findByContractor_Mpk(contractorMpk);

    for (DeliveryPrice deliveryPriceByContractor : deliveryPricesByContractor) {
//      useless to check if current price to be edited is unique against itself
      if (currentDeliveryPrice == deliveryPriceByContractor) {
        continue;
      }

      if (!isPriceUnique(editedDeliveryPrice, deliveryPriceByContractor)) {
        throw new OverlappingDatesException(
            String.format("Delivery price for a given contractor (%s), material (%s), price type (%s) cannot overlap in time with the same entry.",
                editedDeliveryPrice.getContractor().getName(), editedDeliveryPrice.getMaterial().getType(), editedDeliveryPrice.getPriceType()));
      }
    }
  }

  public boolean isPriceUnique(DeliveryPrice newPrice, DeliveryPrice price) {
    return !newPrice.getContractor().getMpk().equals(price.getContractor().getMpk())
        || !newPrice.getMaterial().getType().equals(price.getMaterial().getType())
        || newPrice.getPriceType() != price.getPriceType()
        || !newPrice.getProjectCode().equals(price.getProjectCode())
        || !dateChecker.areDatesOverlapping(newPrice, price);
  }

  private void checkDeliveryPriceMatch(DeliveryPrice editedDeliveryPrice, List<DeliveryDocumentEntry> deliveryDocumentEntires) {
    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntires) {

      if (!isPriceMatching(deliveryDocumentEntry, editedDeliveryPrice)) {
        DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();
        String delivertDocumentContractorName = deliveryDocument.getContractor().getName();
        String deliveryDocumentNumber = deliveryDocument.getDocumentNumber();
        String materialType = deliveryDocumentEntry.getMaterial().getType();
        PriceType priceType = deliveryDocumentEntry.getDeliveryPrice().getPriceType();
        String estimateProjectCode = deliveryDocumentEntry.getEstimatePosition().getCostCode().getProjectCode();
        LocalDate date = deliveryDocument.getDate();

        throw new NotFoundException(String.format("Edited distance price does not match delivery document %s, %s entry parameters: %s, %s, %s, %s.",
            delivertDocumentContractorName,
            deliveryDocumentNumber,
            materialType,
            priceType,
            estimateProjectCode,
            date));
      }
    }
  }

  private boolean isPriceMatching(DeliveryDocumentEntry deliveryDocumentEntry, DeliveryPrice editedDeliveryPrice) {

    DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();

    LocalDate date = deliveryDocument.getDate();
    String contractorMpk = deliveryDocument.getContractor().getMpk();

    return contractorMpk.equals(editedDeliveryPrice.getContractor().getMpk())
        && deliveryDocumentEntry.getMaterial().getType().equals(editedDeliveryPrice.getMaterial().getType())
        && deliveryDocumentEntry.getDeliveryPrice().getPriceType() == editedDeliveryPrice.getPriceType()
        && deliveryDocumentEntry.getEstimatePosition().getCostCode().getProjectCode().equals(editedDeliveryPrice.getProjectCode())
        && (date.isAfter(editedDeliveryPrice.getStartDate()) || date.isEqual(editedDeliveryPrice.getStartDate()))
        && (date.isBefore(editedDeliveryPrice.getEndDate()) || date.isEqual(editedDeliveryPrice.getEndDate()));
  }
}
