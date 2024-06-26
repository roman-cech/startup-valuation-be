package cz.utb.fai.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.utb.fai.dto.controller.StartupValuationRequest
import cz.utb.fai.controller.StartupValuationService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.core.publisher.Mono
import kotlin.Pair

@Component
class QueueWorker(
    private val properties: RedisProperties,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val startupValuationService: StartupValuationService
) {

    @Scheduled(fixedRateString = "\${redis.interval}")
    fun proceedQueue(): Disposable =
        redisTemplate.opsForList().leftPop(properties.topic) // Atomically get and remove the first element
            .flatMap { message ->
                if (message != null) processMessage(message)
                else Mono.empty()
            }.subscribe()

    private fun processMessage(message: String): Mono<Void> {
        val (jobId, request) = parseMessage(message)

        redisTemplate.opsForValue().set(jobId, "")
        val result = jacksonObjectMapper().writeValueAsString(startupValuationService.evaluateStartup(request))

        return redisTemplate.opsForValue().set(jobId, result).then()
    }

    private fun parseMessage(message: String): Pair<String, StartupValuationRequest> {
        val queue = jacksonObjectMapper().readValue(message, Pair::class.java)
        val jsonRequest = jacksonObjectMapper().writeValueAsString(queue.second)
        val request = jacksonObjectMapper().readValue(jsonRequest, StartupValuationRequest::class.java)
        return (queue.first as String) to request
    }
}