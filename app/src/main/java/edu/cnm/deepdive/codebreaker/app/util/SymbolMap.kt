package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import edu.cnm.deepdive.codebreaker.app.R
import jakarta.inject.Inject

class SymbolMap @Inject constructor(
    @param:ActivityContext private val context: Context
) {

    private val symbols: Map<Int, SymbolAttributes>

    init {
        val names = context.resources.getStringArray(R.array.color_names)
        val valuesTyped = context.resources.obtainTypedArray(R.array.color_values)
        val values = mutableListOf<Int>()
        for (i in 0 until valuesTyped.length()) {
            val color = valuesTyped.getColor(i, Color.TRANSPARENT)
            values.add(color)
        }
        valuesTyped.recycle()
        val keys = context.resources.getStringArray(R.array.color_keys)
        val drawableIds = context.resources.getIntArray(R.array.color_drawables)
        val drawables = mutableListOf<Drawable>()
        for (i in 0 until drawableIds.size) {
            val drawable = ContextCompat.getDrawable(context, drawableIds[i]) as Drawable
            drawables.add(drawable)
        }
        symbols = keys
            .indices
            .associate { i ->
                val key = keys[i]
                val name = names[i]
                val value = values[i]
                val drawable = drawables[i]
                key.codePointAt(0) to SymbolAttributes(value, name, drawable)
            }
    }

    private data class SymbolAttributes(
        val value: Int,
        val name: String,
        val drawable: Drawable
    )

}
