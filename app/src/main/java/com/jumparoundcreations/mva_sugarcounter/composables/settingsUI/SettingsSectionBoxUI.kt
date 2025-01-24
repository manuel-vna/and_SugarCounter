package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingsSectionBoxUI(
    title: String,
    sectionRows: List<Pair<String, () -> Unit>>
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )
    }
    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        sectionRows.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { item.second.invoke() }
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.first,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Copy App Version",
                    modifier = Modifier.size(20.dp)
                )

            }
            if (index != sectionRows.lastIndex) {
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
        }
    }

}
