package edu.put.inf151739

import android.util.Log
import org.jdom2.Element

class Game(
    val id: Int?,
    val name: String,
    val yearOfProduction: String,
    val thumbnail: String
) {

    companion object {
        @JvmStatic
        fun parse(item: Element): Game {
            val idAttribute = item.getAttribute("objectid")
            val nameElement = item.getChild("name")
            val yearElement = item.getChild("yearpublished")
            val thumbnailElement = item.getChild("thumbnail")

            val id = idAttribute?.intValue
            val name = if (nameElement != null) nameElement.text else ""
            val yearOfProduction = if (yearElement != null) yearElement.text else ""
            val thumbnail = if (thumbnailElement != null) thumbnailElement.text else ""

            return Game(id, name, yearOfProduction, thumbnail)
        }
    }

}
