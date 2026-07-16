package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.AddressEntity
import java.util.*

@Repository
interface AddressRepository : JpaRepository<AddressEntity, UUID> {
  fun findByWarrantRiskAssessmentIdAndScreen(id: UUID, screen: String): MutableList<AddressEntity>
}
