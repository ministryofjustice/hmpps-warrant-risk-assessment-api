package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model

data class Address(
  val deliusAddressId: Long?,
  val status: String? = null,
  val officeDescription: String? = null,
  val buildingName: String? = null,
  val addressNumber: String? = null,
  val streetName: String? = null,
  val district: String? = null,
  val townCity: String? = null,
  val county: String? = null,
  val postcode: String? = null,
)
