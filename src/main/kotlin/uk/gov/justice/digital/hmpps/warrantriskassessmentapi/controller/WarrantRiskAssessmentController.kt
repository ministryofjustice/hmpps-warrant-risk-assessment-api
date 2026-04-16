package uk.gov.justice.digital.hmpps.warrantriskassessmentapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.InitialiseWarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.model.WarrantRiskAssessment
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service.SnsService
import uk.gov.justice.digital.hmpps.warrantriskassessmentapi.service.WarrantRiskAssessmentService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.util.UUID

@Validated
@RestController
// @PreAuthorize("hasRole('ROLE_WARRANT_RISK_ASSESSMENT')")
@RequestMapping(value = ["/warrant-risk-assessment"], produces = ["application/json"])
class WarrantRiskAssessmentController(
  private val warrantRiskAssessmentService: WarrantRiskAssessmentService,
  private val sqsService: SnsService,
) {
  @GetMapping("/{uuid}")
  @Operation(
    summary = "Retrieve a draft warrant risk assessment by uuid",
    description = "Calls through the warrant risk assessment service to retrieve warrant risk assessment",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "warrant risk assessment returned"),
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
  fun getWarrantRiskAssessmentById(@PathVariable uuid: UUID): WarrantRiskAssessment? = warrantRiskAssessmentService.findWarrantRiskAssessmentById(uuid)

  @PostMapping
  @Operation(
    summary = "Initialises a Warrant Risk Assessment",
    description = "Calls the API to initialise a warrant risk assessment",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "warrant risk assessment created"),
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
  @ResponseStatus(HttpStatus.CREATED)
  fun initialiseWarrantRiskAssessment(@Valid @RequestBody initialiseWarrantRiskAssessment: InitialiseWarrantRiskAssessment) = warrantRiskAssessmentService.initialiseWarrantRiskAssessment(initialiseWarrantRiskAssessment)

  @PutMapping("/{id}")
  @Operation(
    summary = "Update a Warrant Risk Assessment",
    description = "Calls through the warrant risk assessment service to update a warrant risk assessment",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "Warrant Risk Assessment updated"),
      ApiResponse(
        responseCode = "400",
        description = "cant change the CRN on an update",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
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
      ApiResponse(
        responseCode = "404",
        description = "The Warrant Risk Assessment id was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun updateWarrantRiskAssessment(@PathVariable id: UUID, @RequestBody warrantRiskAssessment: WarrantRiskAssessment) {
    val original = warrantRiskAssessmentService.findWarrantRiskAssessmentById(id)
    warrantRiskAssessmentService.updateWarrantRiskAssessment(id, warrantRiskAssessment)

    if (original != null && original.completedDate == null && warrantRiskAssessment.completedDate != null) {
      sqsService.sendPublishDomainEvent(warrantRiskAssessment, id)
    }
  }

  @DeleteMapping("/{id}")
  @Operation(
    summary = "Delete a Warrant Risk Assessment",
    description = "Calls through the warrant risk assessment service to delete a warrant risk assessment",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "Warrant Risk Assessment deleted"),
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
      ApiResponse(
        responseCode = "404",
        description = "The Warrant Risk Assessment id was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun deleteWarrantRiskAssessment(@PathVariable id: UUID) {
    val crn = warrantRiskAssessmentService.deleteWarrantRiskAssessment(id)
    sqsService.sendDeleteDomainEvent(crn, id)
  }

  @GetMapping("/{uuid}/pdf")
  @Operation(
    summary = "Retrieve a warrant risk assessment pdf by uuid - warrant risk assessment id",
    description = "Calls through the warrant risk assessment service to generate a pdf",
    security = [SecurityRequirement(name = "warrant-risk-assessment-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "warrant risk assessment pdf returned"),
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
  fun getWarrantRiskAssessmentAsPdf(@PathVariable uuid: UUID): ResponseEntity<ByteArray> {
    var warrantRiskAssessment = warrantRiskAssessmentService.findWarrantRiskAssessmentById(uuid)
    var pdfBytes = warrantRiskAssessmentService.getWarrantRiskAssessmentAsPdf(uuid, warrantRiskAssessment, warrantRiskAssessment.completedDate == null)
    var headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_PDF
    headers.contentDisposition = ContentDisposition.attachment().filename("Warrant_Risk_Assessment_" + warrantRiskAssessment?.crn + ".pdf").build()
    return ResponseEntity.ok().headers(headers).body(pdfBytes)
  }
}
