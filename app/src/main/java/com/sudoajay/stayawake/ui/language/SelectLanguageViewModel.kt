package com.sudoajay.stayawake.ui.language

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sudoajay.stayawake.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    var isSelectLanguageProgress: MutableLiveData<Boolean> = MutableLiveData()
    var selectLanguageNonChangeList = mutableListOf<String>()

    var selectLanguageValue = mutableListOf<String>()


    init {
        selectLanguageNonChangeList =  application.resources.getStringArray(R.array.languageNonChangeList).toMutableList()
        selectLanguageValue = application.resources.getStringArray(R.array.languageValues).toMutableList()

        loadHideProgress()
    }
    private fun loadHideProgress() {
        isSelectLanguageProgress.value = true
    }

}