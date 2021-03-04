package com.androiddevs

import com.androiddevs.data.checkPasswordForEmail
import com.androiddevs.routes.loginRoute
import com.androiddevs.routes.noteRoutes
import com.androiddevs.routes.registerRoute
import io.ktor.application.*
import io.ktor.auth.*
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
    //  Make sure what content the server responds with
    install(ContentNegotiation) {
        //  Configuring content negotiation - answer and expect JSON
        gson {
            setPrettyPrinting()
        }
    }
    //  Authentication
    install(Authentication) {
        //  Configure below
        configureAuth()
    }
    //  Essential feature for the server - to make it rest API
    install(Routing) {
        //  All routes to be specified in the routing block
        registerRoute()
        loginRoute()
        noteRoutes()
    }
}

private fun Authentication.Configuration.configureAuth() {
    //  Different kinds of authorisation goes here
    basic {
        realm = "Note server"       //  What will display when you open in browser
        validate { credentials ->
            //  Check email and password and authenticate user
            val email = credentials.name
            val password = credentials.password
            if (checkPasswordForEmail(email, password)) {
                UserIdPrincipal(email)
            } else {
                null
            }
        }
    }
}

