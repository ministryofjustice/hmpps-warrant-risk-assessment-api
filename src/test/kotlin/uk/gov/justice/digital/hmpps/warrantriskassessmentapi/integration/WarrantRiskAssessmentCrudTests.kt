package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.WarrantRiskAssessmentRepository
import java.time.LocalDate
import java.time.ZonedDateTime

class WarrantRiskAssessmentCrudTests : IntegrationTestBase() {

  @Autowired
  private lateinit var warrantRiskAssessmentRepository: WarrantRiskAssessmentRepository

  @Test
  fun `should create a warrant risk assessment`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(WarrantRiskAssessment(crn = "X000001"))
      .exchange()
      .expectStatus()
      .isCreated

    val warrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X000001").single()
    assertThat(warrantRiskAssessment.crn).isEqualTo("X000001")
    assertThat(warrantRiskAssessment.id).isNotNull()
  }

  @Test
  fun `should update a Warrant Risk Assessment`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(WarrantRiskAssessment(crn = "X000002"))
      .exchange()
      .expectStatus()
      .isCreated

    val warrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X000002").single()
    assertThat(warrantRiskAssessment.crn).isEqualTo("X000002")

    val warrantRiskAssessmentBody = WarrantRiskAssessment(
      crn = "X000002",
      sheetSentBy = "Joe Bloggs",
      titleAndFullName = "Mr Henry Bean",
      postalAddress = Address(
        deliusAddressId = 25,
        status = "Postal",
        officeDescription = null,
        buildingName = "MOO",
        warrantRiskAssessmentId = warrantRiskAssessment.id,
      ),
      completedDate = ZonedDateTime.now(),
      reviewEvent = "Merge",
      reviewRequiredDate = ZonedDateTime.now(),
      basicDetailsSaved = true,
      contactSaved = false,
      signAndSendSaved = false,
      riskAssessmentSaved = true,
      riskSummarySaved = false,
      prisonNumber = "123456",
      dateOfLetter = LocalDate.now(),
      telephoneNumber = "01911234560",
      signature = "testsignature",
      nationalInsuranceNumber = "A123",
      probationArea = "Probation Area",
      riskToPublicLevel = "Risk to Public Level",
      riskToEnforcementOfficers = "Risk to Enforcement Officers",
      riskToPolice = "Risk to police",
      warrantExecutedBy = "Warrant Executed By",
      signOnOffice = Address(
        deliusAddressId = 67,
        status = "Main",
        officeDescription = "signOnOfficeDescription",
        buildingName = "SOD",
        warrantRiskAssessmentId = warrantRiskAssessment.id,
      ),
      subjectOfMappaProcedures = false,
      highRiskOfSelfHarm = false,
      highRiskOfAbsconding = false,
      vulnerable = true,
      carryOrUseWeapons = true,
      assaultingPolice = false,
      misuseDrugsAndAlcohol = true,
      dateOfBirth = LocalDate.now(),
      workAddress = Address(
        deliusAddressId = 66,
        status = "Postal",
        officeDescription = "anOfficeDescription",
        buildingName = "MOO",
        warrantRiskAssessmentId = warrantRiskAssessment.id,
      ),
    )

    webTestClient.put()
      .uri("/warrant-risk-assessment/" + warrantRiskAssessment.id)
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(warrantRiskAssessmentBody)
      .exchange()
      .expectStatus()
      .isOk

    val updatedWarrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X000002").single()
    assertThat(updatedWarrantRiskAssessment.crn).isEqualTo("X000002")
    assertThat(updatedWarrantRiskAssessment.reviewEvent).isEqualTo("Merge")
    assertThat(updatedWarrantRiskAssessment.nationalInsuranceNumber).isEqualTo("A123")
    assertThat(updatedWarrantRiskAssessment.basicDetailsSaved).isEqualTo(true)
    assertThat(updatedWarrantRiskAssessment.riskAssessmentSaved).isEqualTo(true)
    assertThat(updatedWarrantRiskAssessment.riskSummarySaved).isEqualTo(false)
    assertThat(updatedWarrantRiskAssessment.workAddressEntity?.officeDescription).isEqualTo("anOfficeDescription")
  }

  @Test
  fun `should update a Warrant Risk Assessment to completed`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(WarrantRiskAssessment(crn = "X600002"))
      .exchange()
      .expectStatus()
      .isCreated

    val warrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X600002").single()
    assertThat(warrantRiskAssessment.crn).isEqualTo("X600002")

    val warrantRiskAssessmentBody = WarrantRiskAssessment(
      crn = "X600002",
      sheetSentBy = "Joe Bloggs",
      titleAndFullName = "Mr Henry Bean",
      postalAddress = Address(
        deliusAddressId = 25,
        status = "Postal",
        officeDescription = null,
        buildingName = "MOO",
        warrantRiskAssessmentId = warrantRiskAssessment.id,
      ),
      completedDate = ZonedDateTime.now(),
      reviewEvent = "Merge",
      reviewRequiredDate = ZonedDateTime.now(),
      basicDetailsSaved = true,
      contactSaved = false,
      signAndSendSaved = false,
      riskAssessmentSaved = true,
      riskSummarySaved = false,
      prisonNumber = "123456",
      dateOfLetter = LocalDate.now(),
      telephoneNumber = "01911234560",
      signature = "testsignature",
      nationalInsuranceNumber = "A123",
      probationArea = "Probation Area",
      riskToPublicLevel = "Risk to Public Level",
      riskToEnforcementOfficers = "Risk to Enforcement Officers",
      riskToPolice = "Risk to police",
      warrantExecutedBy = "Warrant Executed By",
      signOnOffice = Address(
        deliusAddressId = 67,
        status = "Main",
        officeDescription = "signOnOfficeDescription",
        buildingName = "SOD",
        warrantRiskAssessmentId = warrantRiskAssessment.id,
      ),
      subjectOfMappaProcedures = false,
      highRiskOfSelfHarm = false,
      highRiskOfAbsconding = false,
      vulnerable = true,
      carryOrUseWeapons = true,
      assaultingPolice = false,
      misuseDrugsAndAlcohol = true,
      dateOfBirth = LocalDate.now(),
      workAddress = Address(
        deliusAddressId = 66,
        status = "Postal",
        officeDescription = "anOfficeDescription",
        buildingName = "MOO",
        warrantRiskAssessmentId = warrantRiskAssessment.id,
      ),
    )

    webTestClient.put()
      .uri("/warrant-risk-assessment/" + warrantRiskAssessment.id)
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(warrantRiskAssessmentBody)
      .exchange()
      .expectStatus()
      .isOk

    val updatedWarrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X600002").single()
    assertThat(updatedWarrantRiskAssessment.crn).isEqualTo("X600002")
    assertThat(updatedWarrantRiskAssessment.reviewEvent).isEqualTo("Merge")
    assertThat(updatedWarrantRiskAssessment.nationalInsuranceNumber).isEqualTo("A123")
    assertThat(updatedWarrantRiskAssessment.basicDetailsSaved).isEqualTo(true)
    assertThat(updatedWarrantRiskAssessment.riskAssessmentSaved).isEqualTo(true)
    assertThat(updatedWarrantRiskAssessment.riskSummarySaved).isEqualTo(false)
    assertThat(updatedWarrantRiskAssessment.workAddressEntity?.officeDescription).isEqualTo("anOfficeDescription")
    assertThat(updatedWarrantRiskAssessment.completedDate).isNotNull()
  }

  @Test
  fun `should fail to create if the crn is too long`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(WarrantRiskAssessment(crn = "X000001123456789123456"))
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.userMessage").isEqualTo("""Field: crn - must match "^[A-Z][0-9]{6}"""")
  }

  @Test
  fun `should delete a warrant risk assessment`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(WarrantRiskAssessment(crn = "X000004"))
      .exchange()
      .expectStatus()
      .isCreated

    val warrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X000004")
    assertThat(warrantRiskAssessment.first().crn).isEqualTo("X000004")
    assertThat(warrantRiskAssessment.first().id).isNotNull()

    webTestClient.delete()
      .uri("/warrant-risk-assessment/" + warrantRiskAssessment.first().id)
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk

    assertThat(warrantRiskAssessmentRepository.findById(warrantRiskAssessment.first().id)).isEmpty
  }
}
