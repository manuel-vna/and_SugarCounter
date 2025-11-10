package com.jumparoundcreations.sugarcounter.data.counterData

sealed class CounterState {
    object Loading : CounterState()
    object Error : CounterState()
    data class Success(val data: SuccessData) : CounterState()
}

data class SuccessData(
    val example: String
)

