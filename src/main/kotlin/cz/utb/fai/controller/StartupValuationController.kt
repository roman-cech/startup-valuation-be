package cz.utb.fai.controller

import cz.utb.fai.model.StartupValuationRequest
import cz.utb.fai.model.StartupValuationResponse
import cz.utb.fai.redis.JobStatus
import cz.utb.fai.redis.PublishRedisQueueService
import cz.utb.fai.redis.ConsumeRedisQueueService
import org.springframework.http.MediaType
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
    fun evaluateStartup(@RequestBody request: StartupValuationRequest): Mono<UUID> = publishRedisQueueService.publish(request)


    @GetMapping(path = ["/v1/startups/result/{jobId}"])
    @ResponseBody
    fun getResult(@PathVariable jobId: UUID): Mono<Pair<JobStatus, StartupValuationResponse?>> = consumeRedisQueueService.getStatusAndResult(jobId)
}