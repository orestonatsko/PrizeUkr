package ua.com.info.prize

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main_prize.*


class PrizeMainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_prize)

        val menuItemHome = HomeFragment()
        loadMenuItem(menuItemHome)

        if (loginMode == LoginMode.NONE) {
            bottomToolBarVisibility(false)
        }

        bottom_toolbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    if (currentFragment !is HomeFragment) {
                        item.isChecked = true
                        loadMenuItem(HomeFragment())
                    }
                }
                R.id.my_office -> {
                    if (currentFragment !is MyOfficeFragment) {
                        item.isChecked = true
                        loadMenuItem(MyOfficeFragment())
                    }
                }
                R.id.my_actions -> {
                    if (currentFragment !is MyActionsFragment) {
                        item.isChecked = true
                        loadMenuItem(MyActionsFragment())
                    }
                }
            }
            false
        }
    }

    private fun loadMenuItem(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        currentFragment = fragment
    }

    private fun bottomToolBarVisibility(visible: Boolean) {
        if (visible) {
            bottom_toolbar.visibility = View.VISIBLE
            bottom_bar_shadow.visibility = View.VISIBLE
        } else {
            bottom_toolbar.visibility = View.GONE
            bottom_bar_shadow.visibility = View.GONE
        }
    }

    private var currentFragment: Fragment? = null

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        if (currentFragment !is HomeFragment) {
            bottom_toolbar.menu.findItem(R.id.main).isChecked = true
            loadMenuItem(HomeFragment())
        } else
            super.onBackPressed()
    }
}

