package com.example.weatherapp.data.firebase

import com.example.weatherapp.data.model.FavoriteCity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoritesRepository {
    private val auth = FirebaseAuth.getInstance()
    private val root = FirebaseDatabase.getInstance().reference

    private fun uid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")

    private fun favRef(): DatabaseReference =
        root.child("favorites").child(uid())

    fun listen(
        onUpdate: (List<FavoriteCity>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(FavoriteCity::class.java) }
                    .sortedByDescending { it.createdAt }
                onUpdate(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }
        favRef().addValueEventListener(listener)
        return listener
    }

    fun stop(listener: ValueEventListener) {
        favRef().removeEventListener(listener)
    }

    fun add(cityName: String, note: String = "", onError: (String) -> Unit) {
        val ref = favRef().push()
        val id = ref.key ?: return
        val u = uid()

        val item = FavoriteCity(
            id = id,
            cityName = cityName.trim(),
            note = note.trim(),
            createdAt = System.currentTimeMillis(),
            createdBy = u
        )

        ref.setValue(item).addOnFailureListener { onError(it.message ?: "error") }
    }

    fun updateNote(id: String, note: String, onError: (String) -> Unit) {
        favRef().child(id).child("note")
            .setValue(note.trim())
            .addOnFailureListener { onError(it.message ?: "error") }
    }

    fun delete(id: String, onError: (String) -> Unit) {
        favRef().child(id)
            .removeValue()
            .addOnFailureListener { onError(it.message ?: "error") }
    }
}
