package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.listener
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.WarrantRiskAssessmentEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.enums.ReviewEventType
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.DomainEventsMessage
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service.NDeliusIntegrationService
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service.WarrantRiskAssessmentService
import java.time.ZonedDateTime

@Service
class DomainEventsListener(
  private val warrantRiskAssessmentService: WarrantRiskAssessmentService,
  private val objectMapper: ObjectMapper,
  private val nDeliusIntegrationService: NDeliusIntegrationService,
) {

  @Transactional
  @SqsListener("hmppswarrantriskassessmentqueue", factory = "hmppsQueueContainerFactoryProxy")
  fun listen(msg: String) {
    val (message, attributes) = objectMapper.readValue<SQSMessage>(msg)
    val domainEventMessage = objectMapper.readValue<DomainEventsMessage>(message)
    handleMessage(domainEventMessage)
  }

  private fun handleMessage(message: DomainEventsMessage) {
    when (message.eventType) {
      "probation-case.merge.completed" -> {
        // Update CRNs where appropriate
        val warrantRiskAssessments = warrantRiskAssessmentService.getActiveWarrantRiskAssessmentsForCrn(message.sourceCrn)
        warrantRiskAssessments.forEach {
          warrantRiskAssessmentService.updateWarrantRiskAssessmentCrn(it, requireNotNull(message.targetCrn))
        }

        updateReviewEvent(ReviewEventType.MERGE, warrantRiskAssessments, message.occurredAt)
      }

      "probation-case.unmerge.completed" -> {
        // Update CRNs where appropriate
        val warrantRisks = warrantRiskAssessmentService.getActiveWarrantRiskAssessmentsForCrn(message.unmergedCrn)
        warrantRisks.forEach {
          nDeliusIntegrationService.getCrnForWarrantRiskAssessmentUuid(it.id.toString())?.crn?.let { crn ->
            warrantRiskAssessmentService.updateWarrantRiskAssessmentCrn(
              it,
              crn,
            )
          }
        }

        updateReviewEvent(ReviewEventType.UNMERGE, warrantRisks, message.occurredAt)
      }

      "probation-case.sentence.moved" -> {
        // Update CRNs where appropriate
        val warrantRisks = warrantRiskAssessmentService.getActiveWarrantRiskAssessmentsForCrn(message.sourceCrn)
        warrantRisks.forEach {
          nDeliusIntegrationService.getCrnForWarrantRiskAssessmentUuid(it.id.toString())?.crn?.let { crn ->
            warrantRiskAssessmentService.updateWarrantRiskAssessmentCrn(
              it,
              crn,
            )
          }
        }

        updateReviewEvent(ReviewEventType.EVENT_MOVE, warrantRisks, message.occurredAt)
      }

      "probation-case.deleted.gdpr" -> {
        message.crn?.let { warrantRiskAssessmentService.deleteAllByCrn(it) }
      }

      "probation-case.non-statutory-intervention.moved" -> {
        // Update CRNs where appropriate
        val warrantRisks = warrantRiskAssessmentService.getActiveWarrantRiskAssessmentsForCrn(message.sourceCrn)
        warrantRisks.forEach {
          nDeliusIntegrationService.getCrnForWarrantRiskAssessmentUuid(it.id.toString())?.crn?.let { crn ->
            warrantRiskAssessmentService.updateWarrantRiskAssessmentCrn(
              it,
              crn,
            )
          }
        }

        updateReviewEvent(ReviewEventType.MOVE_NSI, warrantRisks, message.occurredAt)
      }
    }
  }

  private fun updateReviewEvent(eventType: ReviewEventType, warrantRisks: Collection<WarrantRiskAssessmentEntity>, occurredAt: ZonedDateTime) {
    warrantRisks.forEach { warrantRisk -> warrantRiskAssessmentService.updateReviewEvent(eventType, warrantRisk, occurredAt) }
  }
}

data class SQSMessage(
  @JsonProperty("Message") val message: String,
  @JsonProperty("MessageAttributes") val attributes: MessageAttributes = MessageAttributes(),
)

data class MessageAttributes(
  @JsonAnyGetter @JsonAnySetter
  private val attributes: MutableMap<String, MessageAttribute> = mutableMapOf(),
) : MutableMap<String, MessageAttribute> by attributes {

  val eventType = attributes[EVENT_TYPE_KEY]?.value

  companion object {
    private const val EVENT_TYPE_KEY = "eventType"
  }
}

data class MessageAttribute(@JsonProperty("Type") val type: String, @JsonProperty("Value") val value: String)
