package com.jumparoundcreations.mva_sugarcounter.viewModels

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.BeforeTest
import kotlin.test.assertNotEquals

@RunWith(JUnit4::class)
class SettingsVMTest {

    @BeforeTest
    fun setup() {
        //val settingsVM = SettingsVM()
    }

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
    fun check_state_settingsScreenShown() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()
            settingsVM.settingsScreenShown.test {
                //Check
                assertEquals(true, awaitItem())
                //Action
                settingsVM.actionChangeSettingsScreenVisibility(false)
                //Check
                assertEquals(false, awaitItem())
            }
        }
    }

    @Test
    fun check_state_faqScreenShown() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()
            settingsVM.faqScreenShown.test {
                //Check
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionChangeFaqScreenVisibility(true)
                //Check
                assertEquals(true, awaitItem())
            }
        }
    }

    @Test
    fun check_state_faqSingleSelectMode() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()
            settingsVM.faqSingleSelectMode.test {
                //Check
                assertEquals(-1L, awaitItem())
                //Action
                settingsVM.actionChangeExpandedId(2L)
                //Check
                assertEquals(2L, awaitItem())
                //Action
                settingsVM.actionChangeExpandedId(3L)
                //Check
                assertNotEquals(5L, awaitItem())
            }
        }
    }

    @Test
    fun check_state_gramThresholdSlider() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()
            settingsVM.gramThresholdSlider.test {
                //Check
                assertEquals(0F, awaitItem())
                //Action
                settingsVM.actionUpdateGramThresholdSlider(48F)
                //Check
                assertEquals(48F, awaitItem())
            }
        }
    }

    @Test
    fun check_state_gramThresholdDialogCheck() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()
            settingsVM.gramThresholdDialogCheck.test {

                //Check
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionGramThresholdDialogCheck(true)
                //Check
                assertEquals(true, awaitItem())
            }
        }
    }

    @Test
    fun check_state_exportProgressIndicator() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.exportProgressIndicator.test {
                //Check
                assertEquals(0.1f, awaitItem())
                //Action
                settingsVM.actionIncrementExportProgressIndicator()
                //Check
                assertEquals(0.2f, awaitItem())
                //Action: Increase three times
                settingsVM.actionIncrementExportProgressIndicator()
                settingsVM.actionIncrementExportProgressIndicator()
                settingsVM.actionIncrementExportProgressIndicator()
                //Check
                assertEquals(0.3f, awaitItem())
                assertEquals(0.4f, awaitItem())
                assertEquals(0.5f, awaitItem())
                //Action
                settingsVM.actionIncrementExportProgressIndicator()
                // Check
                assertEquals(0.6f, awaitItem())
            }
        }
    }

    @Test
    fun check_state_exportProgressIndicatorShown() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.exportProgressIndicatorShown.test {
                //Check for Initial value
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionChangExportProgressIndicatorVisibility(true)
                //Check
                assertEquals(true, awaitItem())
            }
        }
    }

    @Test
    fun check_state_dataSuccessfullyExportedShown() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.dataSuccesfullyExportedShown.test {
                //Check for Initial value
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionChangeExportBottomSheetVisibility(true)
                //Check
                assertEquals(true, awaitItem())
            }
        }
    }

    @Test
    fun check_state_exportSuccessfully() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.exportSuccessfully.test {
                //Check for Initial value
                assertEquals(true, awaitItem())
                //Action
                settingsVM.actionChangeExportSuccessfully(false)
                //Check
                assertEquals(false, awaitItem())
            }
        }
    }

}