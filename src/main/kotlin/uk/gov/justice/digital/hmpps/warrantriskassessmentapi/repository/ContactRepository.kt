package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.ContactEntity
import java.util.*

@Repository
interface ContactRepository : JpaRepository<ContactEntity, UUID> {
  fun findFirstByWarrantRiskAssessmentIdAndId(warrantRiskAssessmentId: UUID, contactId: UUID): ContactEntity?
}
