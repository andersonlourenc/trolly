package com.lourenc.trolly.data.repository

import com.lourenc.trolly.data.model.User



object UserRepository {
    private val users = mutableListOf<User>()

    fun registerUser(name: String, email: String, password: String): Boolean {

        val userExists = users.any { it.email == email }
        if (userExists) return false

        users.add(User(name, email, password))
        return true
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun getAllUsers(): List<User> = users
}