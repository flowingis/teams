package it.flowing

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.Jetty
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import it.flowing.config.Credentials
import it.flowing.repository.Surfers
import it.flowing.repository.Teams
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Info(_uptime: OffsetDateTime) {
    val uptime = _uptime.format(DateTimeFormatter.ISO_DATE_TIME)
}

val INFO_INSTANCE = Info(OffsetDateTime.now())

val credentials = Credentials.load();

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val teams = Teams()
    val surfers = Surfers()

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    install(Authentication) {
        basic("myBasicAuth") {
            realm = "Ktor Server"
            validate { if (it.name == credentials.user && it.password == credentials.password) UserIdPrincipal(it.name) else null }
        }
    }

    install(ContentNegotiation) {
        gson {

        }
    }

    val client = HttpClient(Jetty) {
    }

    routing {
        get("/") {
            call.respond(INFO_INSTANCE)
        }

        get("/people") {
            call.respond(surfers.list())
        }

        authenticate("myBasicAuth") {
            get("/teams") {
                call.respond(teams.list())
            }
        }
    }
}

