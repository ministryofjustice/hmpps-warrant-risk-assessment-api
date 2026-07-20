package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model

import java.util.*

data class Contact(
  val id: UUID? = null,
  val warrantRiskAssessmentId: UUID,
  val contactPerson: String? = null,
  val contactLocation: Address? = null,
  val telephoneNumber: String? = null,
  val mobileNumber: String? = null,
)
