package com.fitx.app.data.remote

import com.fitx.app.data.local.entity.SyncQueueEntity
import com.fitx.app.service.sync.SyncOperationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class CloudSyncRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth?,
    private val firestore: FirebaseFirestore?,
    private val gson: Gson
) {
    suspend fun push(operation: SyncQueueEntity): Result<Unit> {
        val auth = firebaseAuth
        val store = firestore
        if (auth == null || store == null) {
            return Result.failure(IllegalStateException("No authenticated user"))
        }
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("No authenticated user"))

        return runCatching {
            val docRef = store
                .collection("users")
                .document(uid)
                .collection(operation.entityType)
                .document(operation.entityId)

            if (operation.operationType == SyncOperationType.DELETE) {
                docRef.delete().await()
                return@runCatching
            }

            val payloadJson = operation.payloadJson ?: "{}"
            val mapType = object : TypeToken<Map<String, Any?>>() {}.type
            val payload = gson.fromJson<Map<String, Any?>>(payloadJson, mapType).orEmpty()
                .toMutableMap()
            payload["updatedAtMillis"] = System.currentTimeMillis()

            docRef.set(payload, SetOptions.merge()).await()
        }
    }
}
