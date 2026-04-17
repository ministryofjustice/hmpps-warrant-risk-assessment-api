package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.AddressEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.WarrantRiskAssessmentEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.enums.ReviewEventType
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.exception.NotFoundException
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Contact
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.CreateResponse
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.InitialiseWarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.WarrantRiskAssessmentRepository
import java.time.ZonedDateTime
import java.util.*

@Service
class WarrantRiskAssessmentService(
  val warrantRiskAssessmentRepository: WarrantRiskAssessmentRepository,
  val contactRepository: ContactRepository,
  val pdfGenerationService: PdfGenerationService,
  @Value("\${frontend.url}") val frontendUrl: String,
) {

  @Transactional
  fun initialiseWarrantRiskAssessment(initialiseWarrantRiskAssessment: InitialiseWarrantRiskAssessment) = warrantRiskAssessmentRepository.save(
    WarrantRiskAssessmentEntity(crn = initialiseWarrantRiskAssessment.crn),
  ).id.let {
    CreateResponse(it, "$frontendUrl/basic-details/$it")
  }

  fun findWarrantRiskAssessmentById(id: UUID): WarrantRiskAssessment {
    val warrantRiskAssessmentEntity: WarrantRiskAssessmentEntity = warrantRiskAssessmentRepository.findByIdOrNull(id) ?: throw NotFoundException(
      "WarrantRiskAssessmentEntity",
      "id",
      id,
    )
    return warrantRiskAssessmentEntity.toModel()
  }

  @Transactional
  fun updateWarrantRiskAssessment(id: UUID, warrantRiskAssessment: WarrantRiskAssessment): WarrantRiskAssessment {
    val warrantRiskAssessmentEntity: WarrantRiskAssessmentEntity = warrantRiskAssessmentRepository.findByIdOrNull(id) ?: throw NotFoundException("WarrantRiskAssessmentEntity", "id", id)
    return warrantRiskAssessmentRepository.save(warrantRiskAssessment.toEntity(warrantRiskAssessmentEntity)).toModel()
  }

  @Transactional
  fun deleteWarrantRiskAssessment(id: UUID): String {
    if (!warrantRiskAssessmentRepository.existsById(id)) {
      throw NotFoundException("WarrantRiskAssessmentEntity", "id", id)
    }
    val crn = findWarrantRiskAssessmentById(id).crn
    warrantRiskAssessmentRepository.deleteById(id)
    return crn
  }

  fun getWarrantRiskAssessmentAsPdf(id: UUID, warrantRiskAssessment: WarrantRiskAssessment?, draft: Boolean): ByteArray? {
    val html = pdfGenerationService.generateHtml(warrantRiskAssessment)

    var pdfBytes = pdfGenerationService.generatePdf(html)

    if (draft) {
      pdfBytes = pdfGenerationService.addWatermark(pdfBytes)
    }

    return pdfBytes
  }

  private fun WarrantRiskAssessment.toEntity(existingEntity: WarrantRiskAssessmentEntity? = null) = existingEntity?.copy(
    crn = crn,
    titleAndFullName = titleAndFullName,
    dateOfLetter = dateOfLetter,
    sheetSentBy = sheetSentBy,
    telephoneNumber = telephoneNumber,
    signature = signature,
    nationalInsuranceNumber = nationalInsuranceNumber,
    probationArea = probationArea,
    riskToPublicLevel = riskToPublicLevel,
    riskToEnforcementOfficers = riskToEnforcementOfficers,
    riskToPolice = riskToPolice,
    warrantExecutedBy = warrantExecutedBy,
    signOnOffice = signOnOffice,
    subjectOfMappaProcedures = subjectOfMappaProcedures,
    highRiskOfSelfHarm = highRiskOfSelfHarm,
    highRiskOfAbsconding = highRiskOfAbsconding,
    vulnerable = vulnerable,
    carryOrUseWeapons = carryOrUseWeapons,
    assaultingPolice = assaultingPolice,
    misuseDrugsAndAlcohol = misuseDrugsAndAlcohol,
    dateOfBirth = dateOfBirth,
    completedDate = completedDate,
    postalAddressEntity = postalAddress?.toEntity(),
    prisonNumber = prisonNumber,
    workAddressEntity = workAddress?.toEntity(),
    basicDetailsSaved = basicDetailsSaved,
    signAndSendSaved = signAndSendSaved,
    contactSaved = contactSaved,
    reviewRequiredDate = reviewRequiredDate,
    reviewEvent = reviewEvent,
    warrantRiskAssessmentContactList = warrantRiskAssessmentContactList.map {
      it.toEntity(
        existingEntity.warrantRiskAssessmentContactList.find { existingContactEntity ->
          existingContactEntity.id == it.id
        },
      )
    },
  )?.also { warrantRiskAssessment ->
    warrantRiskAssessment.warrantRiskAssessmentContactList.forEach { it.warrantRiskAssessment = warrantRiskAssessment }
  } ?: WarrantRiskAssessmentEntity(
    crn = crn,
    titleAndFullName = titleAndFullName,
    dateOfLetter = dateOfLetter,
    sheetSentBy = sheetSentBy,
    telephoneNumber = telephoneNumber,
    signature = signature,
    nationalInsuranceNumber = nationalInsuranceNumber,
    probationArea = probationArea,
    riskToPublicLevel = riskToPublicLevel,
    riskToEnforcementOfficers = riskToEnforcementOfficers,
    riskToPolice = riskToPolice,
    warrantExecutedBy = warrantExecutedBy,
    signOnOffice = signOnOffice,
    subjectOfMappaProcedures = subjectOfMappaProcedures,
    highRiskOfSelfHarm = highRiskOfSelfHarm,
    highRiskOfAbsconding = highRiskOfAbsconding,
    vulnerable = vulnerable,
    carryOrUseWeapons = carryOrUseWeapons,
    assaultingPolice = assaultingPolice,
    misuseDrugsAndAlcohol = misuseDrugsAndAlcohol,
    completedDate = completedDate,
    postalAddressEntity = postalAddress?.toEntity(),
    dateOfBirth = dateOfBirth,
    prisonNumber = prisonNumber,
    workAddressEntity = workAddress?.toEntity(),
    basicDetailsSaved = basicDetailsSaved,
    signAndSendSaved = signAndSendSaved,
    contactSaved = contactSaved,
    reviewRequiredDate = reviewRequiredDate,
    reviewEvent = reviewEvent,
    warrantRiskAssessmentContactList = warrantRiskAssessmentContactList.map { it.toEntity() },
  )

  private fun WarrantRiskAssessmentEntity.toModel() = WarrantRiskAssessment(
    crn = crn,
    titleAndFullName = titleAndFullName,
    dateOfLetter = dateOfLetter,
    sheetSentBy = sheetSentBy,
    telephoneNumber = telephoneNumber,
    signature = signature,
    nationalInsuranceNumber = nationalInsuranceNumber,
    probationArea = probationArea,
    riskToPublicLevel = riskToPublicLevel,
    riskToEnforcementOfficers = riskToEnforcementOfficers,
    riskToPolice = riskToPolice,
    warrantExecutedBy = warrantExecutedBy,
    signOnOffice = signOnOffice,
    subjectOfMappaProcedures = subjectOfMappaProcedures,
    highRiskOfSelfHarm = highRiskOfSelfHarm,
    highRiskOfAbsconding = highRiskOfAbsconding,
    vulnerable = vulnerable,
    carryOrUseWeapons = carryOrUseWeapons,
    assaultingPolice = assaultingPolice,
    misuseDrugsAndAlcohol = misuseDrugsAndAlcohol,
    completedDate = completedDate,
    postalAddress = postalAddressEntity?.toModel(),
    dateOfBirth = dateOfBirth,
    prisonNumber = prisonNumber,
    workAddress = workAddressEntity?.toModel(),
    basicDetailsSaved = basicDetailsSaved,
    signAndSendSaved = signAndSendSaved,
    contactSaved = contactSaved,
    reviewRequiredDate = reviewRequiredDate,
    reviewEvent = reviewEvent,
    warrantRiskAssessmentContactList = warrantRiskAssessmentContactList.map {
      it.toModel()
    },
  )

  private fun AddressEntity.toModel() = Address(
    deliusAddressId = deliusAddressId,
    officeDescription = officeDescription,
    status = status,
    buildingName = buildingName,
    addressNumber = addressNumber,
    streetName = streetName,
    district = district,
    townCity = townCity,
    county = county,
    postcode = postcode,
  )

  private fun Address.toEntity(existingEntity: AddressEntity? = null) = existingEntity?.copy(
    deliusAddressId = deliusAddressId,
    officeDescription = officeDescription,
    status = status,
    buildingName = buildingName,
    addressNumber = addressNumber,
    streetName = streetName,
    district = district,
    townCity = townCity,
    county = county,
    postcode = postcode,
  ) ?: AddressEntity(
    deliusAddressId = deliusAddressId,
    status = status,
    officeDescription = officeDescription,
    buildingName = buildingName,
    addressNumber = addressNumber,
    streetName = streetName,
    district = district,
    townCity = townCity,
    county = county,
    postcode = postcode,
  )

  private fun ContactEntity.toModel() = Contact(
    id = this.id,
    contactDate = this.contactDate,
    contactTypeDescription = this.contactTypeDescription,
    contactPerson = this.contactPerson,
    contactOutcome = this.contactOutcome,
    formSent = this.formSent,
    deliusContactId = this.deliusContactId,
    contactLocation = this.contactLocation?.toModel(),
  )

  private fun Contact.toEntity(existingEntity: ContactEntity? = null) = existingEntity?.copy(
    contactTypeDescription = contactTypeDescription,
    contactDate = contactDate,
    contactPerson = contactPerson,
    contactLocation = contactLocation?.toEntity(),
    contactOutcome = contactOutcome,
    formSent = formSent,
    deliusContactId = deliusContactId,
  ) ?: ContactEntity(
    contactTypeDescription = contactTypeDescription,
    contactDate = contactDate,
    contactPerson = contactPerson,
    contactLocation = contactLocation?.toEntity(),
    contactOutcome = this.contactOutcome,
    formSent = this.formSent,
    deliusContactId = this.deliusContactId,
  )

  fun getActiveWarrantRiskAssessmentsForCrn(crn: String?): Collection<WarrantRiskAssessmentEntity> = warrantRiskAssessmentRepository.findByCrnAndCompletedDateIsNull(crn)

  fun updateWarrantRiskAssessmentCrn(warrantRiskAssessment: WarrantRiskAssessmentEntity, crn: String) {
    warrantRiskAssessment.crn = crn
    warrantRiskAssessmentRepository.save(warrantRiskAssessment)
  }

  fun updateReviewEvent(eventType: ReviewEventType, warrantRiskAssessment: WarrantRiskAssessmentEntity, occurredAt: ZonedDateTime) {
    warrantRiskAssessment.reviewEvent = eventType.name
    warrantRiskAssessment.reviewRequiredDate = occurredAt.toLocalDateTime()
    warrantRiskAssessmentRepository.save(warrantRiskAssessment)
  }

  fun deleteAllByCrn(crn: String) {
    warrantRiskAssessmentRepository.deleteByCrn(crn)
  }
}
