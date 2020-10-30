package machineRental.MR.price;

public enum PriceType {

  ONLY_MACHINE("Price for machine only"),
  WITH_OPERATOR("Price for machine and operator"),
  WITH_FUEL("Price for machine and fuel"),
  WITH_OPERATOR_AND_FUEL("Price for machine, operator and fuel"),
  DISTANCE_KM("Price for distance unit of 1 km"),
  DISTANCE_M3("Price for road card transport of 1 m3 of material"),
  DISTANCE_MG("Price for road card transport of 1 Mg of material"),
  DISTANCE_RUN("Price for 1 run of vehicle recorded at road card"),
  TRANSPORT_M3("Price for transport of 1 m3 of material"),
  TRANSPORT_MG("Price for transport of 1 Mg of material"),
  TRANSPORT_KPL("Price for transport of 1 set of material"),
  TRANSPORT_SZT("Price for transport of 1 m3 piece material"),
  MATERIAL_M3("Price of 1 m3 of material"),
  MATERIAL_MG("Price of 1 Mg of material"),
  MATERIAL_KPL("Price of 1 set of material"),
  MATERIAL_SZT("Price of 1 piece of material"),
  MATERIAL_TRANSPORT_M3("Price of 1 m3 of material and its transport"),
  MATERIAL_TRANSPORT_MG("Price of 1 Mg of material and its transport"),
  MATERIAL_TRANSPORT_KPL("Price of 1 set of material and its transport"),
  MATERIAL_TRANSPORT_SZT("Price of 1 piece of material and its transport"),
  OTHER("Any other not precisely defined price");

  private String description;

  PriceType(final String description) {
    this.description = description;
  }
}
