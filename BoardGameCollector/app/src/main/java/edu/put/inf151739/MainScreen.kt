package edu.put.inf151739

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

class MainScreen : AppCompatActivity() {

    private lateinit var db: MyDBHandler
    private lateinit var textViewUserName: TextView
    private lateinit var gameCount: TextView
    private lateinit var extensionCount: TextView
    private lateinit var lastSynchronizedText: TextView

    private companion object {
        const val USER_NAME_EXTRA = "user_name"
        const val CURRENT_DATE_TIME_EXTRA = "current_date_time"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        db = MyDBHandler(this, null, null, 1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        gameCount = findViewById(R.id.games_count)
        textViewUserName = findViewById(R.id.user_name)
        extensionCount = findViewById(R.id.addson_count)
        lastSynchronizedText = findViewById(R.id.last_synchronized)

        val userName = intent.getStringExtra(USER_NAME_EXTRA)
        textViewUserName.text = userName

        gameCount.text = db.countGames().toString()
        extensionCount.text = db.countExtensions().toString()

        val lastSynchronizedDateTime = intent.getLongExtra(CURRENT_DATE_TIME_EXTRA, 0)
        if (lastSynchronizedDateTime != 0L) {
            val lastSynchronizedDate = Date(lastSynchronizedDateTime)
            val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(lastSynchronizedDate)
            lastSynchronizedText.text = formattedDateTime.toString()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    fun onClearDataButtonClick(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Czy jesteś pewny?")
            .setPositiveButton("Tak") { dialog, which ->
                db.deleteTable()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Nie") { dialog, which ->
                // Nic nie robimy, zostajemy na bieżącej aktywności
            }
            .show()
    }

    fun onSynchronizationButtonClick(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Czy na pewno chcesz dokonać synchronizacji?")
            .setPositiveButton("Tak") { dialog, which ->
                db.deleteTable()

                // Tworzenie bazy danych
                db = MyDBHandler(this, null, null, 1)
                db.onCreate(db.writableDatabase)

                val userName = textViewUserName.text.toString()
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
                            // Aktualizacja interfejsu użytkownika
                            gameCount.text = db.countGames().toString()
                            extensionCount.text = db.countExtensions().toString()

                            val currentDate = Date()
                            val formattedDateTime =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(currentDate)
                            lastSynchronizedText.text = formattedDateTime

                            Toast.makeText(
                                this@MainScreen,
                                "Synchronizacja zakończona pomyślnie",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainScreen,
                                "Wystąpił błąd podczas pobierania danych",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Nie") { dialog, which ->
                // Nic nie robimy, zostajemy na bieżącej aktywności
            }
            .show()
    }




    fun onGamesDetailsButtonClick(view: View) {
        val intent = Intent(this, GamesDetails::class.java)
        startActivity(intent)
    }

    fun onAddsonDetailsButtonClick(view: View) {
        val intent = Intent(this, AddsonDetails::class.java)
        startActivity(intent)
    }
}
