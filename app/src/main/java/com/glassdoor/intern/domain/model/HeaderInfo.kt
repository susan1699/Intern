/*
 * Copyright (c) 2025, Glassdoor Inc.
 *
 * Licensed under the Glassdoor Inc Hiring Assessment License.
 * You may not use this file except in compliance with the License.
 * You must obtain explicit permission from Glassdoor Inc before sharing or distributing this file.
 * Mention Glassdoor Inc as the source if you use this code in any way.
 */

package com.glassdoor.intern.domain.model

/**
 * DONE: Determine undefined model properties
 */
data class HeaderInfo(
    val id: Long,
    val title: String,
    val description: String,
    val timestamp: String,
    val items: List<ItemInfo>,
)
