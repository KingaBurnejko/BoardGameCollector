package edu.put.inf151739

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.io.StringReader
import java.net.URL

class ApiHandler {
    val link = "https://www.boardgamegeek.com/xmlapi2/collection?subtype=boardgame&username="
    val linkExtensions = "https://boardgamegeek.com/xmlapi2/collection?subtype=boardgameexpansion&username="

    suspend fun makeRequest(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(url)
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()
                inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun parseXML(xml: String): MutableList<Element>? {
        val saxBuilder = SAXBuilder()
        val document: Document = saxBuilder.build(StringReader(xml))

        val rootNode: Element = document.rootElement

        return rootNode.children
    }
}
