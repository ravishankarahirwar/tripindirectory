package directory.tripin.com.tripindirectory.newlookcode

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.newlookcode.pojos.FleetSelectPojo
import kotlinx.android.synthetic.main.item_truck_select.view.*

class FleetsSelectAdapter(val items: ArrayList<FleetSelectPojo>, val context: Context, val listner : OnFleetSelectedListner, val type :Int): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_truck_select, parent, false))
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFleetname.text = items[position].name
        holder.ivFleetImage.setImageDrawable(items[position].drawable)
        if(items[position].isSelected){

            holder.ivFleetImage.background = ContextCompat.getDrawable(context,R.drawable.border_sreoke_cyne_bg)
            holder.ivFleetImage.setPadding(6,6,6,6)
            listner.onFleetSelected(items[position].name)
            val matrix = ColorMatrix()
            matrix.setSaturation(1f)
            val filter = ColorMatrixColorFilter(matrix)
            holder.ivFleetImage.colorFilter = filter
            holder.tvFleetname.alpha = 1f
        }else{
            holder.ivFleetImage.background = null
            listner.onFleetDeslected(items[position].name)
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(matrix)
            holder.ivFleetImage.colorFilter = filter
            holder.tvFleetname.alpha = 0.6f
        }


        holder.itemView.setOnClickListener {
            if(type==0){
                items[position].isSelected = !items[position].isSelected
                notifyItemChanged(position)
            }
        }

    }


}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvFleetname = view.tv_fleet_name
    val ivFleetImage = view.iv_fleettype
    val constrainfull = view.constrainfleetitem
}