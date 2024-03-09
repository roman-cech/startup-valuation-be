package cz.utb.fai.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.utb.fai.model.StartupValuationResponse
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class ConsumeRedisQueueService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    fun getStatusAndResult(jobId: UUID): Mono<Pair<JobStatus, StartupValuationResponse?>> =
        redisTemplate.opsForValue().get(jobId.toString())
            .map { job ->
                if (job != null) {
                    JobStatus.DONE to jacksonObjectMapper().readValue(job, StartupValuationResponse::class.java)
                } else {
                    JobStatus.IN_PROGRESS to null
                }
            }.defaultIfEmpty(JobStatus.IN_PROGRESS to null)

}