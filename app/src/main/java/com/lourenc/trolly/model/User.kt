package com.lourenc.trolly.model

data class User(
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val password: String
)

object FakeUserDatabase {
    val users = mutableListOf<User>()

    fun emailExists(email: String): Boolean {
        return users.any { it.email == email }
    }

    fun addUser(user: User) {
        users.add(user)
    }
}