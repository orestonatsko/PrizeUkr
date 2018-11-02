package ua.com.info.prize

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main_prize.*
import ua.com.info.prize.R.id.*


class PrizeMainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_prize)

        val menuItemHome = HomeFragment()
        loadMenuItem(menuItemHome)

        if (loginMode == LoginMode.NONE) {
            bottomToolBar(false)
        }

        bottom_toolbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    item.isChecked = true
                    loadMenuItem(HomeFragment())
                }
                R.id.my_office -> {
                    item.isChecked = true
                    loadMenuItem(MyOfficeFragment())
                }
                R.id.my_actions -> {
                    item.isChecked = true
//                    loadMenuItem(MyActionsFragment())
                }
            }
            false
        }
    }

    private fun loadMenuItem(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

    private fun bottomToolBar(visible: Boolean) {
        if (visible) {
            bottom_toolbar.visibility = View.VISIBLE
            bottom_bar_shadow.visibility = View.VISIBLE
        } else {
            bottom_toolbar.visibility = View.GONE
            bottom_bar_shadow.visibility = View.GONE
        }
    }

    private var currentFragment: Fragment? = null

    override fun onBackPressed() {
        if (currentFragment !is HomeFragment) {
           bottom_toolbar.menu.findItem(R.id.main).isChecked = true
            loadMenuItem(HomeFragment())
        } else
            super.onBackPressed()
    }
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
}

