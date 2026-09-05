package com.sl.biorhythms

import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest

@RunWith(AndroidJUnit4::class)
class LauncherIconInstrumentedTest {
    @Test
    fun launcherIconUsesExactApprovedPngAsForeground() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val icon = context.packageManager.getApplicationIcon(context.packageName)
        assertTrue("Launcher icon must remain adaptive", icon is AdaptiveIconDrawable)

        val adaptiveIcon = icon as AdaptiveIconDrawable
        val background = adaptiveIcon.background
        assertTrue("Adaptive icon background must be opaque", background is ColorDrawable)
        assertEquals(
            "Adaptive icon background color changed",
            Color.rgb(8, 3, 71),
            (background as ColorDrawable).color,
        )

        val foreground = adaptiveIcon.foreground
        assertTrue("Adaptive icon foreground must be the approved PNG", foreground is BitmapDrawable)

        val bitmap = (foreground as BitmapDrawable).bitmap
        assertEquals(512, bitmap.width)
        assertEquals(512, bitmap.height)

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val digest = MessageDigest.getInstance("SHA-256")
        for (pixel in pixels) {
            digest.update((pixel ushr 24).toByte())
            digest.update((pixel ushr 16).toByte())
            digest.update((pixel ushr 8).toByte())
            digest.update(pixel.toByte())
        }

        val pixelHash = digest.digest().joinToString("") { "%02x".format(it) }
        assertEquals(
            "Packaged launcher artwork pixels differ from the approved original PNG",
            "acb6036810d2b4c66d8f19bdb07413dccd378f16fe04ff590da5fa279376cf38",
            pixelHash,
        )
    }
}
