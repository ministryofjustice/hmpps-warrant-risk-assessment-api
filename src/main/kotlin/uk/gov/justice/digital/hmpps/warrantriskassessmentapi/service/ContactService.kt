package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.AddressEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.exception.NotFoundException
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Contact
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.ContactRepository
import java.util.*

@Service
class ContactService(
  private val contactRepository: ContactRepository,
  @Value("\${frontend.url}") val frontendUrl: String,
) {

  @Transactional
  fun createContact(contact: Contact): Contact {
    val entity = contact.toEntity()
    return contactRepository.save(entity).toModel()
  }

  fun findContactById(id: UUID): Contact {
    val contactEntity: ContactEntity = contactRepository.findByIdOrNull(id) ?: throw NotFoundException(
      "ContactEntity",
      "id",
      id,
    )
    return contactEntity.toModel()
  }

  @Transactional
  fun updateContact(id: UUID, contact: Contact): Contact {
    val contactEntity: ContactEntity = contactRepository.findByIdOrNull(id) ?: throw NotFoundException("ContactEntity", "id", id)
    return contactRepository.save(contact.toEntity(contactEntity)).toModel()
  }

  @Transactional
  fun deleteContact(id: UUID) {
    if (!contactRepository.existsById(id)) {
      throw NotFoundException("ContactEntity", "id", id)
    }
    contactRepository.deleteById(id)
  }

  fun findContactsByWraId(id: UUID): MutableList<Contact> {
    val contactList = contactRepository.findByWarrantRiskAssessmentId(id)
    return contactList.map { it.toModel() }.toMutableList()
  }

  private fun ContactEntity.toModel() = Contact(
    id = this.id,
    contactPerson = this.contactPerson,
    telephoneNumber = this.telephoneNumber,
    mobileNumber = this.mobileNumber,
    contactLocation = this.contactLocation?.toModel(),
    warrantRiskAssessmentId = this.warrantRiskAssessmentId,
  )

  private fun Contact.toEntity(existingEntity: ContactEntity? = null) = existingEntity?.copy(
    contactPerson = contactPerson,
    telephoneNumber = telephoneNumber,
    mobileNumber = mobileNumber,
    contactLocation = contactLocation?.toEntity(),
  ) ?: ContactEntity(
    contactPerson = contactPerson,
    telephoneNumber = telephoneNumber,
    mobileNumber = mobileNumber,
    contactLocation = contactLocation?.toEntity(),
    warrantRiskAssessmentId = this.warrantRiskAssessmentId,
  )

  private fun AddressEntity.toModel() = Address(
    id = this.id,
    deliusAddressId = deliusAddressId,
    screen = screen,
    officeDescription = officeDescription,
    status = status,
    buildingName = buildingName,
    addressNumber = addressNumber,
    streetName = streetName,
    district = district,
    townCity = townCity,
    county = county,
    postcode = postcode,
    warrantRiskAssessmentId = warrantRiskAssessmentId,
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
    warrantRiskAssessmentId = warrantRiskAssessmentId,
  ) ?: AddressEntity(
    deliusAddressId = deliusAddressId,
    screen = screen,
    status = status,
    officeDescription = officeDescription,
    buildingName = buildingName,
    addressNumber = addressNumber,
    streetName = streetName,
    district = district,
    townCity = townCity,
    county = county,
    postcode = postcode,
    warrantRiskAssessmentId = warrantRiskAssessmentId,
  )
}
