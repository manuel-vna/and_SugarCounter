package com.jumparoundcreations.sugarcounter.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent


class CounterVM : ViewModel(), KoinComponent {

    private val _noBarcodeYetInfoTitle = MutableStateFlow(false)
    val noBarcodeYetInfoTitle = _noBarcodeYetInfoTitle.asStateFlow()

    private val _noBarcodeYetInfoDescription = MutableStateFlow(false)
    val noBarcodeYetInfoDescription = _noBarcodeYetInfoDescription.asStateFlow()

    private val _barcodeNumber = MutableStateFlow("")
    val barcodeNumber = _barcodeNumber.asStateFlow()

    fun removeLastBarcodeInput() {
        _noBarcodeYetInfoTitle.value = false
    }

    fun actionChangeNOBarcodeInfoYetDescription(visibility: Boolean) {
        _noBarcodeYetInfoDescription.value = visibility
    }


}
