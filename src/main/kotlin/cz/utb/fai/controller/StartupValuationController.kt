package cz.utb.fai.controller

import cz.utb.fai.model.StartupValuationRequest
import cz.utb.fai.model.StartupValuationResponse
import cz.utb.fai.redis.JobStatus
import cz.utb.fai.redis.PublishRedisQueueService
import cz.utb.fai.redis.ConsumeRedisQueueService
import cz.utb.fai.redis.Pair
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping(path = ["/rest"])
@CrossOrigin(origins = ["\${frontend.scheme}"])
class StartupValuationController(
    private val publishRedisQueueService: PublishRedisQueueService,
    private val consumeRedisQueueService: ConsumeRedisQueueService
) {

    @PostMapping(path = ["/v1/startups/evaluate"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun evaluateStartup(@RequestBody request: StartupValuationRequest): ResponseEntity<Mono<UUID>> = ResponseEntity.status(HttpStatus.CREATED).body(publishRedisQueueService.publish(request))


    @GetMapping(path = ["/v1/startups/evaluate/{jobId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getStatusAndResult(@PathVariable jobId: UUID): Mono<Pair<JobStatus, StartupValuationResponse?>> {
        return consumeRedisQueueService.getStatusAndResult(jobId.toString())
    }
}