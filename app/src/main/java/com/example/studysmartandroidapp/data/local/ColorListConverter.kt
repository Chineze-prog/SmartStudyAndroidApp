package com.example.studysmartandroidapp.data.local

import androidx.room.TypeConverter

//use @TypeConverter so Room knows that the functions are used to convert some value from/to an entity

// since we cant store a list of colors in one entry on the table, we have to converts the list into
// one string and when we want to use the list convert the string back to the list of colors
class ColorListConverter {
    @TypeConverter
    fun fromColorList(colorList: List<Int>): String {
        return colorList.joinToString(","){ colorInt -> colorInt.toString() }
    }

    @TypeConverter
    fun toColorList(colorListString: String): List<Int> {
        return colorListString.split(",").map{ token -> token.toInt() }
    }
}