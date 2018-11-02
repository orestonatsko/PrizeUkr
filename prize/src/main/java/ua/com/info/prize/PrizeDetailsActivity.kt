package ua.com.info.prize

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_prize_details.*
import kotlinx.android.synthetic.main.item_image.view.*
import ua.com.info.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface OnReadDetailsListener {
    fun onRead(result: Result)
}

interface OnParticipateListener {
    fun onAssume(result: Result)
}

class PrizeDetailsActivity : AppCompatActivity(), View.OnClickListener {

    var name: String? = null
    var imgCount: Int = 1
    var stamp: Int? = null
    var desc: String? = null
    var prizeStars: Int? = null
    var info: Int? = null
    var quizDate: Any? = null
    var hasPlaces: Int? = null
    var allPlaces: Int? = null
    var endDate: String? = null
    private var prizeCode: Int? = null

    val images = ArrayList<String>()
    private lateinit var adapter: ImageAdapter

    private val onReadDetailsListener: OnReadDetailsListener = object : OnReadDetailsListener {
        override fun onRead(result: Result) {
            if (result.isOk) {
                val variables = result.variables!!
                name = variables.getString(NAME)
                imgCount = variables.getInt(IMAGE_COUNT) ?: imgCount
                stamp = variables.getInt(SECURITY_STAMP)
                desc = variables.getString(DESCRIPTION)
                prizeStars = variables.getInt(STARS)
                info = variables.getInt(INFORMATION)
                quizDate = variables.getValue(ACTION_DATE)
                hasPlaces = variables.getInt(PLACES_LEFT)
                allPlaces = variables.getInt(PLACES_ALL)
                endDate = variables.getString(END_DATE)

                showDetails()
            } else {
                // todo: обробити помилку під'єднання до БД
            }
        }
    }
    private val onParticipateListener: OnParticipateListener = object : OnParticipateListener {
        override fun onAssume(result: Result) {
            if (result.isOk) {
                val variables = result.variables!!
                val stars = variables.getInt(STARS)
                val seats = variables.getInt(PLACES_NUMBER)
                val familiarized = variables.getValue(FAMILIAR_WITH_CONDITIONS)
                val endDate = getDate(variables.getString(END_DATE))
                showParticipateDialog(seats, stars, familiarized, endDate)
            } else {
                // todo: обробити помилку під'єднання до БД
            }
        }
    }
    fun getDate(str : String?) : String?{
        return if(str != null) {
            val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = inputFormat.parse(str)
            outputFormat.format(date)
        } else {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prize_details)

        prizeCode = intent.getIntExtra(PRIZE, -1)
        adapter = ImageAdapter(this)
        image_pager.adapter = adapter

        btn_participate.setOnClickListener(this)
        DbHelper.getPrizeDetail(prizeCode!!, onReadDetailsListener)
    }

    private fun showDetails() {
        prize_name.text = name
        description.text = Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT)
        for (i in 1..imgCount) {
//            images.add(resources.getString(R.string.prize_url, prizeCode, i))
            images.add("https://приз.укр/image/$prizeCode/$i?w=300&h=300")
        }
        adapter.notifyDataSetChanged()
//        prize_stars.text = prizeStars //todo: uncomment
        if (UserInfo.login != null && prizeStars!! <= UserInfo.stars!!)
        btn_participate.visibility = View.VISIBLE
    }

    private fun showParticipateDialog(numberOfSeats: Int?, stars: Int?, familiarized: Any?, endDate: String?) {
        val intent = Intent(this, DialogParticipationActivity::class.java)
        intent.putExtra(STARS, numberOfSeats)
        intent.putExtra(STARS, stars)
        intent.putExtra(PRIZE, prizeCode)
        if(familiarized is Boolean)
            intent.putExtra(FAMILIAR_WITH_CONDITIONS, familiarized)
        if (endDate != null)
            intent.putExtra(END_DATE, endDate)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        DbHelper.numberOfSeats(prizeCode!!, onParticipateListener)

    }

    inner class ImageAdapter(private val context: Context) : PagerAdapter() {

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.item_image, container, false)
            Glide.with(context).load(images[position]).into(view.image)
            container.addView(view, 0)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
