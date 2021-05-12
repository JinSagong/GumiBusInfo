package com.jin.businfo_gumi.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

@Suppress("UNUSED")
fun Query.readSingleItem(
    onSuccess: ((DataSnapshot) -> Unit)?,
    onFailure: ((DatabaseError) -> Unit)?
) = addListenerForSingleValueEvent(object : ValueEventListener {
    override fun onCancelled(error: DatabaseError) {
        Debug.e("[onFailureReadSingleItem] ${error.message}")
        onFailure?.invoke(error)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        Debug.i("[onSuccessReadSingleItem] $snapshot")
        onSuccess?.invoke(snapshot)
    }
})

@Suppress("UNUSED")
fun Query.readSingleItem(onSuccess: ((DataSnapshot) -> Unit)?) =
    readSingleItem(onSuccess, null)