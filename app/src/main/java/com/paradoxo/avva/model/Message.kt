package com.paradoxo.avva.model

data class Message(
    val text: String = "",
    val status: Status = Status.AI,
    val image: String = ""
)

enum class Status {
    LOAD, USER, AI
}

val markdownContent = " \n# Sample \n* Markdown\n[Link de teste](https://example.com)\n<a href=\"https://www.google.com/\">Google</a>"

val sampleMessageList = listOf(
    Message("Hello", Status.AI),
    Message("Hi", Status.USER),
    Message("How are you?", Status.AI),
    Message("I'm fine, thank you", Status.USER),
    Message("What's your name?", Status.AI),
    Message("My name is Laura", Status.USER),
    Message("What can you do?", Status.AI),
    Message("I can help you with your daily tasks", Status.USER),
    Message("Can you tell me a joke?", Status.AI),
    Message("Sure, why did the tomato turn red? Because it saw the salad dressing!", Status.USER),
    Message(markdownContent, Status.AI),
)