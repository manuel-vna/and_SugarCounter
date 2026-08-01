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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class DayStatus {
    LIMIT_OK,
    LIMIT_BREACHED,
    SPECIAL,
    NO_DATA
}

data class CalendarDayUi(
    val date: LocalDate,
    val status: DayStatus,
    val isInCurrentMonth: Boolean
)

class EntryCalendarViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryCalendarFeature(
    //viewModel: EntryCalendarViewModel = viewModel()
) {
    val currentMonth = remember { YearMonth.now() }
    val pagerState = rememberPagerState(pageCount = { 12 })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp)
        ) { page ->
            val month = remember(page) { currentMonth.minusMonths(page.toLong()) }
            val days = remember(month) { buildMonthGrid(month) }

            MonthPage(
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(days) { day ->
                DayCell(
                    day = day,
                    onClick = { onDayClick(day) }
                )
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weekdays.forEach { weekday ->
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = weekday,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDayUi,
    onClick: () -> Unit
) {
    val backgroundColor = day.status.toBackgroundColor()
    val contentAlpha = if (day.isInCurrentMonth) 1f else 0.35f

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
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
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

private fun buildMonthGrid(month: YearMonth): List<CalendarDayUi> {
    val firstDayOfMonth = month.atDay(1)
    val startOffset = firstDayOfMonth.dayOfWeek.toMondayBasedIndex()

    val gridStartDate = firstDayOfMonth.minusDays(startOffset.toLong())

    return (0 until 42).map { index ->
        val date = gridStartDate.plusDays(index.toLong())
        CalendarDayUi(
            date = date,
            status = previewStatusFor(date, month),
            isInCurrentMonth = date.month == month.month
        )
    }
}

private fun previewStatusFor(
    date: LocalDate,
    currentMonth: YearMonth
): DayStatus {
    if (date.month != currentMonth.month) return DayStatus.NO_DATA

    return when (date.dayOfMonth % 5) {
        0 -> DayStatus.LIMIT_BREACHED
        1 -> DayStatus.SPECIAL
        2, 3 -> DayStatus.LIMIT_OK
        else -> DayStatus.NO_DATA
    }
}

private fun DayStatus.toBackgroundColor(): Color {
    return when (this) {
        DayStatus.LIMIT_OK -> Color(0xFF81C784)        // green
        DayStatus.LIMIT_BREACHED -> Color(0xFFE57373)  // red
        DayStatus.SPECIAL -> Color(0xFFFFF176)         // yellow
        DayStatus.NO_DATA -> Color(0xFFE0E0E0)         // neutral
    }
}

private fun DayOfWeek.toMondayBasedIndex(): Int {
    return value - 1 // Monday = 0 ... Sunday = 6
}

private fun YearMonth.formatMonthTitle(): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    return atDay(1).format(formatter)
}