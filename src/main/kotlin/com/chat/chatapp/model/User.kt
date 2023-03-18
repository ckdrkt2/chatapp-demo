package com.chat.chatapp.model

import java.security.Principal

class User(private val name: String) : Principal {
    override fun getName(): String {
        return name
    }
}