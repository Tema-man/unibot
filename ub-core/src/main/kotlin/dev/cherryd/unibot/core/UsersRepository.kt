package dev.cherryd.unibot.core

interface UsersRepository {
    fun saveUser(user: User)
    fun saveUsers(users: List<User>)
    fun findUserByName(name: String): User?
    fun findUserByMention(mention: String): User?
    fun linkUserToChat(user: User, chat: Chat)
    fun getUsersOfChat(chat: Chat, activeOnly: Boolean = false): List<User>
}