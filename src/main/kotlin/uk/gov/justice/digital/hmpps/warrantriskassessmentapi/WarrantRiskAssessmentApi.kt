package uk.gov.justice.digital.hmpps.warrantriskassessmentapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WarrantRiskAssessmentApi

fun main(args: Array<String>) {
  runApplication<WarrantRiskAssessmentApi>(*args)
}
