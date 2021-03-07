package com.androiddevs.data

import com.androiddevs.data.collections.Note
import com.androiddevs.data.collections.User
import com.androiddevs.security.checkHashForPassword
import io.ktor.html.*
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

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
    //  Check password
    return checkHashForPassword(passwordToCheck, actualPassword)
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

//  Delete owner from the list of owners
suspend fun deleteNoteForUser(email: String, noteId: String): Boolean {
    //  Finding the corresponding note
    val note = notes.findOne(Note::id eq noteId, Note::owners contains email)
    note?.let { singleNote ->
        //  If the note with the given ID exist and that it belongs to the user with the email that is trying to delete it
        if (singleNote.owners.size > 1) {
            //  Note have multiple owners
            val newOwners = singleNote.owners - email     //  Delete only the user email from the list of owners
            //  Only update the owners list for the Note with the given ID
            val updateResult = notes.updateOne(Note::id eq singleNote.id, setValue(Note::owners, newOwners))
            return updateResult.wasAcknowledged()
        }
        //  If the note owner list only have one email assigned to it
        return notes.deleteOneById(noteId).wasAcknowledged()
    } ?: return false       //  If there is no note with the given ID
}

//  Adding owners to notes
suspend fun addOwnerToNote(noteId: String, owner: String): Boolean {
    val owners = notes.findOneById(noteId)?.owners ?: return false
    return notes.updateOneById(noteId, setValue(Note::owners, owners + owner)).wasAcknowledged()
}

//  Checking if the owner is already in the owners list
suspend fun isOwnerOfNote(noteId: String, owner: String):Boolean{
    val note = notes.findOneById(noteId) ?: return false
    return owner in note.owners
}