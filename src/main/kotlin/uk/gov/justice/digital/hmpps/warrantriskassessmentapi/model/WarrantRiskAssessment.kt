package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

data class WarrantRiskAssessment(
  @field:Pattern(regexp = "^[A-Z][0-9]{6}")
  var crn: String,
  var titleAndFullName: String? = null,
  var dateOfLetter: LocalDate? = null,
  var sheetSentBy: String? = null,
  var telephoneNumber: String? = null,
  var signature: String? = null,
  var nationalInsuranceNumber: String? = null,
  var probationArea: String? = null,
  var riskToPublicLevel: String? = null,
  var riskToEnforcementOfficers: String? = null,
  var riskToPolice: String? = null,
  var warrantExecutedBy: String? = null,
  var signOnOffice: UUID? = null,
  var subjectOfMappaProcedures: Boolean? = null,
  var highRiskOfSelfHarm: Boolean? = null,
  var highRiskOfAbsconding: Boolean? = null,
  var vulnerable: Boolean? = null,
  var carryOrUseWeapons: Boolean? = null,
  var assaultingPolice: Boolean? = null,
  var misuseDrugsAndAlcohol: Boolean? = null,
  var completedDate: ZonedDateTime? = null,
  var postalAddress: Address? = null,
  var dateOfBirth: LocalDate? = null,
  var prisonNumber: String? = null,
  var workAddress: Address? = null,
  var basicDetailsSaved: Boolean? = null,
  var signAndSendSaved: Boolean? = null,
  var contactSaved: Boolean? = null,
  var reviewRequiredDate: LocalDateTime? = null,
  var reviewEvent: String? = null,
  @field:JsonSetter(nulls = Nulls.AS_EMPTY)
  var warrantRiskAssessmentContactList: List<Contact> = emptyList(),
)
