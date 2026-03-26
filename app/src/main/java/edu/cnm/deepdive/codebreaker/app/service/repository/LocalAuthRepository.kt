package edu.cnm.deepdive.codebreaker.app.service.repository

import android.app.Activity
import androidx.credentials.PublicKeyCredential
import java.util.*
import java.util.concurrent.CompletableFuture

interface LocalAuthRepository {

    fun registerPasskey(
        activity: Activity,
        username: String,
        uuid: UUID
    ): CompletableFuture<PublicKeyCredential>

    fun signIn(activity: Activity): CompletableFuture<PublicKeyCredential>

    fun signOut(): CompletableFuture<Void?>

}
