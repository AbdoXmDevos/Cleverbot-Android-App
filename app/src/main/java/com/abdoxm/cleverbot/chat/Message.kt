package com.abdoxm.cleverbot.chat


data class Message(val message: String, val id: String, val time: String)

data class ReceivedMessage(val role: String, val content: String)