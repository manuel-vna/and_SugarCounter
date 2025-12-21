package com.jumparoundcreations.sugarcounter.viewModels

import android.content.SharedPreferences
import app.cash.turbine.test
import com.jumparoundcreations.sugarcounter.data.settingsData.BottomSheetsSettings
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.assertNotEquals


class SettingsVMTest : KoinTest {

    private lateinit var settingsVM: SettingsVM

    private val mockkSharedPrefsMain: SharedPreferences = mockk(relaxed = true)

    @Before
    fun setup() {
        startKoin {
            modules(
                module {
                    single<SharedPreferences> {
                        mockkSharedPrefsMain
                    }
                }
            )
        }
        settingsVM = SettingsVM()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun check_initialisations_of_state_flows() {
        //Check
        assertTrue(settingsVM.faqExpandedId.value == -1L)
        assertTrue(settingsVM.gramThresholdSlider.value == 0F)
        assertTrue(settingsVM.gramThresholdDialogCheck.value == false)
        assertTrue(settingsVM.exportProgressIndicator.value == 0.1f)
        assertTrue(settingsVM.exportProgressIndicatorShown.value == false)
        assertTrue(settingsVM.dataSuccessfullyExportedShown.value == false)
        assertTrue(settingsVM.exportSuccessfully.value == true)
        assertTrue(settingsVM.bottomSheetsSettings.value == BottomSheetsSettings.NONE)
        assertTrue(settingsVM.entriesDeletionActivated.value == false)
    }

    @Test
    fun check_state_faqSingleSelectMode() {
        runTest {
            settingsVM.faqExpandedId.test {
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
    fun check_state_entriesDeletionBottomSheetShown() {
        runTest {
            settingsVM.bottomSheetsSettings.test {
                //Check for Initial value
                assertEquals(BottomSheetsSettings.NONE, awaitItem())
                //Action
                settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.ENTRIES_DELETION)
                //Check
                assertEquals(BottomSheetsSettings.ENTRIES_DELETION, awaitItem())
            }
        }
    }

    @Test
    fun check_state_entriesDeletionActivated() {
        runTest {
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