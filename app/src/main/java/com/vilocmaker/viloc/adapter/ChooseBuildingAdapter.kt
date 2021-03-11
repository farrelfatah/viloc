package com.vilocmaker.viloc.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.model.Building
import com.vilocmaker.viloc.ui.authorization.AuthorizationActivity
import com.vilocmaker.viloc.util.Constant.Companion.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.recycler_rowlayout.view.*
import java.util.*

class ChooseBuildingAdapter : RecyclerView.Adapter<ChooseBuildingAdapter.MyViewHolder>() {

    private var buildingList: List<Building> = emptyList<Building>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorizeButton = itemView.findViewById<Button>(R.id.chooseBuilding_button)
        val seeOnMapButton = itemView.findViewById<Button>(R.id.seeOnMap_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_rowlayout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.chooseBuilding_buildingName.text = buildingList[position].buildingName
        holder.itemView.chooseBuilding_buildingAddress.text = buildingList[position].buildingAddress
        holder.itemView.chooseBuilding_buildingStatus.text = buildingList[position].buildingStatus.toString()
            .toLowerCase(Locale.ROOT)
            .capitalize(Locale.ROOT)

        holder.authorizeButton.setOnClickListener {
            val context = holder.authorizeButton.context
            val buildingId = buildingList[position]._id.toString().substring(6,30)
            val buildingName = buildingList[position].buildingName
            val intent = Intent(context, AuthorizationActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, buildingId).putExtra(EXTRA_MESSAGE, buildingName)
            }

            Log.d("Main", buildingId)
            Log.d("Main", buildingName)
            context.startActivity(intent)
        }

        holder.seeOnMapButton.setOnClickListener {
            val context = holder.seeOnMapButton.context
            val gmLocIntentUri = Uri.parse("geo:0,0?z=10&q=${buildingList[position].buildingAddress}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmLocIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }
    }

    override fun getItemCount(): Int {
        return buildingList.size
    }

    fun setData(newList: List<Building>) {
        buildingList = newList
        notifyDataSetChanged()
    }
}