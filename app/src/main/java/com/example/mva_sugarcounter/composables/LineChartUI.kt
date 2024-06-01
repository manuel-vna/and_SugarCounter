package com.example.mva_sugarcounter.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mva_sugarcounter.data.GraphData

@Composable
fun LineChart(graphDataList: List<GraphData>, darkMode: Int) {

    var backgroundColor = Color.White
    var drawColor = Color.Black
    var lineGraphColor = Color.Blue
    val thresholdLineColor = Color.Red

    if (darkMode == 33) {
        backgroundColor = Color.Black
        drawColor = Color.White
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

    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .background(backgroundColor)
            .horizontalScroll(scrollState)
    ) {


        Canvas(
            modifier = Modifier
                .aspectRatio(4 / 1f)
                .padding(top = 42.dp, bottom = 42.dp)
        ) {

            //DrawScope variables
            val barWidthPix = 1.dp.toPx()
            val onePercentHeight = size.height / 100
            val onePercentWidth = size.width / 100
            val oneWidthSection = size.width / 35 // width is divided in x sections
            val oneHeightSection =
                size.height / 20 // height is divided in twenty sections (5er steps)
            val fortyFiveGramLineHeight =
                onePercentHeight * 45 //TODO get percentage of users gram threshold from data store
            val path = Path()
            // create array that tags the x axis of the graph with gram values: 0g,10g,...,90g
            val lineGraphYAxisGramTags = (0..18).map { i -> "${(18 - i) * 5}g" }

            drawLine(
                thresholdLineColor,
                start = Offset(oneWidthSection, fortyFiveGramLineHeight),
                end = Offset(size.width, fortyFiveGramLineHeight),
                strokeWidth = barWidthPix * 3
            )

            var yAxisCount = 1
            var previousTotalGramValue = 0
            graphDataList.forEach { it ->

                //vertical lines
                val xAxisPointVerticalLines =
                    oneWidthSection * (yAxisCount) // increase value of x-axis by each loop
                drawLine(
                    drawColor,
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

                // graph lines
                val heightGramDataPoint = getXyChartFloat(it.gramTotal, onePercentHeight)
                val xAxisPointHorizontalLines = oneWidthSection * (yAxisCount)
                if (yAxisCount == 1) {
                    path.moveTo(xAxisPointHorizontalLines, heightGramDataPoint)
                }
                path.lineTo(xAxisPointHorizontalLines, heightGramDataPoint)
                drawPath(path = path, color = lineGraphColor, style = Stroke(2.dp.toPx()))

                drawText(
                    textMeasurer = textMeasurer,
                    text = it.gramTotal.toString(),
                    style = styleSmall,
                    topLeft = Offset(
                        x = xAxisPointHorizontalLines - (onePercentWidth / 2),
                        y = if (previousTotalGramValue < it.gramTotal) {
                            heightGramDataPoint - onePercentHeight * 3
                        } else {
                            heightGramDataPoint
                        }
                    )
                )
                previousTotalGramValue = it.gramTotal


                yAxisCount++
            }

            // horizontal lines
            var horizontalLinesCount = 0
            lineGraphYAxisGramTags.forEach { yAxis ->

                var xAxisCount = 0
                lineGraphYAxisGramTags.forEach { yAxis ->

                    drawText(
                        textMeasurer = textMeasurer,
                        text = yAxis,
                        style = if (xAxisCount % 2 == 0) {
                            styleBig
                        } else {
                            styleSmall
                        },
                        topLeft = Offset(
                            x = (onePercentWidth * 1), // move the gram tags of y axis x% to the left
                            y = ((oneHeightSection * xAxisCount) - (onePercentHeight * 2.5)).toFloat() // move gram tags of y axis 2.5% up
                        )
                    )
                    xAxisCount++
                }

                if (horizontalLinesCount < 19) { // do not draw last horizontal line at the bottom which is the 10th line
                    drawLine(
                        drawColor,
                        start = Offset(oneWidthSection, oneHeightSection * horizontalLinesCount),
                        end = Offset(size.width, oneHeightSection * horizontalLinesCount),
                        strokeWidth = barWidthPix
                    )
                }
                horizontalLinesCount++

            }
        }
    }

}

fun getXyChartFloat(gramValue: Int, onePercentHeight: Float): Float {
    return if (gramValue <= 100) {
        onePercentHeight * (90 - gramValue) // 90 = 90% height line graph, 10% height bottom date line

    } else {
        onePercentHeight * (90 - 100) // = maximum of 100g sugar
    }
}

val exampleData = listOf(
    GraphData(id = 0, gramTotal = 50, "15.06."),
    GraphData(id = 1, gramTotal = 50, "20.06."),
    GraphData(id = 2, gramTotal = 50, "17.06."),
    GraphData(id = 3, gramTotal = 50, "20.06."),
    GraphData(id = 4, gramTotal = 50, "21.06."),
    GraphData(id = 5, gramTotal = 42, "22.06."),
)

@Preview
@Composable
fun GraphPreview() {
    MaterialTheme {
        LineChart(exampleData, 0)
    }
}
