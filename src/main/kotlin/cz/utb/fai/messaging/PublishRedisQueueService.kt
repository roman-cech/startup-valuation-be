package cz.utb.fai.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.utb.fai.dto.controller.StartupValuationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class PublishRedisQueueService(
    @Value("\${redis.topic}") private val topic: String,
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    fun publish(request: StartupValuationRequest): Mono<UUID> = Mono.defer {
        val jobId = UUID.randomUUID()
        val jsonPair = jacksonObjectMapper().writeValueAsString(jobId to request)
        redisTemplate.opsForList().leftPush(topic, jsonPair).thenReturn(jobId)
    }
}