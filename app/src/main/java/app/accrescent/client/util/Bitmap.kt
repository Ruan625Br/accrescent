package app.accrescent.client.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette

fun Bitmap.getDominantColor(defaultColor: Color = Color.Transparent): Color {
   val palette = createPaletteSync(this)

    return Color(palette.getDominantColor(defaultColor.toArgb()))
}

fun Bitmap.getVibrantSwatch(): Palette.Swatch? {
    val palette = createPaletteSync(this)

    return palette.vibrantSwatch
}

private fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()