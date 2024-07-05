package dev.cherryd.unibot.data

import dev.cherryd.unibot.core.User

class UsersRepository(
    private val database: Database
) {

    fun saveUser(user: User) {
        database.execute(
            """
                INSERT INTO users (id, name, role) VALUES (?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET name = excluded.name, role = excluded.role
            """.trimIndent()
        ) {
            setString(1, user.id)
            setString(2, user.name)
            setString(3, user.role.name.lowercase())
            executeUpdate()
        }
    }
}