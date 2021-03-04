package com.androiddevs.routes

import com.androiddevs.data.checkPasswordForEmail
import com.androiddevs.data.requests.AccountRequest
import com.androiddevs.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute(){
    //  Setting the route for a login request
    route("/login"){
        post {
            val request = try {
                //  If all goes right
                call.receive<AccountRequest>()
            }catch (e: ContentTransformationException){
                //  Invalid request
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            //  Check password
            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if (isPasswordCorrect){
                //  If the user exist and the password matches
                call.respond(OK, SimpleResponse(true, "You are now logged in!"))
            }else{
                //  If the password does not match or the user does not exist
                call.respond(OK, SimpleResponse(false, "The E-mail or password is incorrect"))
            }
        }
    }
}