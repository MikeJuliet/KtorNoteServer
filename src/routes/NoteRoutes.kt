package com.androiddevs.routes

import com.androiddevs.data.collections.Note
import com.androiddevs.data.getNotesForUser
import com.androiddevs.data.saveNote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
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

    route("/addNote"){
        //  User must be authenticated to be able to access this route
        authenticate {
            //  Post request to be able to add the note or update the note in the database
            post {
                    val note = try {
                        //  If the data can be transformed to a note
                        call.receive<Note>()
                    }catch (e:ContentTransformationException){
                        //  If the data could not be transformed to the Note.kt class
                        call.respond(BadRequest)
                        return@post
                    }
                //  Check condition
                if (saveNote(note)){
                    // If the note was saved or updated successfully
                    call.respond(OK)
                }else{
                    //  If the note was not saved or updated
                    call.respond(Conflict)
                }
            }
        }
    }
}