package com.jumparoundcreations.mva_sugarcounter.util

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R

@Composable
fun VideoPlayerFullScreen(context: Context, navController: NavController) {

    Row {

        IconButton(
            modifier = Modifier
                .weight(1F),
            onClick = { navController.popBackStack() }
        )
        {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.generalClose)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(14F)
        ) {
            VideoPlayer(
                context = context,
                videoResId = R.raw.counter
            )
        }
    }


}