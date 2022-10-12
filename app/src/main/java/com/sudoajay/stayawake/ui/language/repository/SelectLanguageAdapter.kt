package com.sudoajay.stayawake.ui.language.repository

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.stayawake.R

import com.sudoajay.stayawake.databinding.HolderSelectLanguageBinding
import javax.inject.Inject

class SelectLanguageAdapter @Inject constructor() :
    RecyclerView.Adapter<SelectLanguageAdapter.ViewHolder>() {

    var languageNonChangeList = mutableListOf<String>()
    var languageList = mutableListOf<String>()
    var languageValue = mutableListOf<String>()
    var selectedLanguage = "en"

    inner class ViewHolder(
        val binding: HolderSelectLanguageBinding? = null,
        val context: Context
    ) : RecyclerView.ViewHolder(binding?.root!!)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            binding = HolderSelectLanguageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = parent.context
        )

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val nonChangeList = languageNonChangeList[position]
        val list = languageList[position]
        val value = languageValue[position]

        viewHolder.binding!!.selectLanguageSameTextView.text = list
        viewHolder.binding.selectLanguageDifferentTextView.text =
            viewHolder.context.getString(R.string.select_language_bracket, nonChangeList)


        viewHolder.binding.tickImageView.visibility =
            if (value == selectedLanguage) View.VISIBLE else View.GONE

        viewHolder.binding.root.setOnClickListener {
            selectedLanguage = value
            notifyItemRangeChanged(0, languageList.size)
        }

    }


    override fun getItemCount() = languageList.size


}