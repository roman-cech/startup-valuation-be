package cz.utb.fai.drools

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.FileCopyUtils
import org.springframework.web.client.ResponseErrorHandler
import java.io.IOException
import javax.validation.Valid


class DroolsErrorHandler(private val mapper: ObjectMapper) : ResponseErrorHandler {

    override fun hasError(response: ClientHttpResponse): Boolean = response.statusCode.series() != HttpStatus.OK.series()

    private fun getResponseBody(response: ClientHttpResponse): ByteArray = try {
        FileCopyUtils.copyToByteArray(response.body)
    } catch (e: IOException) {
        println(e.message)
        ByteArray(0)
    }

    override fun handleError(response: ClientHttpResponse) {
        val statusCode = response.statusCode
        lateinit var errorResponse: Errors

        try {
            val responseJson = String(getResponseBody(response))
            errorResponse = mapper.readValue(responseJson, GeneralFault::class.java).errors
        } catch (e: IOException) {
            throw DroolsException(httpStatus = statusCode, errorResponse = errorResponse)
        }
    }
}

@JvmInline value class GeneralFault(@Valid @JsonProperty("errors") val errors: Errors)

data class Errors(@Valid @JsonProperty("error") val error: List<Error> = emptyList())

data class Error(@JsonProperty("code") val code: String, @JsonProperty("text") val text: String? = null)

class DroolsException(
    val httpStatus: HttpStatus,
    val errorResponse: Errors? = null
) : RuntimeException()