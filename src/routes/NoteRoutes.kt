package com.androiddevs.routes

import com.androiddevs.data.getNotesForUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes(){
    //  All routes that have to do with notes will go into this class

    route("/getNotes"){
        //  User must be authenticated to be able to access this route
        authenticate {
            //  GET request to make sure that we can get all our notes
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                //  Get notes for the email address
                val notes = getNotesForUser(email)
                //  Return the notes to the requesting user or app
                call.respond(OK, notes)
            }
        }
    }
}