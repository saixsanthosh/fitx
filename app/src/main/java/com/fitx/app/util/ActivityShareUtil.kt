package com.fitx.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.FileProvider
import com.fitx.app.domain.model.ActivitySession
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

object ActivityShareUtil {
    private val decimal = DecimalFormat("0.00")

    fun shareSessionCard(context: Context, session: ActivitySession) {
        runCatching {
            val imageUri = createCardPng(context, session)
            val caption = buildCaption(session)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, caption)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                Intent.createChooser(shareIntent, "Share Fitx session").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun createCardPng(context: Context, session: ActivitySession): android.net.Uri {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                Color.parseColor("#12171F"),
                Color.parseColor("#1C2430"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#202C3A") }
        canvas.drawRoundRect(RectF(60f, 120f, width - 60f, height - 120f), 36f, 36f, cardPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#9BD7CF")
            textSize = 54f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 88f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#C9D6E4")
            textSize = 40f
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#90A4BA")
            textSize = 32f
        }

        val primaryValue = if (session.activityType.name == "CYCLING") {
            "${decimal.format(session.averageSpeedMps * 3.6)} km/h"
        } else {
            "${decimal.format(session.distanceMeters / 1000.0)} km"
        }
        val primaryLabel = if (session.activityType.name == "CYCLING") "Average Speed" else "Distance"

        canvas.drawText("FITX SESSION", 110f, 220f, titlePaint)
        canvas.drawText(primaryLabel.uppercase(), 110f, 315f, labelPaint)
        canvas.drawText(primaryValue, 110f, 410f, valuePaint)
        canvas.drawText("Calories: ${session.caloriesBurned} kcal", 110f, 520f, bodyPaint)
        canvas.drawText("Steps: ${session.steps}", 110f, 590f, bodyPaint)
        canvas.drawText("Duration: ${DateUtils.formatDuration(session.durationSeconds)}", 110f, 660f, bodyPaint)
        canvas.drawText("Type: ${session.activityType.name.lowercase().replaceFirstChar { it.uppercase() }}", 110f, 730f, bodyPaint)
        canvas.drawText("Powered by Fitx", 110f, height - 180f, labelPaint)

        val dir = File(context.cacheDir, "share_cards").apply { mkdirs() }
        val file = File(dir, "fitx_session_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun buildCaption(session: ActivitySession): String {
        return "Completed a ${session.activityType.name.lowercase()} session in Fitx: " +
            "${decimal.format(session.distanceMeters / 1000.0)} km, ${session.caloriesBurned} kcal."
    }
}
