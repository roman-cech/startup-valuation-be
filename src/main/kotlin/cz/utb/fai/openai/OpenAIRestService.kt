package cz.utb.fai.openai

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

open class OpenAIRestService(
    val template: RestTemplate,
    val properties: OpenAIProperties
) {
    inline fun <REQUEST, reified RESPONSE> callService(
        request: REQUEST?,
        path: String,
        httpMethod: HttpMethod,
        queryVariable: Map<String, *> = emptyMap<String, String>()
    ): RESPONSE = with(prepareURI(request, path, httpMethod, queryVariable)) {
        val (requestEntity, uri) = this
        template.exchange(uri.toString(), httpMethod, requestEntity, RESPONSE::class.java)
    }.run {
        if (RESPONSE::class == Unit::class) {
            Unit as RESPONSE
        } else {
            this.body ?: throw IllegalStateException("Response is null")
        }
    }

    fun <REQUEST> prepareURI(
        request: REQUEST,
        path: String,
        httpMethod: HttpMethod,
        queryVariable: Map<String, *> = emptyMap<String, String>()
    ): Pair<RequestEntity<REQUEST>, URI> {
        val requestEntity= RequestEntity
            .method(httpMethod, "")
            .accept(MediaType.APPLICATION_JSON)
            .headers(HttpHeaders().apply { set(HttpHeaders.AUTHORIZATION, "Bearer ${properties.key}") })
            .body(request)

        val multiQueryVariables = MultiValueMapAdapter(queryVariable.mapValues { listOf(it.value.toString()) })

        val uri = UriComponentsBuilder.newInstance()
            .scheme(properties.scheme)
            .path(path)
            .queryParams(multiQueryVariables)
            .build()
            .toUri()

        return requestEntity to uri
    }
}