package com.example.newview

data class UserData(
    var isblind: Boolean? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var since: Long? = null,
    var status: Boolean? = null
)

/*
data class UserData(val isblind: Boolean? = null, val firstname: String? = null, val lastname: String? = null) {
    private val database: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth

    fun changeUserType(isBlind: String) {
        auth.uid?.let { database.child("userdata").child(it).child("isblind").setValue(isBlind) }
    }
    fun changeFirstName(newFirstName: String) {
        auth.uid?.let { database.child("userdata").child(it).child("firstname").setValue(newFirstName) }
    }
    fun changeLastName(newLastName: String) {
        auth.uid?.let { database.child("userdata").child(it).child("lastname").setValue(newLastName) }
    }

}
*/