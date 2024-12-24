package com.jumparoundcreations.mva_sugarcounter.worker

enum class WorkerDeletionPeriods(val deletionPeriods: Long, val deletionPeriodUI: String) {
    AfterHalfAYear(15778463, "ein"),
    AfterOneYear(31556926, "zwei"),
    AfterTwoYears(63113852, "drei"),
    AfterFiveYears(157784630, "fünf"),
}