package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Address
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service.AddressService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.util.UUID

@Validated
@RestController
@PreAuthorize("hasRole('ROLE_WARRANT_RISK_ASSESSMENT__RW')")
@RequestMapping(value = ["/warrant-risk-assessment/address"], produces = ["application/json"])
class AddressController(
  private val addressService: AddressService,
) {

  @GetMapping("/{uuid}")
  @Operation(
    summary = "Retrieve an address by uuid",
    description = "Calls through the warrant risk assessment service to retrieve an address",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "warrant risk assessment address returned"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun getAddressById(@PathVariable uuid: UUID): Address? = addressService.findAddressById(uuid)

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create an address record",
    description = "Creates a new address record",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "Address record created"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
    ],
  )
  fun initialiseAddress(@Valid @RequestBody address: Address) = addressService.createAddress(address)

  @PutMapping("/{id}")
  @Operation(
    summary = "Update an address record",
    description = "Updates an existing address record",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "Address record updated"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
      ApiResponse(responseCode = "404", description = "Address record not found"),
    ],
  )
  fun updateAddress(@PathVariable id: UUID, @RequestBody address: Address) = addressService.updateAddress(id, address)

  @DeleteMapping("/{id}")
  @Operation(
    summary = "Delete an address record",
    description = "Deletes an address record from a WRA record",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "Address record deleted"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
      ApiResponse(responseCode = "404", description = "Address record not found"),
    ],
  )
  fun deleteAddress(@PathVariable id: UUID) {
    addressService.deleteAddress(id)
  }

  @GetMapping("/byWRAIdAndPage/{uuid}/{screen}")
  @Operation(
    summary = "Fetch addresses by WRA ID and screen",
    description = "Calls through the warrant risk assessment service to retrieve a set of addresses using WRA id and screen",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "addresses returned"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
      ApiResponse(responseCode = "404", description = "WRA ID not found"),
    ],
  )
  fun getAddresses(@PathVariable uuid: UUID, @PathVariable screen: String): List<Address> = addressService.findAddressesByIdAndScreen(uuid, screen)
}
