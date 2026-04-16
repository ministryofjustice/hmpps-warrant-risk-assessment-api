package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model

import jakarta.validation.constraints.Pattern

data class InitialiseWarrantRiskAssessment(
  @field:Pattern(regexp = "^[A-Z][0-9]{6}")
  val crn: String,
)
