/*
 * Copyright (c) 2025, Glassdoor Inc.
 *
 * Licensed under the Glassdoor Inc Hiring Assessment License.
 * You may not use this file except in compliance with the License.
 * You must obtain explicit permission from Glassdoor Inc before sharing or distributing this file.
 * Mention Glassdoor Inc as the source if you use this code in any way.
 */

package com.glassdoor.intern.presentation.di

import com.glassdoor.intern.presentation.MainUiState
import com.glassdoor.intern.presentation.model.HeaderUiModel
import com.glassdoor.intern.presentation.model.ItemUiModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal object MainModule {

    @Provides
    fun provideMainUiState(): MainUiState = MainUiState(
        errorMessage = null,
        header = HeaderUiModel(
            id = 1,
            title = "Synoptical Astrology",
            description = "A horoscope is an astrological chart or diagram representing the positions of celestial bodies at the time of an event.",
            timestamp = "Nov 10, 2021 02:02 AM"
        ),
        isLoading = false,
        items = emptyList()
    )

        /***TODO("Define default values and provide initial state")*/
}
