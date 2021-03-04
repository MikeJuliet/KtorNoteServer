package com.androiddevs

import com.androiddevs.routes.loginRoute
import com.androiddevs.routes.registerRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    //  All server configurations (features) goes into this file

    //  Intercept responses with more information
    install(DefaultHeaders)
    //  Will log all requests to the server and the responses
    install(CallLogging)
    //  Essential feature for the server - to make it rest API
    install(Routing) {
        //  All routes to be specified in the routing block
        registerRoute()
        loginRoute()
    }
    //  Make sure what content the server responds with
    install(ContentNegotiation) {
        //  Configuring content negotiation - answer and expect JSON
        gson {
            setPrettyPrinting()
        }
    }
}

