package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Contact
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.WarrantRiskAssessmentRepository
import java.util.UUID

class ContactCrudTests : IntegrationTestBase() {

  @Autowired
  private lateinit var contactRepository: ContactRepository

  @Autowired
  private lateinit var warrantRiskAssessmentRepository: WarrantRiskAssessmentRepository

  @Test
  fun `should create a contact`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X710001")

    val request = Contact(
      warrantRiskAssessmentId = warrantRiskAssessmentId,
      contactPerson = "Alex Officer",
      telephoneNumber = "02071234567",
      mobileNumber = "07700123456",
      contactLocation = Address(
        deliusAddressId = 201,
        screen = "contact",
        warrantRiskAssessmentId = warrantRiskAssessmentId,
        buildingName = "Contact House",
      ),
    )

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/contact")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Contact::class.java)
      .returnResult()
      .responseBody!!

    val persisted = contactRepository.findById(created.id!!).orElseThrow()
    assertThat(persisted.contactPerson).isEqualTo("Alex Officer")
    assertThat(persisted.telephoneNumber).isEqualTo("02071234567")
    assertThat(persisted.mobileNumber).isEqualTo("07700123456")
    assertThat(persisted.contactLocation?.buildingName).isEqualTo("Contact House")
  }

  @Test
  fun `should get a contact by id`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X710002")

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/contact")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Contact(
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          contactPerson = "Jamie Caseworker",
          telephoneNumber = "01131234567",
          mobileNumber = "07900111111",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Contact::class.java)
      .returnResult()
      .responseBody!!

    val fetched = webTestClient.get()
      .uri("/warrant-risk-assessment/contact/${created.id}")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody(Contact::class.java)
      .returnResult()
      .responseBody!!

    assertThat(fetched.id).isEqualTo(created.id)
    assertThat(fetched.contactPerson).isEqualTo("Jamie Caseworker")
    assertThat(fetched.telephoneNumber).isEqualTo("01131234567")
    assertThat(fetched.mobileNumber).isEqualTo("07900111111")
  }

  @Test
  fun `should update a contact`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X710003")

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/contact")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Contact(
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          contactPerson = "Original Person",
          telephoneNumber = "01411234567",
          mobileNumber = "07111111111",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Contact::class.java)
      .returnResult()
      .responseBody!!

    webTestClient.put()
      .uri("/warrant-risk-assessment/contact/${created.id}")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Contact(
          id = created.id,
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          contactPerson = "Updated Person",
          telephoneNumber = "01611234567",
          mobileNumber = "07222222222",
        ),
      )
      .exchange()
      .expectStatus()
      .isOk

    val persisted = contactRepository.findById(created.id!!).orElseThrow()
    assertThat(persisted.contactPerson).isEqualTo("Updated Person")
    assertThat(persisted.telephoneNumber).isEqualTo("01611234567")
    assertThat(persisted.mobileNumber).isEqualTo("07222222222")
  }

  @Test
  fun `should get contacts by WRA id`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X710004")

    webTestClient.post()
      .uri("/warrant-risk-assessment/contact")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Contact(
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          contactPerson = "First Contact",
          telephoneNumber = "01311234567",
          mobileNumber = "07333333333",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated

    webTestClient.post()
      .uri("/warrant-risk-assessment/contact")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Contact(
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          contactPerson = "Second Contact",
          telephoneNumber = "01511234567",
          mobileNumber = "07444444444",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated

    webTestClient.get()
      .uri("/warrant-risk-assessment/contact/byWRAId/$warrantRiskAssessmentId")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(2)
      .jsonPath("$[0].warrantRiskAssessmentId").isEqualTo(warrantRiskAssessmentId.toString())
  }

  @Test
  fun `should delete a contact`() {
    val warrantRiskAssessmentId = createWarrantRiskAssessment("X710005")

    val created = webTestClient.post()
      .uri("/warrant-risk-assessment/contact")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .bodyValue(
        Contact(
          warrantRiskAssessmentId = warrantRiskAssessmentId,
          contactPerson = "Delete Contact",
          telephoneNumber = "01911234567",
          mobileNumber = "07555555555",
        ),
      )
      .exchange()
      .expectStatus()
      .isCreated
      .expectBody(Contact::class.java)
      .returnResult()
      .responseBody!!

    webTestClient.delete()
      .uri("/warrant-risk-assessment/contact/${created.id}")
      .headers(setAuthorisation(roles = listOf("ROLE_WARRANT_RISK_ASSESSMENT__RW")))
      .exchange()
      .expectStatus()
      .isOk

    assertThat(contactRepository.findById(created.id!!)).isEmpty
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
