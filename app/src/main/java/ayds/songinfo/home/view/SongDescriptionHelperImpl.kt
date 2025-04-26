package ayds.songinfo.home.view

import ayds.songinfo.home.model.entities.Song.EmptySong
import ayds.songinfo.home.model.entities.Song
import ayds.songinfo.home.model.entities.Song.SpotifySong

interface SongDescriptionHelper {
    fun getSongDescriptionText(song: Song = EmptySong): String
}

internal class SongDescriptionHelperImpl : SongDescriptionHelper {
    override fun getSongDescriptionText(song: Song): String {
        return when (song) {
            is SpotifySong ->
                "${
                    "Song: ${song.songName} " +
                            if (song.isLocallyStored) "[*]" else ""
                }\n" +
                        "Artist: ${song.artistName}\n" +
                        "Album: ${song.albumName}\n" +
                        "Released Date: ${song.releaseDate} (precision: ${song.releaseDatePrecision})"
            else -> "Song not found"
        }
    }

    private fun spotifySong.releaseDate() =
        when (this.releaseDatePrecision){
            "day" -> {
                val year = this.releaseDate.split("-").first()
                val month = this.releaseDate.split("-")[1]
                val day = this.releaseDate.split("-")[2]
                "$day/$month/$year"
            }
            "month" -> {
                val year = this.releaseDate.split("-").first()
                val month = this.releaseDate.split("-")[1]
                "${month.toInt().toMonthString()},$year"
            }
            "year" -> {
                val isLeapYear = isLeapYear(this.releaseDate.toInt())
                "${this.releaseDate}${if (isLeapYear) "(leap year)" else "(not a leap year)"}"
            }
            else -> ""
        }

    private
}

