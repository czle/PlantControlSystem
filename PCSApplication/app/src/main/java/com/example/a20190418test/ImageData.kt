package com.example.a20190418test

class ImageData {

        public var imageResource:Int = 0
        public lateinit var imageDescription: String

    constructor(imageResource:Int,imageDescription: String){
        this.imageResource = imageResource
        this.imageDescription = imageDescription
    }

}