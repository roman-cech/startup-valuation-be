package cz.utb.fai.controller

import cz.utb.fai.connector.GetExplanationConnector
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

//TODO - Should be used only for test purposes
@RestController
class GetExplanationController(
    private val getExplanationConnector: GetExplanationConnector
) {
    @GetMapping("/explanation")
    @ResponseBody
    fun chat(@RequestParam("prompt") prompt: String): String =
        getExplanationConnector.getExplanation(prompt).choices.first().message.content
}