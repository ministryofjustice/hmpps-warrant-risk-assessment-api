package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.WarrantRiskAssessmentEntity
import java.util.*

@Repository
interface WarrantRiskAssessmentRepository : JpaRepository<WarrantRiskAssessmentEntity, UUID> {
  fun findByCrn(crn: String): List<WarrantRiskAssessmentEntity>
  fun deleteByCrn(crn: String)
  fun findByCrnAndCompletedDateIsNull(crn: String?): List<WarrantRiskAssessmentEntity>
}
