package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Pedro Silva
 */
data class Device(
        @Expose
        @SerializedName("device_key")
        val deviceKey: String
)