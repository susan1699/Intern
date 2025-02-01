/*
 * Copyright (c) 2025, Glassdoor Inc.
 *
 * Licensed under the Glassdoor Inc Hiring Assessment License.
 * You may not use this file except in compliance with the License.
 * You must obtain explicit permission from Glassdoor Inc before sharing or distributing this file.
 * Mention Glassdoor Inc as the source if you use this code in any way.
 */

package com.glassdoor.intern.presentation

import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.glassdoor.intern.domain.usecase.GetHeaderInfoUseCase
import com.glassdoor.intern.presentation.MainIntent.HideErrorMessage
import com.glassdoor.intern.presentation.MainIntent.RefreshScreen
import com.glassdoor.intern.presentation.MainUiState.PartialState
import com.glassdoor.intern.presentation.MainUiState.PartialState.HideLoadingState
import com.glassdoor.intern.presentation.MainUiState.PartialState.ShowLoadingState
import com.glassdoor.intern.presentation.MainUiState.PartialState.UpdateErrorMessageState
import com.glassdoor.intern.presentation.MainUiState.PartialState.UpdateHeaderState
import com.glassdoor.intern.presentation.MainUiState.PartialState.UpdateItemsState
import com.glassdoor.intern.presentation.mapper.HeaderUiModelMapper
import com.glassdoor.intern.presentation.mapper.ItemUiModelMapper
import com.glassdoor.intern.utils.presentation.UiStateMachine
import com.glassdoor.intern.utils.presentation.UiStateMachineFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

internal interface IMainViewModel : UiStateMachine<MainUiState, PartialState, MainIntent>

/**
 * TODO: Inject the correct header mapper dependency
 */
@HiltViewModel
internal class MainViewModel @Inject constructor(
    defaultUiState: MainUiState,
    uiStateMachineFactory: UiStateMachineFactory,
    private val getHeaderInfoUseCase: GetHeaderInfoUseCase,
    private val itemUiModelMapper: ItemUiModelMapper,
    private val headerUiModelMapper: HeaderUiModelMapper,
) : ViewModel(), IMainViewModel {

    /**
     * TODO: Define the correct methods as callbacks
     */
    private val uiStateMachine: UiStateMachine<MainUiState, PartialState, MainIntent> =
        uiStateMachineFactory.create(
            defaultUiState = defaultUiState,
            errorTransform = ::errorTransform,
            intentTransform = ::intentTransform,
            updateUiState = ::updateUiState,
        )

    override val uiState: StateFlow<MainUiState> = uiStateMachine.uiState

    init {
        /**
         * TODO: Refresh the screen only when the header is empty
         */
        if (uiState.value.header.isEmpty) {
            acceptIntent(RefreshScreen)
        }
    }

    /**
     * TODO: Delegate method to [uiStateMachine]
     */
    override fun acceptIntent(intent: MainIntent){
        uiStateMachine.acceptIntent(intent)
    }

    private fun errorTransform(throwable: Throwable): Flow<PartialState> = flow {
        Timber.e(throwable, "MainViewModel")

        emit(HideLoadingState)

        emit(UpdateItemsState(emptyList()))

        emit(UpdateErrorMessageState(errorMessage = throwable.message))
    }

    private fun intentTransform(intent: MainIntent): Flow<PartialState> = when (intent) {
        HideErrorMessage -> onHideErrorMessage()
        RefreshScreen -> onRefreshScreen()
    }

    private fun updateUiState(
        previousUiState: MainUiState,
        partialState: PartialState,
    ): MainUiState = when (partialState) {

            /**
             * TODO: Separate handling and update correct properties [previousUiState]
             */
        HideLoadingState -> previousUiState.copy(isLoading = false)

        ShowLoadingState -> previousUiState.copy(isLoading = true)

        is UpdateErrorMessageState -> with(partialState) {
            previousUiState.copy(
                errorMessage = errorMessage,
                items = /*if (errorMessage.isNullOrEmpty())*/ previousUiState.items //else emptyList(),
            )
        }

        is UpdateHeaderState -> {
            previousUiState.copy(header = partialState.header)
        }

        is UpdateItemsState -> {
            previousUiState.copy(items = partialState.items)
        }
    }

    private fun onHideErrorMessage(): Flow<PartialState> =
        flowOf(UpdateErrorMessageState(errorMessage = null))

    private fun onRefreshScreen(): Flow<PartialState> = flow {
        emit(ShowLoadingState)
        Timber.d("🔄 Fetching data from API...")
       try{
           getHeaderInfoUseCase()
               .onSuccess { headerInfo ->
                   /**
                    * TODO: Transform the header domain model to the UI model
                    * TODO: Emit the transformed UI model as state
                    */
                   emit(UpdateHeaderState(headerUiModelMapper.toUiModel(headerInfo)))
                   emit(UpdateItemsState(headerInfo.items.map(itemUiModelMapper::toUiModel)))
               }
               .onFailure { throwable ->
                   Timber.e(throwable, "API Error - Failed to Load Data")
                   emit(UpdateErrorMessageState(errorMessage = throwable.message))
               }
       }catch (e: Exception){
           Timber.e(e, "Uncaught Exception - Handling in ViewModel")
           emit(UpdateErrorMessageState(errorMessage = "Unexpected error occurred. Please try again."))
       }
       finally{
        emit(HideLoadingState)
       }

    }
}
