package com.yanyushkin.kudago.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class City(private var name: String, private var shortEnglishName: String) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
        name = parcel.readString()
        shortEnglishName = parcel.readString()
    }

    val nameInfo: String
        get() = name

    val shortEnglishNameInfo: String
        get() = shortEnglishName

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest != null) {
            dest.writeValue(name)
            dest.writeValue(shortEnglishName)
        }
    }

    companion object CREATOR : Parcelable.Creator<City> {
        override fun createFromParcel(parcel: Parcel): City {
            return City(parcel)
        }

        override fun newArray(size: Int): Array<City?> {
            return arrayOfNulls(size)
        }
    }
}