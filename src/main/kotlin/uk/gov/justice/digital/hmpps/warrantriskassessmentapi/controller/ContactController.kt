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
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.Contact
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service.ContactService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.util.UUID

@Validated
@RestController
 @PreAuthorize("hasRole('ROLE_WARRANT_RISK_ASSESSMENT__RW')")
@RequestMapping(value = ["/warrant-risk-assessment/contact"], produces = ["application/json"])
class ContactController(
  private val contactService: ContactService,
) {

  @GetMapping("/{uuid}")
  @Operation(
    summary = "Retrieve a contact by uuid",
    description = "Calls through the warrant risk assessment service to retrieve a contact",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "warrant risk assessment contact returned"),
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
  fun getContactById(@PathVariable uuid: UUID): Contact? = contactService.findContactById(uuid)

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create an contact record",
    description = "Creates a new contact record",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "Contact record created"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
    ],
  )
  fun initialiseContact(@Valid @RequestBody contact: Contact) = contactService.createContact(contact)

  @PutMapping("/{id}")
  @Operation(
    summary = "Update an contact record",
    description = "Updates an existing contact record",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "Contact record updated"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
      ApiResponse(responseCode = "404", description = "Contact record not found"),
    ],
  )
  fun updateContact(@PathVariable id: UUID, @RequestBody contact: Contact) = contactService.updateContact(id, contact)

  @DeleteMapping("/{id}")
  @Operation(
    summary = "Delete an contact record",
    description = "Deletes an contact record from a WRA record",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "Contact record deleted"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
      ApiResponse(responseCode = "404", description = "Contact record not found"),
    ],
  )
  fun deleteContact(@PathVariable id: UUID) {
    contactService.deleteContact(id)
  }

  @GetMapping("/byWRAId/{uuid}")
  @Operation(
    summary = "Fetch contacts by WRA ID",
    description = "Calls through the warrant risk assessment service to retrieve a set of contacts using WRA id",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "contacts returned"),
      ApiResponse(responseCode = "401", description = "Unauthorized"),
      ApiResponse(responseCode = "403", description = "Forbidden"),
      ApiResponse(responseCode = "404", description = "WRA ID not found"),
    ],
  )
  fun getContacts(@PathVariable uuid: UUID): List<Contact> = contactService.findContactsByWraId(uuid)
}
