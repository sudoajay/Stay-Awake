package com.sudoajay.stayawake.ui.home.repository

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.stayawake.databinding.HolderHomeOptionsItemBinding
import com.sudoajay.stayawake.model.HomeOption

import javax.inject.Inject

class HomeAdapter @Inject constructor() :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    var homeOptionList = mutableListOf<HomeOption>()

    inner class ViewHolder(
        val binding: HolderHomeOptionsItemBinding? = null,
        val context: Context
    ) : RecyclerView.ViewHolder(binding?.root!!)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            binding = HolderHomeOptionsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = parent.context
        )

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val item = homeOptionList[position]

        viewHolder.binding?.headingTextView?.text = item.heading?.let {
            viewHolder.context.getString(
                it
            )
        }
        viewHolder.binding?.descriptionTextview?.text= item.description?.let {
            viewHolder.context.getString(
                it
            )
        }
        item.image?.let {
            viewHolder.binding?.imageView?.setImageResource(
                it
            )
        }

        viewHolder.binding?.switchMaterial?.visibility = if(item.isShowSwitch == true) View.VISIBLE else View.GONE



    }


    override fun getItemCount() = homeOptionList.size


}