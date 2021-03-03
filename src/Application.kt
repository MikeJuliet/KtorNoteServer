package com.androiddevs

import com.androiddevs.data.collections.User
import com.androiddevs.data.registerUser
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    install(Routing)
    //  Make sure what content the server responds with
    install(ContentNegotiation) {
        //  Configuring content negotiation - answer and expect JSON
        gson {
            setPrettyPrinting()
        }
    }
    CoroutineScope(Dispatchers.IO).launch {
        registerUser(User("test@test.com", "123456"))
    }
}

