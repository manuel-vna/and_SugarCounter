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
fun SimpleLine(exampleData: List<GraphData>, darkMode: Int) {

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
            val barWidthPix = 1.dp.toPx()

            val fortyFiveGramLine = (size.height / 100) * 45
            drawLine(
                Color.Red,
                start = Offset(125f, fortyFiveGramLine),
                end = Offset(size.width, fortyFiveGramLine),
                strokeWidth = 2.dp.toPx()
            )

            val path = Path()
            exampleData.forEach { it ->

                //vertical lines
                val verticalSize = size.width / (7 + 1)
                val startX1 = verticalSize * (it.id + 1)
                drawLine(
                    Color.Black,
                    start = Offset(startX1, 0f),
                    end = Offset(startX1, (size.height / 10) * 9),
                    strokeWidth = barWidthPix
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = if (it.id < 7) {
                        it.day
                    } else {
                        ""
                    },
                    style = style,
                    topLeft = Offset(
                        x = startX1 - 50,
                        y = (size.height / 10) * 9
                    )
                )

                // horizontal lines
                var height = getXyChartFloat(it.gramTotal, size.height)
                val startX = verticalSize * (it.id + 1)
                if (it.id == 0) {
                    path.moveTo(startX, height)
                }
                path.lineTo(startX, height)
                drawPath(path = path, color = Color.Blue, style = Stroke(2.dp.toPx()))
            }

            val yDistance = size.height / 10
            val yAxis = listOf<String>(
                "90g",
                "80g",
                "70g",
                "60g",
                "50g",
                "40g",
                "30g",
                "20g",
                "10g",
                "0g"
            )
            var count = 0
            yAxis.forEach { yAxis ->

                drawText(
                    textMeasurer = textMeasurer,
                    text = yAxis,
                    style = style,
                    topLeft = Offset(
                        x = 10.0F,
                        y = (yDistance * count) - 25
                    )
                )
                count++
                if (count < 10) {
                    drawLine(
                        Color.Gray,
                        start = Offset(125F, yDistance * count),
                        end = Offset(size.width, yDistance * count),
                        strokeWidth = barWidthPix
                    )
                }

            }
        }

    }
}


fun getXyChartFloat(gramValue: Int, chartHeight: Float): Float {
    return if (gramValue <= 100) {
        (chartHeight / 100) * (90 - gramValue)

    } else {
        (chartHeight / 100) * (90 - 100) // = maximum of 100g sugar
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
        SimpleLine(exampleData, 0)
    }
}
