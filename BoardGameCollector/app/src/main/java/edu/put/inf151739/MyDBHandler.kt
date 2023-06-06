package edu.put.inf151739

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "games.db"

        const val TABLE_DLC = "extensions"

        const val TABLE_GAMES = "games"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_YEAR = "year_of_prod"
        const val COLUMN_THUMBNAIL = "thumbnail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = """CREATE TABLE IF NOT EXISTS 
            $TABLE_GAMES (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_YEAR TEXT,
                $COLUMN_THUMBNAIL TEXT
            )"""

        val CREATE_EXTENSIONS_TABLE = """CREATE TABLE IF NOT EXISTS 
            $TABLE_DLC (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_YEAR TEXT,
                $COLUMN_THUMBNAIL TEXT
            )"""

        db.execSQL(CREATE_GAMES_TABLE)
        db.execSQL(CREATE_EXTENSIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DLC")

        onCreate(db)
    }

    fun addGame(game: Game) {
        val values = ContentValues().apply {
            put(COLUMN_ID, game.id)
            put(COLUMN_NAME, game.name)
            put(COLUMN_YEAR, game.yearOfProduction)
            put(COLUMN_THUMBNAIL, game.thumbnail)
        }

        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun addExtension(extension: Extension) {
        val values = ContentValues().apply {
            put(COLUMN_ID, extension.id)
            put(COLUMN_NAME, extension.name)
            put(COLUMN_YEAR, extension.yearOfProduction)
            put(COLUMN_THUMBNAIL, extension.thumbnail)
        }

        val db = this.writableDatabase
        db.insert(TABLE_DLC, null, values)
        db.close()
    }

    fun findAllGames(): List<Game> {
        val query = "SELECT * FROM $TABLE_GAMES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val games = mutableListOf<Game>()
        while (cursor.moveToNext()) {
            games.add(
                Game(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
            )
        }
        cursor.close()
        db.close()
        return games
    }

    fun findAllExtensions(): List<Extension> {
        val query = "SELECT * FROM $TABLE_DLC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val extensions = mutableListOf<Extension>()
        while (cursor.moveToNext()) {
            extensions.add(
                Extension(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
            )
        }
        cursor.close()
        db.close()
        return extensions
    }

    fun findGameById(id: Int): Game? {
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID = $id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var game: Game? = null
        if (cursor.moveToFirst()) {
            game = Game(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
            )
        }
        cursor.close()
        db.close()
        return game
    }

    fun findExtensionById(id: Int): Extension? {
        val query = "SELECT * FROM $TABLE_DLC WHERE $COLUMN_ID = $id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var extension: Extension? = null
        if (cursor.moveToFirst()) {
            extension = Extension(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
            )
        }
        cursor.close()
        db.close()
        return extension
    }

    fun countGames(): Int {
        val query = "SELECT COUNT(*) FROM $TABLE_GAMES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun countExtensions(): Int {
        val query = "SELECT COUNT(*) FROM $TABLE_DLC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun deleteTable() {
        val query = "DROP TABLE $TABLE_GAMES"
        val query2 = "DROP TABLE $TABLE_DLC"
        val db = this.writableDatabase
        db.execSQL(query)
        db.execSQL(query2)
        db.close()
    }
}
