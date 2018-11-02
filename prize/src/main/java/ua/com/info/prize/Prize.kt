package ua.com.info.prize

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley

/**
 *  Створено oor 15.08.2018.
 */
class Prize(val name: String, val imageCode: Int, val starCount: Int) {
    var image: Bitmap? = null
}
