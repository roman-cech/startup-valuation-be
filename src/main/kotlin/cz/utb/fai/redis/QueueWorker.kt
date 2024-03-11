package cz.utb.fai.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.utb.fai.model.StartupValuationRequest
import cz.utb.fai.service.StartupValuationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.core.publisher.Mono

@Component
class QueueWorker(
    @Value("\${redis.topic}") private val topic: String,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val startupValuationService: StartupValuationService
) {

    @Scheduled(fixedRateString = "\${redis.interval}")
    fun proceedQueue(): Disposable =
        redisTemplate.opsForList().leftPop(topic) // Atomically get and remove the first element
            .flatMap { message ->
                if (message != null) {
                    processMessage(message)
                } else {
                    Mono.empty()
                }
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