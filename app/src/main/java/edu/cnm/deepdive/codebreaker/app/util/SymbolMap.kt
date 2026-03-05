package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import android.content.res.Resources
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
        val resources = context.resources
        val names = resources.getStringArray(R.array.color_names)
        val keys = resources.getStringArray(R.array.color_keys)
        val values = getColorValues(resources)
        val drawables = getDrawables(resources)

        symbols = keys.indices.associate { i ->
            keys[i].codePointAt(0) to SymbolAttributes(values[i], names[i], drawables[i])
        }
    }

    private fun getColorValues(res: Resources): List<Int> {
        val typedArray = res.obtainTypedArray(R.array.color_values)
        return try {
            List(typedArray.length()) { i -> typedArray.getColor(i, Color.TRANSPARENT) }
        } finally {
            typedArray.recycle()
        }
    }

    private fun getDrawables(res: Resources): List<Drawable> {
        return res.getIntArray(R.array.color_drawables).map { id ->
            ContextCompat.getDrawable(context, id)!!
        }
    }

    private data class SymbolAttributes(
        val value: Int,
        val name: String,
        val drawable: Drawable
    )

}
