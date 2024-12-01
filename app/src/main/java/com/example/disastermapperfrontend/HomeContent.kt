package com.example.disastermapperfrontend

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(
    context: Context,
    currentPage: ApplicationPage,
    innerPadding: PaddingValues,
    showChatState: MutableState<Boolean>,
    changePage: (ApplicationPage) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        when(currentPage) {
            ApplicationPage.Home -> MapScreen(context,
                currentPage = currentPage,
                changePage = changePage
            )
            ApplicationPage.History -> FloodHistoryPage()
        }
    }
}