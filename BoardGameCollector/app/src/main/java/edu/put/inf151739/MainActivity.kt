package edu.put.inf151739

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var editTextUserName: EditText
    private lateinit var buttonCheckUserName: Button
    private lateinit var loadingText: TextView
    private lateinit var db: MyDBHandler

    private companion object {
        const val USER_NAME_EXTRA = "user_name"
        const val CURRENT_DATE_TIME_EXTRA = "current_date_time"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = MyDBHandler(this, null, null, 1)
        db.onCreate(db.writableDatabase)

        editTextUserName = findViewById(R.id.username_input)
        buttonCheckUserName = findViewById(R.id.enter_button)
        loadingText = findViewById(R.id.loading_text)

        buttonCheckUserName.setOnClickListener {
            val userName = editTextUserName.text.toString()
            val link = "https://boardgamegeek.com/xmlapi2/user?name=$userName"

            CoroutineScope(Dispatchers.Main).launch(Dispatchers.IO) {
                try {
                    val url = URL(link)
                    val connection = url.openConnection()
                    val inputStream = connection.getInputStream()
                    val response = inputStream.bufferedReader().use { it.readText() }

                    val documentBuilder =
                        DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    val inputSource = InputSource(StringReader(response))
                    val document: Document = documentBuilder.parse(inputSource)
                    document.documentElement.normalize()

                    val userIdAttribute =
                        document.getElementsByTagName("user").item(0).attributes.getNamedItem("id")
                    val userId = userIdAttribute?.nodeValue
                    if (!userId.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            loadingText.visibility = View.VISIBLE
                        }

                        val api = ApiHandler()
                        val gamesResponse = api.makeRequest(api.link + userName)
                        gamesResponse?.let { response ->
                            val games = api.parseXML(response)
                            games?.forEach { gameElement ->
                                try {
                                    val game = Game.parse(gameElement)
                                    db.addGame(game)
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error parsing game: ${e.message}")
                                }
                            }
                        }

                        val extensionsResponse = api.makeRequest(api.linkExtensions + userName)
                        extensionsResponse?.let { response ->
                            val extensions = api.parseXML(response)
                            extensions?.forEach { extensionElement ->
                                try {
                                    val extension = Extension.parse(extensionElement)
                                    db.addExtension(extension)
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error parsing extension: ${e.message}")
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            loadingText.visibility = View.GONE
                            goToMainScreen(userName)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Użytkownik o podanej nazwie nie istnieje",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Wystąpił błąd podczas pobierania danych",
                            Toast.LENGTH_LONG
                        ).show()
                        loadingText.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun goToMainScreen(userName: String) {
        val intent = Intent(this, MainScreen::class.java)
        intent.putExtra(USER_NAME_EXTRA, userName)

        val currentDate = Date()
        val currentDateTimeMillis = currentDate.time

        intent.putExtra(CURRENT_DATE_TIME_EXTRA, currentDateTimeMillis)

        startActivity(intent)
    }
}
