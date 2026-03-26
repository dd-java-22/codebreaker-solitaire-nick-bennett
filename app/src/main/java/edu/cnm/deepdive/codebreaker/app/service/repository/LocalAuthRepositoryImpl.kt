package edu.cnm.deepdive.codebreaker.app.service.repository

import android.app.Activity
import android.content.Context
import android.util.Base64
import androidx.credentials.*
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.CompletableFuture

@Singleton
class LocalAuthRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : LocalAuthRepository {

private val credentialManager = CredentialManager.create(context)
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

// In a local-only passkey flow, "Sign Up" and "Sign In" are distinct UI actions.

/**
 * Creates a new Passkey. In a serverless environment, you generate a
 * dummy or locally-consistent 'requestJson' following WebAuthn specs.
 */
override fun registerPasskey(
    activity: Activity,
    username: String,
    uuid: UUID
): CompletableFuture<PublicKeyCredential> =
    scope.future {
        // Note: In a real WebAuthn flow, this JSON comes from a server.
        // For local-only, you must provide a valid WebAuthn creation JSON string.
        val requestJson = generateRegistrationJson(username, uuid)
        val request = CreatePublicKeyCredentialRequest(requestJson)

        val result = credentialManager.createCredential(activity, request)
        result.data as PublicKeyCredential
    }

override fun signIn(activity: Activity): CompletableFuture<PublicKeyCredential> =
    scope.future {
        val getOption = GetPublicKeyCredentialOption(generateAuthenticationJson())
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getOption)
            .build()

        val result = credentialManager.getCredential(activity, request)
        result.credential as PublicKeyCredential
    }

// Passkeys don't "expire" like JWTs, but you can clear the local session state.
override fun signOut(): CompletableFuture<Void?> =
    scope.future {
        credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
        null
    }

private fun generateRegistrationJson(username: String, uuid: UUID): String {
    // The user.id must be Base64url encoded for the WebAuthn spec.
    val encodedId = Base64.encodeToString(
        ByteBuffer.allocate(16)
            .run {
                putLong(uuid.mostSignificantBits)
                putLong(uuid.leastSignificantBits)
                array()
            },
        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )

    return """
        {
            "challenge": "bm9uY2U=", 
            "rp": { 
                "name": "Speedometer Local", 
                "id": "nick-bennett.github.io" 
            },
            "user": { 
                "id": "$encodedId", 
                "name": "$username", 
                "displayName": "$username" 
            },
            "pubKeyCredParams": [{ "type": "public-key", "alg": -7 }],
            "timeout": 60000,
            "attestation": "none",
            "authenticatorSelection": {
                "authenticatorAttachment": "platform",
                "residentKey": "required",
                "userVerification": "required"
            }
        }
    """.trimIndent()
}

private fun generateAuthenticationJson(): String {
    return """
            {
                "challenge": "bm9uY2U=",
                "timeout": 60000,
                "rpId": "nick-bennett.github.io",
                "userVerification": "required"
            }
        """.trimIndent()
}
}