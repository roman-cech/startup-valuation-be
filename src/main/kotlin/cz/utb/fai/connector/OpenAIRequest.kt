package cz.utb.fai.connector;

private const val OPEN_AI_ROLE = "user"

data class OpenAIRequest(
    val model: String,
    val temperature: Double,
    val messages: MutableList<Message> = mutableListOf()
) {
    constructor(model: String, prompt: String, temperature: Double) : this(model, temperature) {
        messages.add(Message(role = OPEN_AI_ROLE, content = prompt))
    }
}