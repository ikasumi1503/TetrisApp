package com.example.tetrisapp.feature_game.util

object LevelConstants {
    // delayLimit
    // level
    // time

    val levelsInfo = mapOf(
        // Level to Pair(time, delayLimit)
        1 to Pair(0L, 1000L),
        2 to Pair(20000L, 900L),
        3 to Pair(40000L, 800L),
        5 to Pair(60000L, 700L),
        6 to Pair(80000L, 600L),
        7 to Pair(100000L, 500L),
        8 to Pair(120000L, 400L),
        9 to Pair(140000L, 300L),
        10 to Pair(160000L, 200L)
    )
}