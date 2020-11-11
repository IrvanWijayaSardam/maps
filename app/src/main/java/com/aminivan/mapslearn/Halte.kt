package com.aminivan.mapslearn

import android.location.Location
import android.media.Image

class Halte {
    var name : String?=null
    var des : String?=null
    var image : Int?=null
    var location: Location?= null
    var IsCatch : Boolean?= false

    constructor(image: Int, name : String, des: String , lat: Double, log : Double) {
        this.des = des
        this.image = image
        this.location = Location(name)
        this.location!!.latitude = lat
        this.location!!.longitude = log
        this.name = name
        this.IsCatch = false
    }
}