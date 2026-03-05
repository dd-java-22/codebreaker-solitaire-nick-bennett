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
    private val keyList: List<Int>

    init {
        val resources = context.resources
        val names = resources.getStringArray(R.array.color_names)
        val keys = resources.getStringArray(R.array.color_keys)
        val values = getColorValues(resources)
        val drawables = getDrawables(resources)

        keyList = keys.map { it.codePointAt(0) }
        symbols = keyList.indices.associate { i ->
            keyList[i] to SymbolAttributes(values[i], names[i], drawables[i])
        }
    }

    /**
     * Returns an unmodifiable list of symbol key codepoints in resource order.
     */
    fun getKeys(): List<Int> = keyList

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
        val typedArray = res.obtainTypedArray(R.array.color_drawables)
        return try {
            List(typedArray.length()) { i ->
                ContextCompat.getDrawable(context, typedArray.getResourceId(i, 0)) as Drawable
            }
        } finally {
            typedArray.recycle()
        }
    }

    private data class SymbolAttributes(
        val value: Int,
        val name: String,
        val drawable: Drawable
    )

}
