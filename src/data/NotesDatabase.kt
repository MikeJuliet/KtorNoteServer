package com.androiddevs.data

import com.androiddevs.data.collections.Note
import com.androiddevs.data.collections.User
import org.litote.kmongo.coroutine.coroutine
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