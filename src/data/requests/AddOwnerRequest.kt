package com.androiddevs.data.requests

import com.androiddevs.data.collections.Note

data class AddOwnerRequest(
    val noteId: String,
    val owner: String
)
