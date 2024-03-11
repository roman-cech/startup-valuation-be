package cz.utb.fai.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.utb.fai.model.StartupValuationResponse
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ConsumeRedisQueueService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    fun getStatusAndResult(jobId: String): Mono<Pair<JobStatus, StartupValuationResponse?>> =
        redisTemplate.hasKey(jobId)
            .flatMap { exists ->
                if (exists)
                    redisTemplate.opsForValue().get(jobId)
                        .map { value ->
                            if (value.isNullOrEmpty()) {
                                JobStatus.IN_PROGRESS to null
                            } else {
                                JobStatus.DONE to jacksonObjectMapper().readValue(value, StartupValuationResponse::class.java)
                            }
                        }
                else Mono.just(JobStatus.INVALID_JOB_ID to null)
            }
}