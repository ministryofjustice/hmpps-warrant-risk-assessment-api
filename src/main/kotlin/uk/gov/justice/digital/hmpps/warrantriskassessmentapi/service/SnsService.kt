package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessagingException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.DomainEventsMessage
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Identifiers
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.PersonReference
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingQueueException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class SnsService(
  val hmppsQueueService: HmppsQueueService,
  val objectMapper: ObjectMapper,
  @Value("\${hmpps.sqs.topics.hmppswarrantriskassessmentpublishtopic.arn}") val outboundTopicArn: String,
) {
  fun sendPublishDomainEvent(warrantRiskAssessment: WarrantRiskAssessment, id: UUID) {
    val outboundTopic = hmppsQueueService.findByTopicId("hmppswarrantriskassessmentpublishtopic") ?: throw MissingQueueException("HmppsTopic hmppswarrantriskassessmentpublishtopic not found")
    val messageObject = DomainEventsMessage(
      description = "A warrant risk assessment has been completed for a person on probation",
      version = 1,
      occurredAt = ZonedDateTime.now(ZoneId.of("Europe/London")),
      eventType = "probation-case.warrant-risk-assessment.created",
      personReference = PersonReference(listOf(Identifiers(type = "crn", value = warrantRiskAssessment.crn))),
      detailUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/warrant-risk-assessment/" + id + "/pdf",
      additionalInformation = mapOf(
        "warrantRiskAssessmentId" to id,
        "username" to SecurityContextHolder.getContext().authentication!!.name,
      ),

    )
    val publishResponse = outboundTopic.snsClient.publish(
      PublishRequest.builder().topicArn(outboundTopicArn).message(objectMapper.writeValueAsString(messageObject)).messageAttributes(
        mapOf("eventType" to MessageAttributeValue.builder().dataType("String").stringValue("probation-case.warrant-risk-assessment.created").build()),
      ).build(),
    )

    publishResponse.get(5, TimeUnit.SECONDS).messageId() ?: throw MessagingException("Unable to publish creation message")
  }
  fun sendDeleteDomainEvent(crn: String, id: UUID) {
    val outboundTopic = hmppsQueueService.findByTopicId("hmppswarrantriskassessmentpublishtopic")
      ?: throw MissingQueueException("HmppsTopic hmppswarrantriskassessmentpublishtopic not found")
    val messageObject = DomainEventsMessage(
      description = "A warrant risk assessment has been deleted",
      version = 1,
      occurredAt = ZonedDateTime.now(ZoneId.of("Europe/London")),
      eventType = "probation-case.warrant-risk-assessment.deleted",
      personReference = PersonReference(listOf(Identifiers(type = "crn", value = crn))),
      detailUrl = null,
      additionalInformation = mapOf(
        "warrantRiskAssessmentId" to id,
        "username" to SecurityContextHolder.getContext().authentication!!.name,
      ),

    )
    val publishResponse = outboundTopic.snsClient.publish(
      PublishRequest.builder().topicArn(outboundTopicArn).message(objectMapper.writeValueAsString(messageObject))
        .messageAttributes(
          mapOf(
            "eventType" to MessageAttributeValue.builder().dataType("String")
              .stringValue("probation-case.warrant-risk-assessment.deleted").build(),
          ),
        ).build(),
    )

    publishResponse.get(5, TimeUnit.SECONDS).messageId()
      ?: throw MessagingException("Unable to publish creation message")
  }
}
