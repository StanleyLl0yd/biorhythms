package com.sl.biorhythms

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherIconInstrumentedTest {
    @Test
    fun launcherIconUsesThreeWaveArtwork() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val icon = context.packageManager.getApplicationIcon(context.packageName)
        assertTrue("Launcher icon must remain adaptive", icon is AdaptiveIconDrawable)

        val size = 432
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        icon.setBounds(0, 0, size, size)
        icon.draw(Canvas(bitmap))

        val center = bitmap.getPixel(size / 2, size / 2)
        assertTrue(
            "The center must be the dark indigo background, not the legacy white nucleus",
            Color.red(center) < 80 && Color.green(center) < 80 && Color.blue(center) < 120,
        )

        var bluePixels = 0
        var orangePixels = 0
        var purplePixels = 0
        var whitePixels = 0

        for (y in 0 until size step 2) {
            for (x in 0 until size step 2) {
                val color = bitmap.getPixel(x, y)
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)

                if (blue > 140 && green > 70 && red < 80) bluePixels++
                if (red > 160 && green > 80 && blue < 90) orangePixels++
                if (red > 70 && blue > 70 && green < 100) purplePixels++
                if (red > 220 && green > 220 && blue > 220) whitePixels++
            }
        }

        assertTrue("Blue biorhythm ribbon is missing", bluePixels > 250)
        assertTrue("Orange biorhythm ribbon is missing", orangePixels > 250)
        assertTrue("Purple biorhythm ribbon is missing", purplePixels > 250)
        assertTrue("Legacy white nucleus must not be present", whitePixels < 30)
    }
}
