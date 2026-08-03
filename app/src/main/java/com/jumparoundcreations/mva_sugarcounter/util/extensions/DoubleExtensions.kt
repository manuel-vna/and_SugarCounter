package com.jumparoundcreations.mva_sugarcounter.util.extensions

import kotlin.math.round

fun Double.roundToOneDecimal(): Double = round(this * 10) / 10