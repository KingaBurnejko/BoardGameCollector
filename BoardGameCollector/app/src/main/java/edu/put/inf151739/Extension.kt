package edu.put.inf151739

import android.util.Log
import org.jdom2.Element

class Extension(
    val id: Int?,
    val name: String,
    val yearOfProduction: String,
    val thumbnail: String
) {

    companion object {
        @JvmStatic
        fun parse(item: Element): Extension {
            return Extension(
                item.getAttribute("objectid").intValue,
                item.getChild("name").text,
                item.getChild("yearpublished").text,
                item.getChild("thumbnail").text
            )
        }
    }
}
