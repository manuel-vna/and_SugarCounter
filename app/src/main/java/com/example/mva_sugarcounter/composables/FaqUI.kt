package com.example.mva_sugarcounter.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.data.Faq
import com.example.mva_sugarcounter.data.faqDataList
import com.example.mva_sugarcounter.viewModels.SettingsVM

@Composable
fun FAQScreen() {

    val settingsVM: SettingsVM = viewModel()
    val expandedId by settingsVM.faqSingleSelectMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        LazyColumn {
            items(items = faqDataList.faqs, key = { it.id }) { faq ->
                FaqItem(
                    isExpanded = expandedId == faq.id,
                    onItemClick = { id ->
                        settingsVM.actionChangeExpandedId(if (id == expandedId) -1L else id)
                    },
                    faq = faq
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqItem(
    isExpanded: Boolean,
    onItemClick: (Long) -> Unit,
    faq: Faq
) {

    ElevatedCard(
        modifier = Modifier.padding(vertical = 5.dp),
        colors = CardDefaults.elevatedCardColors(contentColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { onItemClick(faq.id) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(id = faq.question),
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)

        }
        if (isExpanded) {
            Text(
                text = stringResource(id = faq.answer),
                modifier = Modifier.padding(10.dp)
            )
        }

    }

}