package com.sudoajay.stayawake.model


data class HomeOption(
    var uniqueId:Int?=null,
    var heading: Int? = null,
    var image: Int? = null,
    var description:Int? = null ,
    var isOn:Boolean? = false,
    var isShowSwitch:Boolean? = true

)