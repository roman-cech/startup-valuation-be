package cz.utb.fai.controller

import cz.utb.fai.model.StartupValuationRequest
import cz.utb.fai.model.StartupValuationResponse
import cz.utb.fai.service.StartupValuationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/rest"])
class StartupValuationController(
    private val startupValuationService: StartupValuationService
) {
    @PostMapping(path = ["/v1/startup/evaluate"], consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun evaluateStartup(@RequestBody request: StartupValuationRequest):
            StartupValuationResponse = startupValuationService.evaluateStartup(request)
}