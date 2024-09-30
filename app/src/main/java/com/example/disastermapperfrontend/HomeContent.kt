package com.example.disastermapperfrontend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeContent(currentPage: ApplicationPage, innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        when(currentPage) {
            ApplicationPage.Home -> MapScreen()
            ApplicationPage.Profile -> Text(text = "Profile")

        }
    }
}