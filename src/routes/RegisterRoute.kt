package com.androiddevs.routes

import com.androiddevs.data.checkIfUserExists
import com.androiddevs.data.collections.User
import com.androiddevs.data.registerUser
import com.androiddevs.data.requests.AccountRequest
import com.androiddevs.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

//  Registering the user
fun Route.registerRoute(){
    //  Defining a route for registering new users
    route("/register"){
        //  Specify the request type - listening to POST requests from our APP
        post {
            val request = try {
                //  If all goes alright
                call.receive<AccountRequest>()
            }catch (e:ContentTransformationException){
                //  Invalid request
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            //  When the request has been successful
            val userExists = checkIfUserExists(request.email)
            //  Check condition
            if (!userExists){
                //  If the user does not exist
                if (registerUser(User(request.email, request.password))){
                    //  If the creating of the user was successful
                    call.respond(HttpStatusCode.OK,SimpleResponse(true, "Successfully created account"))
                }else{
                    call.respond(HttpStatusCode.OK,SimpleResponse(false, "An unknown error occurred"))
                }
            }else{
                //  If the user already exist in the database
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "A user with that email already exists"))
            }
        }
    }
}