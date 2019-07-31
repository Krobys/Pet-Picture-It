package com.akrivonos.app_kotlin.Models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rsp", strict = false)
class Rsp {
    @field:Element(name = "photos")
    lateinit var photos: Photos

    @Root(name = "photos", strict = false)
    class Photos {
        @field:ElementList(entry = "photo", inline = true)
        lateinit var photo: List<Photo>
    }

    @Root(name = "photo", strict = false)
    class Photo {
        @field:Attribute(name = "id")
        lateinit var id: String
        @field:Attribute(name = "secret")
        lateinit var secret: String
        @field:Attribute(name = "server")
        lateinit var server: String
        @field:Attribute(name = "farm")
        lateinit var farm: String
    }
}

