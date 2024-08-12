package com.paradoxo.avva.model

data class Message(
    val text: String = "",
    val author: Author = Author.AI,
    val image: String = ""
)

enum class Author { USER, AI }

const val markdownContent =
    " \n# Sample \n* Markdown\n[Link de test](https://example.com)\n<a href=\"https://www.google.com/\">Google</a>"

val sampleMessageList = listOf(
    Message("Hello", Author.AI),
    Message("Hi", Author.USER),
    Message("How are you?", Author.AI),
    Message("I'm fine, thank you", Author.USER),
    Message("What's your name?", Author.AI),
    Message("My name is Lorem", Author.USER),
    Message("What can you do?", Author.AI),
    Message("I can help you with your daily tasks", Author.USER),
    Message("Sure, why did the tomato turn red? Because it saw the salad dressing!", Author.USER),
    Message(author = Author.AI, text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."),
    Message(markdownContent, Author.AI),
)