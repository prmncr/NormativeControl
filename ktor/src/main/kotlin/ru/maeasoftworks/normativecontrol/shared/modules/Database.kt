package ru.maeasoftworks.normativecontrol.shared.modules

import io.ktor.server.application.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.FlowQuery
import org.komapper.core.dsl.query.Query
import org.komapper.core.dsl.query.QueryScope
import org.komapper.r2dbc.R2dbcDatabase

class Database(application: Application) {
    private val database: R2dbcDatabase

    init {
        val url = application.environment.config.property("r2dbc.url").getString()
        database = R2dbcDatabase(url)
        runBlocking {
            database.withTransaction {
                database.runQuery {
                    QueryDsl.create(Meta.all())
                }
            }
        }
    }

    suspend fun <T> runQuery(query: QueryScope.() -> Query<T>): T = database.runQuery(query)

    fun <T> flowQuery(query: QueryScope.() -> FlowQuery<T>): Flow<T> = database.flowQuery(query)
}