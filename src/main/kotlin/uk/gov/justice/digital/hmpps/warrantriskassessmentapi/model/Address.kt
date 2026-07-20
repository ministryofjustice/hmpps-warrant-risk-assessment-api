package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model

import java.util.UUID

data class Address(
  val id: UUID? = null,
  val deliusAddressId: Long?,
  val screen: String = "",
  val warrantRiskAssessmentId: UUID,
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
