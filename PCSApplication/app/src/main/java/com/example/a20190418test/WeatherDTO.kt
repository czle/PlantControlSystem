package com.example.a20190418test

class WeatherDTO {
    var temp:Double = 0.0
    var humi:Double = 0.0
    var pressure:Int = 0
    var mainWeather:String = ""
    var descriptionWeather:String = ""

    constructor(temp:Double,humi: Double,pressure:Int,mainWeather:String,descriptionWeather:String){
        this.temp = temp
        this.humi = humi
        this.pressure = pressure
        this.mainWeather = mainWeather
        this.descriptionWeather = descriptionWeather
    }
    constructor(){

    }

}