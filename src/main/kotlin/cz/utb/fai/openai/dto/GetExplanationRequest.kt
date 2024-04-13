package cz.utb.fai.openai.dto

import cz.utb.fai.openai.dto.type.Message

private const val OPEN_AI_ROLE = "user"

data class GetExplanationRequest(
    val model: String,
    val temperature: Double,
    val messages: MutableList<Message> = mutableListOf()
) {
    constructor(model: String, prompt: String, temperature: Double) : this(model, temperature) {
        messages.add(Message(role = OPEN_AI_ROLE, content = prompt))
    }
}