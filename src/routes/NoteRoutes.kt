package com.androiddevs.routes

import com.androiddevs.data.*
import com.androiddevs.data.collections.Note
import com.androiddevs.data.requests.AddOwnerRequest
import com.androiddevs.data.requests.DeleteNoteRequest
import com.androiddevs.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    //  All routes that have to do with notes will go into this class

    route("/getNotes") {
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

    route("/addNote") {
        //  User must be authenticated to be able to access this route
        authenticate {
            //  Post request to be able to add the note or update the note in the database
            post {
                val note = try {
                    //  If the data can be transformed to a note
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    //  If the data could not be transformed to the Note.kt class
                    call.respond(BadRequest)
                    return@post
                }
                //  Check condition
                if (saveNote(note)) {
                    // If the note was saved or updated successfully
                    call.respond(OK)
                } else {
                    //  If the note was not saved or updated
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                //  Check condition
                if (deleteNoteForUser(email, request.id)) {
                    //  If the delete request was successful
                    call.respond(OK)
                } else {
                    //  If the delete request was unsuccessful
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                //  Check condition
                if (!checkIfUserExists(request.owner)) {
                    call.respond(OK, SimpleResponse(false, "No user with this email exists"))
                    return@post
                }
                if (isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(OK, SimpleResponse(false, "This user is already an owner of this note"))
                    return@post
                }
                if (addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(OK, SimpleResponse(true, "${request.owner} can now see this note"))
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
}