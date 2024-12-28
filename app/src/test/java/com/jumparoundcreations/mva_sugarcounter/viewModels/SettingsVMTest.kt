package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.content.SharedPreferences
import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.assertNotEquals

@RunWith(JUnit4::class)
class SettingsVMTest : KoinTest {

    private val mockkSharedPrefsMain: SharedPreferences = mockk(relaxed = true)

    @Before
    fun setup() {
        startKoin {
            modules(
                module {
                    single<SharedPreferences>(qualifier = named("sharedPrefsMain")) {
                        mockkSharedPrefsMain
                    }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
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
        assertTrue(settingsVM.caloriesThresholdSlider.value == 0F)
        assertTrue(settingsVM.gramThresholdDialogCheck.value == false)
        assertTrue(settingsVM.exportProgressIndicator.value == 0.1f)
        assertTrue(settingsVM.exportProgressIndicatorShown.value == false)
        assertTrue(settingsVM.dataSuccessfullyExportedShown.value == false)
        assertTrue(settingsVM.exportSuccessfully.value == true)
        assertTrue(settingsVM.caloriesCounterActivated.value == false)
        assertTrue(settingsVM.entriesDeletionBottomSheetShown.value == false)
        assertTrue(settingsVM.entriesDeletionActivated.value == false)
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
    fun check_state_caloriesThresholdSlider() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()
            settingsVM.caloriesThresholdSlider.test {
                //Check
                assertEquals(0F, awaitItem())
                //Action
                settingsVM.actionUpdateCaloriesThresholdSlider(800F)
                //Check
                assertEquals(800F, awaitItem())
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

            settingsVM.dataSuccessfullyExportedShown.test {
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

    @Test
    fun check_state_caloriesCounterActivated() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.caloriesCounterActivated.test {
                //Check for Initial value
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionChangeCaloriesCounterGeneral(true)
                //Check
                assertEquals(true, awaitItem())
                //Action
                settingsVM.actionChangeCaloriesCounterGeneral(false)
                //Check
                assertEquals(false, awaitItem())
            }
        }
    }

    @Test
    fun check_state_entriesDeletionBottomSheetShown() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.entriesDeletionBottomSheetShown.test {
                //Check for Initial value
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionChangeEntriesDeletionBottomSheetShown(true)
                //Check
                assertEquals(true, awaitItem())
            }
        }
    }

    @Test
    fun check_state_entriesDeletionActivated() {
        runTest {
            //Arrange
            val settingsVM = SettingsVM()

            settingsVM.entriesDeletionActivated.test {
                //Check for Initial value
                assertEquals(false, awaitItem())
                //Action
                settingsVM.actionChangeEntriesDeletionActivated(true)
                //Check
                assertEquals(true, awaitItem())
            }
        }
    }

}