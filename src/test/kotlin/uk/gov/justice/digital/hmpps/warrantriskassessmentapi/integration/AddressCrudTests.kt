package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.AddressRepository
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.WarrantRiskAssessmentRepository
import java.util.UUID

class AddressCrudTests : IntegrationTestBase() {

  @Autowired
  private lateinit var addressRepository: AddressRepository

  @Autowired
  private lateinit var warrantRiskAssessmentRepository: WarrantRiskAssessmentRepository

  @Test
  fun `should create an address`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X700001")

    val request = Address(
      deliusAddressId = 101,
      screen = "basic-details",
      warrantRiskAssessmentId = warrantRiskAssessmentId,
      status = "Main",
      buildingName = "North House",
      postcode = "AA1 1AA",
    )

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/address")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Address::class.java)
      .returnResult()
      .responseBody!!

    val persisted = addressRepository.findById(created.id!!).orElseThrow()
    assertThat(persisted.warrantRiskAssessmentId).isEqualTo(warrantRiskAssessmentId)
    assertThat(persisted.screen).isEqualTo("basic-details")
    assertThat(persisted.buildingName).isEqualTo("North House")
    assertThat(persisted.postcode).isEqualTo("AA1 1AA")
  }

  @Test
  fun `should get an address by id`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X700002")

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/address")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Address(
          deliusAddressId = 102,
          screen = "risk-summary",
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          status = "Postal",
          buildingName = "South House",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Address::class.java)
      .returnResult()
      .responseBody!!

    val fetched = webTestClient.get()
      .uri("/warrant-risk-assessment/address/${created.id}")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody(Address::class.java)
      .returnResult()
      .responseBody!!

    assertThat(fetched.id).isEqualTo(created.id)
    assertThat(fetched.screen).isEqualTo("risk-summary")
    assertThat(fetched.buildingName).isEqualTo("South House")
  }

  @Test
  fun `should update an address`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X700003")

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/address")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Address(
          deliusAddressId = 103,
          screen = "contact",
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          status = "Main",
          buildingName = "Old Building",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Address::class.java)
      .returnResult()
      .responseBody!!

    webTestClient.put()
      .uri("/warrant-risk-assessment/address/${created.id}")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Address(
          id = created.id,
          deliusAddressId = 103,
          screen = "contact",
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          status = "Postal",
          buildingName = "Updated Building",
          postcode = "ZZ9 9ZZ",
        ),
      )
      .exchange()
      .expectStatus()
      .isOk

    val persisted = addressRepository.findById(created.id!!).orElseThrow()
    assertThat(persisted.status).isEqualTo("Postal")
    assertThat(persisted.buildingName).isEqualTo("Updated Building")
    assertThat(persisted.postcode).isEqualTo("ZZ9 9ZZ")
  }

  @Test
  fun `should get addresses by WRA id and screen`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X700004")

    webTestClient.post()
      .uri("/warrant-risk-assessment/address")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Address(
          deliusAddressId = 104,
          screen = "sign-and-send",
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          buildingName = "Included",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated

    webTestClient.post()
      .uri("/warrant-risk-assessment/address")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Address(
          deliusAddressId = 105,
          screen = "basic-details",
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          buildingName = "Excluded",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated

    webTestClient.get()
      .uri("/warrant-risk-assessment/address/byWRAIdAndPage/$warrantRiskAssessmentId/sign-and-send")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(1)
      .jsonPath("$[0].screen").isEqualTo("sign-and-send")
      .jsonPath("$[0].buildingName").isEqualTo("Included")
  }

  @Test
  fun `should delete an address`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X700005")

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/address")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Address(
          deliusAddressId = 106,
          screen = "risk-summary",
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          buildingName = "Delete Me",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Address::class.java)
      .returnResult()
      .responseBody!!

    webTestClient.delete()
      .uri("/warrant-risk-assessment/address/${created.id}")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk

    assertThat(addressRepository.findById(created.id!!)).isEmpty
  }

  private fun createWarrantRiskAssessment(crn: String): UUID {
    webTestClient.post()
      .uri("/warrant-risk-assessment")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(WarrantRiskAssessment(crn = crn))
      .exchange()
      .expectStatus()
      .isCreated

    return warrantRiskAssessmentRepository.findByCrn(crn).single().id
  }
}
