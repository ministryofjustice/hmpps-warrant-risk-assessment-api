package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.entity.AddressEntity
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.exception.NotFoundException
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.repository.AddressRepository
import java.util.*

@Service
class AddressService(
  private val addressRepository: AddressRepository,
  @Value("\${frontend.url}") val frontendUrl: String,
) {

  @Transactional
  fun createAddress(address: Address): Address {
    val entity = address.toEntity()
    return addressRepository.save(entity).toModel()
  }

  fun findAddressById(id: UUID): Address {
    val addressEntity: AddressEntity = addressRepository.findByIdOrNull(id) ?: throw NotFoundException(
      "AddressEntity",
      "id",
      id,
    )
    return addressEntity.toModel()
  }

  @Transactional
  fun updateAddress(id: UUID, address: Address): Address {
    val addressEntity: AddressEntity = addressRepository.findByIdOrNull(id) ?: throw NotFoundException("AddressEntity", "id", id)
    return addressRepository.save(address.toEntity(addressEntity)).toModel()
  }

  @Transactional
  fun deleteAddress(id: UUID) {
    if (!addressRepository.existsById(id)) {
      throw NotFoundException("AddressEntity", "id", id)
    }
    addressRepository.deleteById(id)
  }

  fun findAddressesByIdAndScreen(id: UUID, screen: String): MutableList<Address> {
    val addressList = addressRepository.findByWarrantRiskAssessmentIdAndScreen(id, screen)
    return addressList.map { it.toModel() }.toMutableList()
  }

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
