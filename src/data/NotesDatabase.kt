package com.androiddevs.data

import com.androiddevs.data.collections.Note
import com.androiddevs.data.collections.User
import io.ktor.html.*
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

//  .coroutines makes sure that all requests in the database happens in coroutines
private val client = KMongo.createClient().coroutine

//  Name is what we will see in the database when we create the first user
private val database = client.getDatabase("NotesDatabase")

//  Making the code cleaner
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    //  On first request of this function the database will create the collections of users
    return users.insertOne(user).wasAcknowledged()
}

//  Checking to see if the user already exist
suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

//  Validate that the user does exist and that the password match
suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    //  Get the password from the database that the user entered and is making a request with and if the user does not exist return false
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == passwordToCheck
}

//  Retrieving all notes for a specific user
suspend fun getNotesForUser(email: String): List<Note> {
    return notes.find(Note::owners contains email).toList()
}

//  Saving or updating database
suspend fun saveNote(note: Note): Boolean {
    //  Check to see if the note already exist in the database, to determine if the note should be updated or created
    val noteExist = notes.findOneById(note.id) != null
    //  Check condition
    return if (noteExist) {
        //  If the note exist, update the note
        notes.updateOneById(note.id, note).wasAcknowledged()
    } else {
        //  If the note does not exist, create it in the database
        notes.insertOne(note).wasAcknowledged()
    }
}