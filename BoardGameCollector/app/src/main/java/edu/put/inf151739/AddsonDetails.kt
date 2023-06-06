package edu.put.inf151739

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class AddsonDetails : AppCompatActivity() {

    private val db by lazy { MyDBHandler(this, null, null, 1) }
    private val gamesTable by lazy { findViewById<TableLayout>(R.id.addson_table) }
    private var gamesList = mutableListOf<Extension?>()
    private var isTitleSortedAscending = true
    private var isYearSortedAscending = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addson_details)

        gamesList = db.findAllExtensions().toMutableList()
        populateGamesTable()

        val titlesSortButton = findViewById<Button>(R.id.titles_sort_button)
        titlesSortButton.setOnClickListener {
            sortGamesByTitle()
        }

        val yearsSortButton = findViewById<Button>(R.id.years_sort_button)
        yearsSortButton.setOnClickListener {
            sortGamesByYear()
        }
    }

    private fun populateGamesTable() {
        val columnWidths = intArrayOf(80, 100, 200, 80)

        val headerRow = TableRow(this)
        headerRow.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)

        for (i in columnWidths.indices) {
            val headerTextView = TextView(this)
            headerTextView.text = when (i) {
                0 -> "Id dodatku"
                1 -> "Zdjęcie"
                2 -> "Nazwa dodatku"
                3 -> "Rok wydania"
                else -> ""
            }
            headerTextView.gravity = Gravity.CENTER
            headerTextView.setPadding(10, 10, 10, 10)
            headerTextView.layoutParams = TableRow.LayoutParams(columnWidths[i], TableRow.LayoutParams.WRAP_CONTENT)

            headerTextView.setTypeface(null, Typeface.BOLD)

            headerRow.addView(headerTextView)
        }

        gamesTable.addView(headerRow)

        for (game in gamesList) {
            val gameId = game?.id ?: ""
            val gameName = game?.name ?: ""
            val gameYear = game?.yearOfProduction ?: ""
            val gameThumbnail = game?.thumbnail ?: ""

            val row = TableRow(this)
            row.isClickable = true
            row.setOnClickListener {

                val intent = Intent(this, DetailsView::class.java)
                intent.putExtra("gameId", gameId.toString())
                intent.putExtra("gameTitle", gameName)
                intent.putExtra("gameYear", gameYear)
                intent.putExtra("gamePhoto", gameThumbnail)
                startActivity(intent)
            }

            for (i in columnWidths.indices) {
                val cellView = when (i) {
                    0 -> {
                        val textView = TextView(this)
                        textView.text = gameId.toString()
                        textView.gravity = Gravity.CENTER
                        textView.setPadding(10, 10, 10, 10)
                        textView.layoutParams = TableRow.LayoutParams(columnWidths[i], TableRow.LayoutParams.WRAP_CONTENT)
                        textView
                    }
                    1 -> {
                        val container = LinearLayout(this)
                        container.gravity = Gravity.CENTER
                        container.setPadding(10, 10, 10, 10)
                        container.layoutParams = TableRow.LayoutParams(columnWidths[i], TableRow.LayoutParams.WRAP_CONTENT)

                        val imageView = ImageView(this)
                        Picasso.get().load(gameThumbnail).into(imageView)

                        container.addView(imageView)
                        container
                    }
                    2 -> {
                        val textView = TextView(this)
                        textView.text = gameName
                        textView.gravity = Gravity.CENTER
                        textView.setPadding(10, 10, 10, 10)
                        textView.layoutParams = TableRow.LayoutParams(columnWidths[i], TableRow.LayoutParams.WRAP_CONTENT)
                        textView
                    }
                    3 -> {
                        val textView = TextView(this)
                        textView.text = gameYear
                        textView.gravity = Gravity.CENTER
                        textView.setPadding(10, 10, 10, 10)
                        textView.layoutParams = TableRow.LayoutParams(columnWidths[i], TableRow.LayoutParams.WRAP_CONTENT)
                        textView
                    }
                    else -> {
                        View(this)
                    }
                }

                row.addView(cellView)
            }

            gamesTable.addView(row)
        }
    }

    private fun sortGamesByTitle() {
        val sortedList = if (isTitleSortedAscending) {
            gamesList.sortedBy { game -> game?.name?.toString() }
        } else {
            gamesList.sortedByDescending { game -> game?.name?.toString() }
        }
        isTitleSortedAscending = !isTitleSortedAscending
        updateGamesTable(sortedList)
    }

    private fun sortGamesByYear() {
        val sortedList = if (isYearSortedAscending) {
            gamesList.sortedBy { game -> game?.yearOfProduction?.toString() }
        } else {
            gamesList.sortedByDescending { game -> game?.yearOfProduction?.toString() }
        }
        isYearSortedAscending = !isYearSortedAscending
        updateGamesTable(sortedList)
    }

    private fun updateGamesTable(sortedList: List<Extension?>) {
        gamesList.clear()
        gamesList.addAll(sortedList)
        gamesTable.removeAllViews()
        populateGamesTable()
    }
}
