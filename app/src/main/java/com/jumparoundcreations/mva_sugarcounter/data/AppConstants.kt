package com.jumparoundcreations.mva_sugarcounter.data

import java.time.LocalDate
import java.time.ZoneId

object AppConstants {

    //<editor-fold desc="Time Values">
    val today = LocalDate.now()
    val endOfToday = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() - 1
    val currentTimestamp = System.currentTimeMillis() / 1000
    val endOf90DaysAgo = currentTimestamp - 7776000 // 7776000 = 90 days in seconds
    //</editor-fold>

}