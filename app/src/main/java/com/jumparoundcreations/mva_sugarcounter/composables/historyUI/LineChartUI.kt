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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.GraphData
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun LineChart(
    context: Context,
    countMode: HelperMethods.CountMode,
    graphDataList: List<GraphData>,
    lineGraphYAxisTag: List<String>,
    sharedPrefsMain: SharedPreferences = koinInject(qualifier = named("sharedPrefsMain"))
) {

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
                .padding(top = 28.dp, end = 4.dp)
                .background(backgroundColor)
                .horizontalScroll(scrollState)
        ) {

            Canvas(
                modifier = Modifier
                    .aspectRatio(6 / 1f)
                    .padding(top = 42.dp, bottom = 42.dp)
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

                //Begin: Threshold Line
                // thresholdLineHeight explanation: 90 is subtracted from the users threshold choice since the graph has 9*10 areas.
                // The 10th 10-area is used by x-axis text (Friday, 01.07.2024,...)
                // The subtraction is needed because the 0,0-point is in the top-left of the graph.

                val thresholdLineHeight =
                    onePercentHeight * (90 - sharedPrefsMain.getInt(
                        "gramThresholdValue",
                        50
                    ))
                drawLine(
                    color = thresholdLineColor,
                    start = Offset(oneWidthSection, thresholdLineHeight),
                    end = Offset(oneWidthSection * sizeGraphDataList, thresholdLineHeight),
                    strokeWidth = barWidthPix * 3
                )
                //End: Threshold Line

                //Start: Vertical lines
                var yAxisCount = 1
                var previousTotalValue = 0
                graphDataList.forEach { it ->

                    val xAxisPointVerticalLines =
                        oneWidthSection * (yAxisCount) // increase value of x-axis by each loop
                    drawLine(
                        color = drawColor,
                        start = Offset(xAxisPointVerticalLines, 0f),
                        end = Offset(
                            xAxisPointVerticalLines,
                            oneHeightSection * 18
                        ), // take x HeightSections
                        strokeWidth = barWidthPix
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = it.day,
                        style = styleSmall,
                        topLeft = Offset(
                            x = xAxisPointVerticalLines - (onePercentWidth / 2), //subtract x% of the x axis point to have the date values centered under the vertical lines
                            y = oneHeightSection * 18 // take x height sections
                        )
                    )

                    //Start: Data point lines
                    val heightDataPoint =
                        getXyChartFloat(countMode, it.valueTotal, onePercentHeight)
                    val xAxisPointHorizontalLines = oneWidthSection * (yAxisCount)
                    if (yAxisCount == 1) {
                        path.moveTo(xAxisPointHorizontalLines, heightDataPoint)
                    }
                    path.lineTo(xAxisPointHorizontalLines, heightDataPoint)
                    drawPath(path = path, color = lineGraphColor, style = Stroke(2.dp.toPx()))
                    //End: Data point lines

                    drawText(
                        textMeasurer = textMeasurer,
                        text = it.valueTotal.toString(),
                        style = styleSmall,
                        topLeft = Offset(
                            x = xAxisPointHorizontalLines - (onePercentWidth / 2),
                            y = if (previousTotalValue < it.valueTotal) {
                                heightDataPoint - onePercentHeight * 2
                            } else {
                                heightDataPoint
                            }
                        )
                    )
                    previousTotalValue = it.valueTotal

                    yAxisCount++
                }
                //End: Vertical lines

                //Start: Horizontal Lines
                var horizontalLinesCount = 0
                lineGraphYAxisTag.forEach { _ ->

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

                    if (horizontalLinesCount < 19) { // do not draw last horizontal lines at the bottom which are the 19th and 20th line
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
                    horizontalLinesCount++

                } //End: Horizontal Lines

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


/**
 * Calculates the height of a data point by subtracting the bottom area.
 * Additionally ensures that a value that exceeds the maximum height of the graph is drawn within the graph
 * at the top
 *
 * @param gramValue: Int, onePercentHeight: Float
 * @return heightGramDataPoint: Float
 */
fun getXyChartFloat(
    countMode: HelperMethods.CountMode,
    gramValue: Int,
    onePercentHeight: Float
): Float {

    var maximalValue = 100 // maximal gram value of sugar count
    if (countMode == HelperMethods.CountMode.CALORIES) {
        maximalValue = 3400 // maximal cal value of calories count
    }

    return if (gramValue <= maximalValue) {
        onePercentHeight * (90 - gramValue) // 90 = 90% height line graph, 10% height bottom date line

    } else {
        onePercentHeight * (90 - maximalValue) // = maximum of 100g sugar
    }
}
