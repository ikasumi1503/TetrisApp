package com.example.tetrisapp.feature_game.util

object LevelConstants {
    // delayLimit
    // level
    // time

    val levelsInfo = mapOf(
        1 to Pair(0L, 1000L),
        2 to Pair(20_000L, 900L),
        3 to Pair(40_000L, 800L),
        4 to Pair(60_000L, 700L),   // ← 修正
        5 to Pair(80_000L, 600L),
        6 to Pair(100_000L, 500L),
        7 to Pair(120_000L, 400L),
        8 to Pair(140_000L, 300L),
        9 to Pair(160_000L, 200L),
        10 to Pair(180_000L, 180L),   // 200→180 の微調整
        11 to Pair(200_000L, 160L),
        12 to Pair(220_000L, 150L),
        13 to Pair(240_000L, 140L),
        14 to Pair(260_000L, 130L),
        15 to Pair(280_000L, 120L),
        16 to Pair(300_000L, 110L),
        17 to Pair(320_000L, 100L),
        18 to Pair(340_000L, 90L),
        19 to Pair(360_000L, 80L),
        20 to Pair(380_000L, 70L),
        21 to Pair(400_000L, 60L),
        22 to Pair(420_000L, 50L),
        23 to Pair(440_000L, 45L),
        24 to Pair(460_000L, 40L),
        25 to Pair(480_000L, 35L),
    )
}