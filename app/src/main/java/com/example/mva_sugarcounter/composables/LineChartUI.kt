package com.example.mva_sugarcounter.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (darkMode == 33) {
                    Color.White
                } else {
                    Color.White
                }
            )
    ) {

        val textMeasurer = rememberTextMeasurer()
        val style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(3 / 3f)
                .padding(16.dp)
        ) {

            //DrawScope variables
            val barWidthPix = 1.dp.toPx()
            val onePercentHeight = size.height / 100
            val onePercentWidth = size.width / 100
            val oneWidthSection = size.width / 8 // width is divided in eight sections
            val oneHeightSection = size.height / 10 // height is divided in ten sections
            val fortyFiveGramLineHeight =
                onePercentHeight * 45 //TODO get percentage of users gram threshold from data store
            val path = Path()
            // create array that tags the x axis of the graph with gram values: 0g,10g,...,90g
            val lineGraphYAxisGramTags = (0..9).map { i -> "${(9 - i) * 10}g" }

            drawLine(
                Color.Red,
                start = Offset(oneWidthSection, fortyFiveGramLineHeight),
                end = Offset(size.width, fortyFiveGramLineHeight),
                strokeWidth = barWidthPix * 2
            )

            graphDataList.forEach { it ->

                //vertical lines
                val xAxisPointVerticalLines =
                    oneWidthSection * (it.id + 1) // increase value of x-axis by each loop
                drawLine(
                    Color.Black,
                    start = Offset(xAxisPointVerticalLines, 0f),
                    end = Offset(
                        xAxisPointVerticalLines,
                        oneHeightSection * 9
                    ), // take 9 HeightSections
                    strokeWidth = barWidthPix
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = if (it.id < 7) { // do not write the 8th date on the screen
                        it.day
                    } else {
                        ""
                    },
                    style = style,
                    topLeft = Offset(
                        x = xAxisPointVerticalLines - (onePercentWidth * 5), //subtract 5% of the x xis point to have the date values centered under the vertical lines
                        y = oneHeightSection * 9 // take 9 height sections
                    )
                )

                // horizontal lines
                var heightGramDataPoint = getXyChartFloat(it.gramTotal, onePercentHeight)
                val xAxisPointHorizontalLines = oneWidthSection * (it.id + 1)
                if (it.id == 0) {
                    path.moveTo(xAxisPointHorizontalLines, heightGramDataPoint)
                }
                path.lineTo(xAxisPointHorizontalLines, heightGramDataPoint)
                drawPath(path = path, color = Color.Blue, style = Stroke(2.dp.toPx()))
            }

            var count = 0
            lineGraphYAxisGramTags.forEach { yAxis ->

                drawText(
                    textMeasurer = textMeasurer,
                    text = yAxis,
                    style = style,
                    topLeft = Offset(
                        x = (onePercentWidth * 2), // move the gram tags of y axis 2% to the left
                        y = ((oneHeightSection * count) - (onePercentHeight * 2.5)).toFloat() // move gram tags of y axis 2.5% up
                    )
                )
                count++
                if (count < 10) { // do not draw last horizontal line at the bottom which is the 10th line
                    drawLine(
                        Color.Gray,
                        start = Offset(oneWidthSection, oneHeightSection * count),
                        end = Offset(size.width, oneHeightSection * count),
                        strokeWidth = barWidthPix
                    )
                }

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
    GraphData(id = 1, gramTotal = 50, "16.06."),
    GraphData(id = 2, gramTotal = 50, "17.06."),
    GraphData(id = 3, gramTotal = 50, "18.06."),
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
