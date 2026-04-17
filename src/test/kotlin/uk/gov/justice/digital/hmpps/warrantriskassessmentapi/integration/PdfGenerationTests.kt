package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ContentDisposition
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.WarrantRiskAssessmentRepository
import java.time.Duration
import java.util.UUID

class PdfGenerationTests : IntegrationTestBase() {

  @Autowired
  private lateinit var warrantRiskAssessmentRepository: WarrantRiskAssessmentRepository

  @Test
  fun `get PDF should return a 200 response`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT")))
      .bodyValue(WarrantRiskAssessment(crn = "X800001"))
      .exchange()
      .expectStatus()
      .isCreated

    val warrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X800001")
    assertThat(warrantRiskAssessment.first().crn).isEqualTo("X800001")

    webTestClient
      .mutate().responseTimeout(Duration.ofSeconds(30)).build()
      .get()
      .uri("/warrant-risk-assessment/" + warrantRiskAssessment[0].id + "/pdf")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT")))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader()
      .contentType(MediaType.APPLICATION_PDF)
      .expectHeader()
      .contentDisposition(ContentDisposition.attachment().filename("Warrant_Risk_Assessment_" + warrantRiskAssessment[0].crn + ".pdf").build())
  }

  @Test
  fun `get PDF should return a 404 response if not found`() {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT")))
      .bodyValue(WarrantRiskAssessment(crn = "X800002"))
      .exchange()
      .expectStatus()
      .isCreated

    val warrantRiskAssessment = warrantRiskAssessmentRepository.findByCrn("X800002")
    assertThat(warrantRiskAssessment.first().crn).isEqualTo("X800002")

    webTestClient.get()
      .uri("/warrant-risk-assessment/" + UUID.randomUUID() + "/pdf")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT")))
      .exchange()
      .expectStatus().isNotFound
  }
}
