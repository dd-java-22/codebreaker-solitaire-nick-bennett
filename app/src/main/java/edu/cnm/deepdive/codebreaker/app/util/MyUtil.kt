package edu.cnm.deepdive.codebreaker.app.util

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class MyUtil @Inject constructor(
    @ActivityContext private val context: Context
) {
    // Utility methods can be added here
}
