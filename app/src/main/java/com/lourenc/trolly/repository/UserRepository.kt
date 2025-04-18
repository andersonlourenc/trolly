package com.lourenc.trolly.repository

import com.lourenc.trolly.model.User


object UserRepository {
    private val users = mutableListOf<User>()

    fun registerUser(firstName: String, lastName: String, email: String, password: String): Boolean {

        val userExists = users.any { it.email == email }
        if (userExists) return false

        users.add(User(firstName, lastName, email, password))
        return true
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun getAllUsers(): List<User> = users
}