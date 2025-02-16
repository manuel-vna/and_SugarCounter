package com.jumparoundcreations.mva_sugarcounter.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Expand
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.navigation.NavItem
import de.sldw.composeonboarding.OnboardingPage
import de.sldw.composeonboarding.PolicyOnboardingPage

class OnboardingPage1(val navController: NavController) : OnboardingPage() {
    @Composable
    override fun Content() {

        val context = LocalContext.current

        Column {

            Text(
                text = "Sugar Counter"
            )

            Text(
                text = "ep eoprjgfoa crojer gfeoirgme rscgfi sergioremgioömsdr grsgeöois"
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { navController.navigate(NavItem.VideoPlayerFullScreen.screenRoute) }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(22.dp),
                        imageVector = Icons.Rounded.Expand,
                        contentDescription = "info",
                    )
                    Text(text = "Expand")
                }
            }


        }

    }
}

class OnboardingPage2 : PolicyOnboardingPage() {
    @Composable
    override fun Content() {
        Text("Hello Gelsenkirchen")
    }
}
