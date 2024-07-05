package dev.cherryd.unibot.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.cherryd.unibot.core.Environment
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException


class Database(
    private val environment: Environment
) {

    private val dataSource: HikariDataSource

    init {
        val dbName = environment.get(DATABASE_NAME)
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${environment.get(DATABASE_HOST)}/$dbName"
            username = environment.get(DATABASE_USER)
            password = environment.get(DATABASE_PASSWORD)
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }
        dataSource = HikariDataSource(config)
    }

    @Throws(SQLException::class)
    fun getConnection(): Connection = dataSource.connection

    fun <T> execute(sql: String, block: PreparedStatement.() -> T): T? {
        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                return block(statement)
            }
        }
    }

    companion object {
        private const val DATABASE_HOST = "DATABASE_HOST"
        private const val DATABASE_NAME = "POSTGRES_DB"
        private const val DATABASE_USER = "POSTGRES_USER"
        private const val DATABASE_PASSWORD = "POSTGRES_PASSWORD"
    }
}