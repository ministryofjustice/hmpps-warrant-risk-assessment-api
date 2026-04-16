package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model

import java.time.LocalDateTime
import java.util.*

data class Contact(
  val id: UUID? = null,
  val contactDate: LocalDateTime? = null,
  val contactTypeDescription: String? = null,
  val contactPerson: String? = null,
  val contactLocation: Address? = null,
)
