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

@Composable
fun SimpleLine(exampleDate: List<Triple<Int, Int, String>>) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
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
            //drawRect(Color.Black, style = Stroke(barWidthPix))

            val fortyFiveGramLine = (size.height / 100) * 55
            drawLine(
                Color.Red,
                start = Offset(0f, fortyFiveGramLine),
                end = Offset(size.width, fortyFiveGramLine),
                strokeWidth = barWidthPix
            )

            val verticalLines = 7
            val verticalSize = size.width / (verticalLines + 1)
            val path = Path()
            repeat(verticalLines) { i ->
                val startX = verticalSize * (i + 1)
                drawLine(
                    Color.Black,
                    start = Offset(startX, 0f),
                    end = Offset(startX, size.height),
                    strokeWidth = barWidthPix
                )
            }

            exampleData.forEach { it ->
                var height = getXyChartFloat(it.second, size.height)
                val startX = verticalSize * (it.first + 1)
                if (it.first == 0) {
                    path.moveTo(startX, height)
                }
                path.lineTo(startX, height)
                drawPath(path = path, color = Color.Blue, style = Stroke(2.dp.toPx()))

                println("it.first: " + it.first)
                println("it.second: " + it.second)
                println("it.third: " + it.third)
                println("size.width" + size.width)

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
                        y = yDistance * count
                    )
                )
                count++
                drawLine(
                    Color.Gray,
                    start = Offset(0F, yDistance * count),
                    end = Offset(size.width, yDistance * count),
                    strokeWidth = barWidthPix
                )

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
    Triple(0, 45, "2024-05-1"),
    Triple(1, 5, "2024-05-2"),
    Triple(2, 40, "2024-05-3"),
    Triple(3, 60, "2024-05-4"),
    Triple(4, 150, "2024-05-5"),
    Triple(5, 100, "2024-05-6"),
    Triple(6, 45, "2024-05-7")
)

@Preview
@Composable
fun GraphPreview() {
    MaterialTheme {
        SimpleLine(exampleData)
    }
}