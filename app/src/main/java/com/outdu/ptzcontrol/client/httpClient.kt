package com.outdu.ptzcontrol.client

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class PTZClient {
    companion object {
        private const val TAG = "PTZClient"
        private const val TIMEOUT_MS = 10_000
    }

    private var httpClient: HttpClient? = null
    private val baseUrl = "http://192.168.1.111:18080"

    @Serializable
    data class Preset(
        val id: Int,
        val name: String
    )

    @Serializable
    data class PresetResponse(
        val presets: List<Preset>
    )

    @Serializable
    data class SetPresetRequest(
        val presetNumber: Int,
        val presetName: String
    )

    @Serializable
    data class SetPresetResponse(
        val message: String? = null
    )

    @Serializable
    data class DeletePresetRequest(
        val presetNumber: Int,
        val presetName: String
    )

    @Serializable
    data class DeletePresetResponse(
        val message: String? = null
    )

    @Serializable
    data class ControlMovementRequest(
        val direction: String,
        val time: Int = 2
    )

    @Serializable
    data class ControlMovementResponse(
        val message: String? = null
    )

    fun init() {
        if (httpClient != null) {
            Log.i(TAG, "HTTP client already initialized")
            return
        }
        
        httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            engine {
                requestTimeout = TIMEOUT_MS.toLong()
                endpoint {
                    connectTimeout = TIMEOUT_MS.toLong()
                    connectAttempts = 1
                }
            }
        }
        Log.i(TAG, "HTTP client initialized")
    }

    fun close() {
        httpClient?.close()
        httpClient = null
        Log.i(TAG, "HTTP client closed")
    }

    suspend fun fetchPresets(): List<Preset> {
        val client = httpClient ?: throw IllegalStateException("Client not initialized")

        val url = "$baseUrl/fetchPresets"

        var result: List<Preset> = emptyList()

        try {
            val response: String = client.get(url).bodyAsText()
            Log.d(TAG, "Raw API response: $response")

            val parsed = Json.decodeFromString<PresetResponse>(response)
            result = parsed.presets
            Log.d(TAG, "Parsed ${result.size} presets: $result")

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching presets", e)
        }

        return result
    }

    suspend fun setPreset(id: Int, name: String): Boolean {
        val client = httpClient ?: throw IllegalStateException("Client not initialized")

        val url = "$baseUrl/setPreset"
        val request = SetPresetRequest(presetNumber = id, presetName = name)

        return try {
            Log.d(TAG, "Setting preset: ID=$id, Name=$name")
            
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val responseText = response.bodyAsText()
            Log.d(TAG, "Set preset response: $responseText")

            val parsed = Json.decodeFromString<SetPresetResponse>(responseText)
            
            if (response.status.value == 200) {
                Log.i(TAG, "Successfully set preset: $name")
            } else {
                Log.w(TAG, "Failed to set preset: ${parsed.message}")
            }

            response.status.value == 200

        } catch (e: Exception) {
            Log.e(TAG, "Error setting preset", e)
            false
        }
    }

    suspend fun deletePreset(presetNumber: Int, presetName: String): Pair<Boolean, String?> {
        val client = httpClient ?: throw IllegalStateException("Client not initialized")

        val url = "$baseUrl/deletePreset"
        val request = DeletePresetRequest(presetNumber = presetNumber, presetName = presetName)

        return try {
            Log.d(TAG, "Deleting preset: Number=$presetNumber, Name=$presetName")
            
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val responseText = response.bodyAsText()
            Log.d(TAG, "Delete preset response: $responseText")

            val parsed = Json.decodeFromString<DeletePresetResponse>(responseText)
            
            if (response.status.value == 200) {
                Log.i(TAG, "Successfully deleted preset: $presetName")
            } else {
                Log.w(TAG, "Failed to delete preset: ${parsed.message}")
            }

            Pair(response.status.value == 200, parsed.message)

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting preset", e)
            Pair(false, "Network error: ${e.message}")
        }
    }

    suspend fun controlMovement(direction: String, time: Int = 2): Boolean {
        val client = httpClient ?: throw IllegalStateException("Client not initialized")

        val url = "$baseUrl/controlMovement"
        val request = ControlMovementRequest(direction = direction, time = time)

        return try {
            Log.d(TAG, "Controlling movement: Direction=$direction, Time=${time}s")
            
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val responseText = response.bodyAsText()
            Log.d(TAG, "Control movement response: $responseText")

            if (response.status.value == 200) {
                Log.i(TAG, "Successfully sent movement command: $direction for ${time}s")
                true
            } else {
                val parsed = Json.decodeFromString<ControlMovementResponse>(responseText)
                Log.w(TAG, "Failed to control movement: ${parsed.message}")
                false
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error controlling movement", e)
            false
        }
    }
}