package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import dagger.hilt.android.qualifiers.ActivityContext
import edu.cnm.deepdive.codebreaker.app.R
import jakarta.inject.Inject

class SymbolMap @Inject constructor(
    @param:ActivityContext private val context: Context
) {

    private val symbolsToAttributes: Map<Int, SymbolAttributes>
    private val symbolList: List<Int>

    init {
        val resources = context.resources
        val names = resources.getStringArray(R.array.symbol_names)
        val symbols = resources.getStringArray(R.array.symbols)
        val values = getColors(resources)
        val drawableResIds = getDrawableIds(resources)

        symbolList = symbols.map { it.codePointAt(0) }
        symbolsToAttributes = symbolList
            .indices
            .associate { i -> symbolList[i] to
                    SymbolAttributes(symbolList[i], values[i], names[i], drawableResIds[i])
            }
    }

    /**
     * Returns an unmodifiable list of symbol codepoints in resource order.
     */
    fun getSymbols(): List<Int> = symbolList

    /**
     * Returns true if the map contains the given symbol codepoint, false otherwise.
     */
    fun hasSymbol(symbol: Int): Boolean = symbolsToAttributes.containsKey(symbol)

    /**
     * Returns the [SymbolAttributes] associated with the given symbol codepoint.
     * Throws an exception if the key is not found.
     */
    @Throws(NoSuchElementException::class)
    fun getAttributes(symbol: Int): SymbolAttributes = symbolsToAttributes.getValue(symbol)

    /**
     * Returns the Int color value associated with the given symbol codepoint.
     * Throws an exception if the key is not found.
     */
    @Throws(NoSuchElementException::class)
    fun getColor(symbol: Int): Int = symbolsToAttributes.getValue(symbol).color

    /**
     * Returns the String name associated with the given symbol codepoint.
     * Throws an exception if the key is not found.
     */
    @Throws(NoSuchElementException::class)
    fun getName(symbol: Int): String = symbolsToAttributes.getValue(symbol).name

    /**
     * Returns the Drawable resource ID associated with the given symbol codepoint.
     * Throws an exception if the key is not found.
     */
    @Throws(NoSuchElementException::class)
    fun getDrawableId(symbol: Int): Int = symbolsToAttributes.getValue(symbol).drawableId

    private fun getColors(res: Resources): List<Int> {
        val typedArray = res.obtainTypedArray(R.array.symbol_colors)
        return try {
            List(typedArray.length()) { i -> typedArray.getColor(i, Color.TRANSPARENT) }
        } finally {
            typedArray.recycle()
        }
    }

    private fun getDrawableIds(res: Resources): List<Int> {
        val typedArray = res.obtainTypedArray(R.array.symbol_drawables)
        return try {
            List(typedArray.length()) { i -> typedArray.getResourceId(i, 0) }
        } finally {
            typedArray.recycle()
        }
    }

    data class SymbolAttributes(
        val codePoint: Int,
        val color: Int,
        val name: String,
        val drawableId: Int
    )

}