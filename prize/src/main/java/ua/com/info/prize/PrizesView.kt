package ua.com.info.prize

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import kotlinx.android.synthetic.main.item_prize.view.*
import ua.com.info.data.DataBase

/**
 *  Створено oor 17.08.2018.
 */

class PrizesView(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    interface OnCompleteListener {
        fun onComplete()
    }

    private val prizes = ArrayList<Prize>()
    private val adapter = PrizeAdapter(context, prizes)
    private var firstDisplayedItem: Int = 0
    private var lastDisplayedItem: Int = 0
    private val gridLayoutManager = GridLayoutManager(context, 2)
    //    private val queue = Volley.newRequestQueue(context.applicationContext)
    var loading = false
    var allImageLoaded = false


    private val onScrolledListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

//            firstDisplayedItem = gridLayoutManager.findFirstVisibleItemPosition() //
//            lastDisplayedItem = gridLayoutManager.findLastVisibleItemPosition()
//            if (!(loading || allImageLoaded)) {
//                loadImage(firstDisplayedItem)
//            }
        }
    }

    private val onCompleteListener = object : OnCompleteListener {
        override fun onComplete() {
            for (pos in firstDisplayedItem..(prizes.size - 1)) {
                if (prizes[pos].image == null) {
                    loadImage(pos)
                    return
                }
            }
            for (pos in lastDisplayedItem..0) {
                if (prizes[pos].image == null) {
                    loadImage(pos)
                    return
                }
            }
            allImageLoaded = true
        }
    }

    init {
        setAdapter(adapter)
        addOnScrollListener(onScrolledListener)
        layoutManager = gridLayoutManager
    }

    fun loadPrizes(listener: OnLoadErrorListener) {
        DataBase.db.getTable("API.Призи_Зчитати", null) { result ->
            if (result.isOk) {
                val table = result.table
                if (table != null)
                    for (row in table) {
                        val name = row.getString("Назва")
                        val image = row.getString("Код").toInt()
                        val starCount = row.getString("Зірочок").toInt()
                        prizes.add(Prize(name, image, starCount))
                    }

                adapter.notifyDataSetChanged()
//                onCompleteListener.onComplete()
            } else {
                listener.error(result)
            }
        }
    }

    private fun loadImage(position: Int) {
//        loading = true
//        val prize = prizes[position]
//        val url = "https://xn--g1abue.xn--j1amh/image/${prize.imageCode}?w=300&h=300"
//        val imageRequest = ImageRequest(url, Response.Listener<Bitmap> { image ->
//            prize.image = image
//            onCompleteListener.onComplete()
//            adapter.notifyDataSetChanged()
//            loading = false
//        }, 0, 0, ImageView.ScaleType.CENTER, null,
//                Response.ErrorListener { e ->
//                    val nextPos = position + 1
//                    if (prizes.size > nextPos) {
//                        firstDisplayedItem = nextPos
//                        onCompleteListener.onComplete()
//                    }
//                    error = e.toString()
//                    Log.e(LOG_TAG, "Response Error! $error")
//                })
//        queue.add(imageRequest)

    }

    class PrizeAdapter(private val context: Context, private val prizes: ArrayList<Prize>) : RecyclerView.Adapter<PrizeAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_prize, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return prizes.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val prize = prizes[position]
//            if (prizes[position].image != null)
//                holder.image.setImageBitmap(prizes[position].image)
//            else
//                holder.image.setImageBitmap(null)
            val url = "https://xn--g1abue.xn--j1amh/image/${prize.imageCode}?w=300&h=300"
            Glide.with(context)
                    .load(url)
                    .into(holder.image)

            holder.name.text = prize.name
            holder.starCount.text = prize.starCount.toString()
            holder.cardView.setOnClickListener { v ->
                val intent = Intent(context, PrizeDetailsActivity::class.java)
                intent.putExtra(PRIZE, prize.imageCode)
                context.startActivity(intent)
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.image
            val name: TextView = view.name
            val starCount: TextView = view.star_count
            val cardView: CardView = view.card_view
        }
    }
}