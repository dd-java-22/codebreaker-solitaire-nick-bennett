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

        symbols = keys.indices.associate {
            keys[it].codePointAt(0) to SymbolAttributes(values[it], names[it], drawables[it])
        }
    }

    /**
     * Returns an unmodifiable list of symbol key codepoints.
     */
    fun getKeys(): List<Int> = symbols.keys.toList()

    /**
     * Returns the Int color value associated with the given key codepoint.
     * Throws an exception if the key is not found.
     */
    fun getColor(key: Int): Int = symbols.getValue(key).value

    /**
     * Returns the String name associated with the given key codepoint.
     * Throws an exception if the key is not found.
     */
    fun getName(key: Int): String = symbols.getValue(key).name

    /**
     * Returns the Drawable associated with the given key codepoint.
     * Throws an exception if the key is not found.
     */
    fun getDrawable(key: Int): Drawable = symbols.getValue(key).drawable

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
