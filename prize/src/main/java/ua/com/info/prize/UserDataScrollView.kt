package ua.com.info.prize

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ScrollView

/**
 *  Створено oor 24.09.2018.
 */
class UserDataScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return true
    }
}