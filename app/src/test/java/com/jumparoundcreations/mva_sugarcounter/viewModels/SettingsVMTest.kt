package com.jumparoundcreations.mva_sugarcounter.viewModels

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SettingsVMTest {

    @Test
    fun check_initialisations_of_state_flows() {
        //Arrange
        val settingsVM = SettingsVM()

        //Check
        assertTrue(settingsVM.settingsScreenShown.value == true)
        assertTrue(settingsVM.faqScreenShown.value == false)
        assertTrue(settingsVM._faqExpandedId.value == -1L)
        assertTrue(settingsVM.gramThresholdSlider.value == 0F)
        assertTrue(settingsVM.gramThresholdDialogCheck.value == false)
        assertTrue(settingsVM.exportProgressIndicator.value == 0.1f)
        assertTrue(settingsVM.exportProgressIndicatorShown.value == false)
        assertTrue(settingsVM.dataSuccesfullyExportedShown.value == false)
        assertTrue(settingsVM.exportSuccessfully.value == true)
    }


    @Test
    fun check_for_correct_visibility() {

        //Arrange
        val settingsVM = SettingsVM()

        //Action
        settingsVM.actionChangeSettingsScreenVisibility(true)
        settingsVM.actionChangeFaqScreenVisibility(true)
        settingsVM.actionChangExportProgressIndicatorVisibility(true)
        settingsVM.actionChangeExportBottomSheetVisibility(true)

        //Check
        //assertEquals(settingsVM.settingsScreenShown, awaitItem())

    }


}