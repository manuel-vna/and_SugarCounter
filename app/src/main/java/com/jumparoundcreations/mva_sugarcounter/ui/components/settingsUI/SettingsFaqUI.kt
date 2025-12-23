package com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.Faq
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.faqDataList
import com.jumparoundcreations.mva_sugarcounter.ui.components.SharedTopAppBar
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun FAQScreen(navController: NavController) {

    val context = LocalContext.current
    val settingsVM: SettingsVM = koinViewModel()
    val expandedId by settingsVM.faqExpandedId.collectAsState()

    val darkMode = HelperMethods.checkForUIMode(context)
    var fontColor = Color.Black


    if (darkMode == 33) {
        fontColor = Color.White
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SharedTopAppBar(
            appBarTitle = stringResource(R.string.settings_title_faq_text),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        LazyColumn {
            items(items = faqDataList.faqs, key = { it.id }) { faq ->
                FaqItem(
                    isExpanded = expandedId == faq.id,
                    onItemClick = { id ->
                        settingsVM.actionChangeExpandedId(if (id == expandedId) -1L else id)
                    },
                    faq = faq,
                    fontColor
                )
            }
        }
    }
}

@Composable
fun FaqItem(
    isExpanded: Boolean,
    onItemClick: (Long) -> Unit,
    faq: Faq,
    fontColor: Color
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
                color = fontColor
            )
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)

        }
        if (isExpanded) {
            Text(
                text = stringResource(id = faq.answer),
                modifier = Modifier.padding(10.dp),
                color = fontColor
            )
        }

    }

}