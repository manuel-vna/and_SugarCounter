package com.jumparoundcreations.mva_sugarcounter.composables.historyUI

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.IEntry
import com.jumparoundcreations.mva_sugarcounter.data.historyData.GraphData
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import org.koin.compose.koinInject

@Composable
fun LineChart(
    context: Context,
    countMode: HelperMethods.CountMode,
    sugarEntryDbHistory: List<EntryGroup<Entry>>,
    caloriesEntryDbHistory: List<EntryGroup<EntryCalories>>,
    sharedPrefsMain: SharedPreferences = koinInject()
) {

    lateinit var graphDataList: List<GraphData>
    lateinit var lineGraphYAxisTag: List<String>

    if (countMode == HelperMethods.CountMode.SUGAR) {
        graphDataList = getGraphDataList(sugarEntryDbHistory)

        // create array that tags the y axis of the graph with gram values: 0g,10g,...,90g = 0 + 18 = 19 gram tags
        lineGraphYAxisTag = (0..18).map { i -> "${(18 - i) * 5}g" }
    } else {
        graphDataList = getGraphDataList(caloriesEntryDbHistory)

        // Calories:
        lineGraphYAxisTag = (0..18).map { i -> "${0 + (18 - i) * (250)}" }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val thresholdLineColor = Color.Red
    val drawColor = MaterialTheme.colorScheme.onSurface
    var lineGraphColor = Color.Blue
    val darkMode = HelperMethods.checkForUIMode(context)
    if (darkMode == 33) {
        lineGraphColor = Color.Magenta
    }
    val scrollState = rememberScrollState()
    val textMeasurer = rememberTextMeasurer()

    val styleBig = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = drawColor,
    )
    val styleSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = drawColor,
    )

    // Draw the XY chart if there is any date given
    if (graphDataList.isNotEmpty()) {
        Box(
            modifier = Modifier
                .padding(top = 32.dp, end = 4.dp)
                .background(backgroundColor)
                .horizontalScroll(scrollState)
        ) {

            Canvas(
                modifier = Modifier
                    .aspectRatio(6 / 1f)
            ) {

                //DrawScope variables
                val barWidthPix = 1.dp.toPx()
                val sizeGraphDataList = graphDataList.count()
                val onePercentHeight = size.height / 100
                val onePercentWidth = size.width / 100
                val oneWidthSection = size.width / 60 // width is divided in x sections
                val oneHeightSection =
                    size.height / 20 // height is divided in 20 sections ( 18 x 5er steps + 2 extra horizontal spaces at the bottom)
                val path = Path()

                drawThresholdLine(
                    sharedPrefsMain = sharedPrefsMain,
                    countMode = countMode,
                    onePercentHeight = onePercentHeight,
                    thresholdLineColor = thresholdLineColor,
                    oneWidthSection = oneWidthSection,
                    sizeGraphDataList = sizeGraphDataList,
                    barWidthPix = barWidthPix
                )

                //Start: Vertical Drawing
                var yAxisCount = 1
                var previousTotalValue = 0
                graphDataList.forEach { it ->

                    // increase value of x-axis by each loop
                    val xAxisPointVerticalLines = oneWidthSection * (yAxisCount)

                    drawVerticalLinesForMatrix(
                        drawColor,
                        xAxisPointVerticalLines,
                        oneHeightSection,
                        barWidthPix
                    )

                    drawEntryDatesUnderVerticalLines(
                        textMeasurer,
                        it,
                        styleSmall,
                        xAxisPointVerticalLines,
                        onePercentWidth,
                        oneHeightSection
                    )

                    val heightDataPoint = getHeightOfDataPoint(
                        countMode,
                        it.valueTotal,
                        onePercentHeight
                    )

                    val xAxisPointHorizontalLines = drawDataPoint(
                        oneWidthSection,
                        yAxisCount,
                        path,
                        lineGraphColor,
                        heightDataPoint
                    )

                    drawTextLabelOfDataPoint(
                        it,
                        textMeasurer,
                        styleSmall,
                        onePercentWidth,
                        previousTotalValue,
                        onePercentHeight,
                        heightDataPoint,
                        xAxisPointHorizontalLines
                    )

                    previousTotalValue = it.valueTotal
                    yAxisCount++
                }
                //END: Vertical Drawing


                //Start: Horizontal Drawing
                var horizontalLinesCount = 0
                lineGraphYAxisTag.forEach { _ ->

                    drawYAxisLabels(
                        lineGraphYAxisTag,
                        onePercentWidth,
                        oneHeightSection,
                        onePercentHeight,
                        textMeasurer,
                        styleBig,
                        styleSmall,
                    )

                    drawHorizontalLinesForMatrix(
                        horizontalLinesCount,
                        drawColor,
                        oneWidthSection,
                        oneHeightSection,
                        sizeGraphDataList,
                        barWidthPix
                    )

                    horizontalLinesCount++


                } //END: Horizontal Drawing

            } // closing Canvas()

        } // closing Box()

    } // closing if-query: graphDataList.isNotEmpty()

    //Show this info text to the user when there is no date yet
    else {
        Text(
            text = stringResource(id = R.string.historyNoDataInfoDescription),
            modifier = Modifier
                .fillMaxSize() // Fill the entire parent space (screen)
                .wrapContentSize(Alignment.Center) // Center the content within the full size
        )
    }

} // closing LineChart() composable method


//START: Methods called from within CANVAS()

/**
 * Draws a line that marks the limit of gram or calories that the user chose
 * 90 is subtracted from the users threshold choice since the graph has 9*10 areas.
 * The subtraction is needed because the 0,0-point is in the top-left of the graph.
 * * The 10th 10-area is used by x-axis text (Friday, 01.07.2024,...)
 * @return Unit
 */
fun DrawScope.drawThresholdLine(
    sharedPrefsMain: SharedPreferences,
    countMode: HelperMethods.CountMode,
    onePercentHeight: Float,
    thresholdLineColor: Color,
    oneWidthSection: Float,
    sizeGraphDataList: Int,
    barWidthPix: Float
) {

    val thresholdLineHeight: Float = if (countMode == HelperMethods.CountMode.SUGAR) {
        onePercentHeight * (90 - sharedPrefsMain.getInt("gramThresholdValue", 50))
    } else {
        // dividing the kcal value by 50 brings ito the the scale of 100
        onePercentHeight * (90 - sharedPrefsMain.getInt("caloriesThresholdValue", 1500) / 50)
    }

    drawLine(
        color = thresholdLineColor,
        start = Offset(oneWidthSection, thresholdLineHeight),
        end = Offset(oneWidthSection * sizeGraphDataList, thresholdLineHeight),
        strokeWidth = barWidthPix * 3
    )
}

/**
 * Draws lines vertically from top to bottom
 * This sets up one half of the coordinate system
 * @return Unit
 */
fun DrawScope.drawVerticalLinesForMatrix(
    drawColor: Color,
    xAxisPointVerticalLines: Float,
    oneHeightSection: Float,
    barWidthPix: Float
) {
    drawLine(
        color = drawColor,
        start = Offset(xAxisPointVerticalLines, 0f),
        end = Offset(
            xAxisPointVerticalLines,
            oneHeightSection * 18
        ), // take x HeightSections
        strokeWidth = barWidthPix
    )
}

/**
 * Draws dates vertically at the bottom
 * @return Unit
 */
fun DrawScope.drawEntryDatesUnderVerticalLines(
    textMeasurer: TextMeasurer,
    graphDataElement: GraphData,
    styleSmall: TextStyle,
    xAxisPointVerticalLines: Float,
    onePercentWidth: Float,
    oneHeightSection: Float
) {
    drawText(
        textMeasurer = textMeasurer,
        text = graphDataElement.day,
        style = styleSmall,
        topLeft = Offset(
            x = xAxisPointVerticalLines - (onePercentWidth / 2), //subtract x% of the x axis point to have the date values centered under the vertical lines
            y = oneHeightSection * 18 // take x height sections
        )
    )
}

/**
 * Calculates the height of a data point by subtracting the bottom area.
 * Additionally ensures that a value that exceeds the maximum height of the graph is drawn within the graph at the top
 * @return heightGramDataPoint: Float
 */
fun getHeightOfDataPoint(
    countMode: HelperMethods.CountMode,
    valueTotal: Int,
    onePercentHeight: Float
): Float {

    val maximalValue: Int
    var valueToSubtractFrom90Percent = 0

    when (countMode) {
        HelperMethods.CountMode.SUGAR -> {
            maximalValue = 100 // gram sugar
            valueToSubtractFrom90Percent = valueTotal
        }

        HelperMethods.CountMode.CALORIES -> {
            maximalValue = 4750 // kilo calories
            valueToSubtractFrom90Percent =
                valueTotal / 50 // dividing the kcal value by 50 brings ito the the scale of 100
            // e.g.: 4500 / 50 = 90 or 500 / 50 = 10
        }
    }
    return if (valueTotal <= maximalValue) {
        onePercentHeight * (90 - valueToSubtractFrom90Percent) // 90 = 90% height line graph, 10% height bottom date line
    } else {
        onePercentHeight * -10 // multiplying one percent of the height with '-10' sets the data point above the top x-axis
        // by having a minus value on the vertical axis
    }
}

/**
 * Draws data points
 * @return Float
 */
fun DrawScope.drawDataPoint(
    oneWidthSection: Float,
    yAxisCount: Int,
    path: Path,
    lineGraphColor: Color,
    heightDataPoint: Float
): Float {
    //Start: Data point lines
    val xAxisPointHorizontalLines = oneWidthSection * (yAxisCount)
    if (yAxisCount == 1) {
        path.moveTo(xAxisPointHorizontalLines, heightDataPoint)
    }
    path.lineTo(xAxisPointHorizontalLines, heightDataPoint)
    drawPath(path = path, color = lineGraphColor, style = Stroke(2.dp.toPx()))
    //End: Data point lines

    return xAxisPointHorizontalLines
}

/**
 * Draws text labels for data points
 * @return Unit
 */
fun DrawScope.drawTextLabelOfDataPoint(
    graphDataElement: GraphData,
    textMeasurer: TextMeasurer,
    styleSmall: TextStyle,
    onePercentWidth: Float,
    previousTotalValue: Int,
    onePercentHeight: Float,
    heightDataPoint: Float,
    xAxisPointHorizontalLines: Float
) {
    drawText(
        textMeasurer = textMeasurer,
        text = graphDataElement.valueTotal.toString(),
        style = styleSmall,
        topLeft = Offset(
            x = xAxisPointHorizontalLines - (onePercentWidth / 2),
            y = if (previousTotalValue < graphDataElement.valueTotal) {
                heightDataPoint - onePercentHeight * 2
            } else {
                heightDataPoint
            }
        )
    )
}

/**
 * Draws text labels for y axis that name the rows
 * @return Unit
 */
fun DrawScope.drawYAxisLabels(
    lineGraphYAxisTag: List<String>,
    onePercentWidth: Float,
    oneHeightSection: Float,
    onePercentHeight: Float,
    textMeasurer: TextMeasurer,
    styleBig: TextStyle,
    styleSmall: TextStyle
) {
    var xAxisCount = 0
    lineGraphYAxisTag.forEach { yAxis ->
        drawText(
            textMeasurer = textMeasurer,
            text = yAxis,
            style = if (xAxisCount % 2 == 0) {
                styleBig
            } else {
                styleSmall
            },
            topLeft = Offset(
                x = (onePercentWidth / 2), // Position tags horizontally in the middle (onePercentWidth / 2) oft the first horizontal section
                y = ((oneHeightSection * xAxisCount) - onePercentHeight) // Position tags to the middle of the horizontal lines
            )
        )
        xAxisCount++
    }
}

/**
 * Draws lines horizontally from left to right
 * This sets up one half of the coordinate system
 * @return Unit
 */
fun DrawScope.drawHorizontalLinesForMatrix(
    horizontalLinesCount: Int,
    drawColor: Color,
    oneWidthSection: Float,
    oneHeightSection: Float,
    sizeGraphDataList: Int,
    barWidthPix: Float
) {
    if (horizontalLinesCount <= 18) { // do not draw last horizontal lines at the bottom which are the 19th and 20th line
        drawLine(
            drawColor,
            start = Offset(
                oneWidthSection,
                oneHeightSection * horizontalLinesCount
            ),
            end = Offset(
                oneWidthSection * sizeGraphDataList,
                oneHeightSection * horizontalLinesCount
            ),
            strokeWidth = barWidthPix
        )
    }
}

//END: Methods called from within CANVAS()


fun <T : IEntry> getGraphDataList(entryList: List<EntryGroup<T>>): List<GraphData> {
    val returnList = entryList.take(60).mapIndexed { id, entryGroup ->
        GraphData(
            id = id,
            valueTotal = HelperMethods.calculateTotalGramPerDayBlock(entryGroup.entryList),
            day = HelperMethods.convertTimestampToDateString(
                entryGroup.entryList.first().currentTimestamp,
                if (HelperMethods.getSystemLanguage() == "en") {
                    "EEEE \n MM/dd"
                } else {
                    "EEEE \n dd.MM"
                }
            ),
            date = entryGroup.date
        )
    }.sortedByDescending { it.date }

    return returnList
}
