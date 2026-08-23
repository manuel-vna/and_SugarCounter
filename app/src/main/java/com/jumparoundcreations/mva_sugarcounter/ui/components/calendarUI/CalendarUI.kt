package com.jumparoundcreations.mva_sugarcounter.ui.components.calendarUI

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.historyData.CalendarDayUi
import com.jumparoundcreations.mva_sugarcounter.data.historyData.DayStatus
import com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature.EntryCalendarStates
import com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature.EntryCalendarViewModel
import com.jumparoundcreations.mva_sugarcounter.util.extensions.formatMonthTitle
import com.jumparoundcreations.mva_sugarcounter.util.extensions.toBackgroundColor
import com.jumparoundcreations.mva_sugarcounter.util.extensions.toMondayBasedIndex
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryCalendarFeature(
    viewModel: EntryCalendarViewModel = koinViewModel()
) {

    val entryCalendarStates by viewModel.entryCalendarStates.collectAsStateWithLifecycle()
    val currentMonth = remember { YearMonth.now() }
    val pagerState = rememberPagerState(initialPage = 11, pageCount = { 12 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp)
        ) { page ->
            val month = remember(page) { currentMonth.minusMonths((11 - page).toLong()) }
            val days = remember(month) { buildMonthGrid(month) }

            MonthPage(
                entryCalendarStates = entryCalendarStates,
                month = month,
                days = days,
                onDayClick = { day ->
                    println("Clicked day: ${day.date}")
                    Log.d("EntryCalendarFeature", "Clicked day: ${day.date}")
                }
            )
        }
    }
}

@Composable
private fun MonthPage(
    entryCalendarStates: EntryCalendarStates,
    month: YearMonth,
    days: List<CalendarDayUi>,
    onDayClick: (CalendarDayUi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = month.formatMonthTitle(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        WeekdayHeader()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val rows = days.chunked(7)
            rows.forEach { rowDays ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowDays.forEach { day ->
                        Box(modifier = Modifier.weight(1f)) {
                            DayCell(
                                entryCalendarStates = entryCalendarStates,
                                day = day,
                                onClick = { onDayClick(day) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val weekdays = listOf(
        R.string.weekday_mon,
        R.string.weekday_tue,
        R.string.weekday_wed,
        R.string.weekday_thu,
        R.string.weekday_fri,
        R.string.weekday_sat,
        R.string.weekday_sun
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weekdays.forEach { weekdayRes ->
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = weekdayRes),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    entryCalendarStates: EntryCalendarStates,
    day: CalendarDayUi,
    onClick: () -> Unit
) {
    val status = remember(entryCalendarStates.gramSummariesPerDate, day.date) {
        if (!day.isInCurrentMonth) {
            DayStatus.NO_DATA
        } else {
            val dateString = day.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val summary = entryCalendarStates.gramSummariesPerDate.find { it.date == dateString }
            when (summary?.totalGram) {
                null -> DayStatus.NO_DATA
                in 0.0..45.0 -> DayStatus.LIMIT_OK
                else -> DayStatus.LIMIT_BREACHED
            }
        }
    }

    val backgroundColor = status.toBackgroundColor()
    val contentAlpha = if (day.isInCurrentMonth) 1f else 0.35f
    val currentDayBorder: Color = if (day.isToday) Color.Blue else Color.LightGray.copy(alpha = 0.3f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, currentDayBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = contentAlpha)
            )
        }
    }
}

private fun buildMonthGrid(
    month: YearMonth
): List<CalendarDayUi> {
    val firstDayOfMonth: LocalDate = month.atDay(1)
    val startOffset: Int = firstDayOfMonth.dayOfWeek.toMondayBasedIndex()

    val gridStartDate: LocalDate = firstDayOfMonth.minusDays(startOffset.toLong())
    val today = LocalDate.now()

    return (0 until 42).map { index ->
        val date = gridStartDate.plusDays(index.toLong())
        CalendarDayUi(
            date = date,
            isInCurrentMonth = date.month == month.month,
            isToday = date.isEqual(today)
        )
    }
}