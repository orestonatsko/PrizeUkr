package ua.com.info.prize

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import ua.com.info.data.Result

interface OnLoadErrorListener {
    fun error(result: Result)
}

interface OnLoadedListener {
    fun onLoaded()
}

class HomeFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        val a = activity as AppCompatActivity
        a.setSupportActionBar(view.toolbar)
        view.toolbar.title = getString(R.string.app_name)
        view.prize_list.loadPrizes(object : OnLoadErrorListener {
            override fun error(result: Result) {
                view.error.visibility = View.VISIBLE
                view.error.text = result.error()
            }
        })
        view.btn_register.setOnClickListener(this)
        if (loginMode != LoginMode.NONE)
            UserInfo.get(context!!, object : OnLoadedListener {
                override fun onLoaded() {
                    viewUserInfo()
                }

            })
        return view
    }

    private fun viewUserInfo() {
        view?.tv_stars?.visibility = View.VISIBLE
        if (UserInfo.stars != null)
            view?.tv_stars?.text = UserInfo.stars.toString()
        view?.img_star?.visibility = View.VISIBLE
        view?.messages?.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.login -> {
                startMenuItem(LoginActivity::class.java)
                return true
            }
            R.id.my_office -> {
                startMenuItem(MyOfficeActivity::class.java)
                return true
            }
            R.id.exit -> {
                exit(context!!)
                hideUserInfo()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideUserInfo() {
        view?.tv_stars?.visibility = View.INVISIBLE
        view?.img_star?.visibility = View.INVISIBLE
        view?.messages?.visibility = View.INVISIBLE
    }


    private fun startMenuItem(cls: Class<out Activity>) {
        val intent = Intent(context, cls)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            view?.btn_register -> {
                val intent = Intent(context, CardRegistrationActivity::class.java)
                startActivity(intent)
            }
        }
    }
}