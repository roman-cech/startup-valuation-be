package cz.utb.fai.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.utb.fai.messaging.type.CustomPair
import cz.utb.fai.messaging.type.JobStatus
import cz.utb.fai.dto.controller.StartupValuationResponse
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ConsumeRedisQueueService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    fun getStatusAndEvaluation(jobId: String): Mono<CustomPair<JobStatus, StartupValuationResponse?>> =
        redisTemplate.hasKey(jobId)
            .flatMap { exists ->
                if (exists)
                    redisTemplate.opsForValue().get(jobId)
                        .map { value ->
                            if (value.isNullOrEmpty()) CustomPair(JobStatus.IN_PROGRESS , null)
                            else CustomPair(JobStatus.DONE , jacksonObjectMapper().readValue(value, StartupValuationResponse::class.java))
                        }
                else Mono.just(CustomPair(JobStatus.INVALID_JOB_ID , null))
            }
}