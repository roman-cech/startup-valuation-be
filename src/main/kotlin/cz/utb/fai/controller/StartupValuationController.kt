package cz.utb.fai.controller

import cz.utb.fai.dto.controller.StartupValuationRequest
import cz.utb.fai.dto.controller.StartupValuationResponse
import cz.utb.fai.messaging.type.JobStatus
import cz.utb.fai.messaging.PublishRedisQueueService
import cz.utb.fai.messaging.ConsumeRedisQueueService
import cz.utb.fai.messaging.type.CustomPair
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping(path = ["/rest"])
class StartupValuationController(
    private val publishRedisQueueService: PublishRedisQueueService,
    private val consumeRedisQueueService: ConsumeRedisQueueService
) {

    @PostMapping(path = ["/v1/startups/evaluate"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun evaluateStartup(@RequestBody request: StartupValuationRequest): ResponseEntity<Mono<UUID>> = ResponseEntity.status(HttpStatus.CREATED).body(publishRedisQueueService.publish(request))

    @GetMapping(path = ["/v1/startups/evaluate/{jobId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getStatusAndEvaluation(@PathVariable jobId: UUID): ResponseEntity<Mono<CustomPair<JobStatus, StartupValuationResponse?>>> {
        val responseMono = consumeRedisQueueService.getStatusAndEvaluation(jobId.toString())

        val statusCode =  responseMono.map { res ->
            when (res.jobStatus) {
                JobStatus.DONE -> HttpStatus.OK
                JobStatus.IN_PROGRESS -> HttpStatus.ACCEPTED
                JobStatus.INVALID_JOB_ID -> HttpStatus.BAD_REQUEST
            }
        }.block() ?: HttpStatus.INTERNAL_SERVER_ERROR

        return ResponseEntity.status(statusCode).body(responseMono)
    }
}