package directory.tripin.com.tripindirectory.newprofiles

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.item_city_new.view.*
import kotlinx.android.synthetic.main.item_operator.view.*

class OperationCitiesAdapter(val cities : List<String>, val context: Context, val callback : CityInteractionCallbacks) : RecyclerView.Adapter<OperationCitiesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_city_new, parent, false))
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCirtName.text = cities[position]
        holder.tvRemove.setOnClickListener {
            if(cities.isNotEmpty()){
                callback.onCityRemoved(cities[position])
            }
        }
    }

    class  ViewHolder (view: View) : RecyclerView.ViewHolder(view)  {
        val tvCirtName = view.cirynamemanage
        val tvRemove = view.removecity
    }
}