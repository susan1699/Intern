/*
 * Copyright (c) 2025, Glassdoor Inc.
 *
 * Licensed under the Glassdoor Inc Hiring Assessment License.
 * You may not use this file except in compliance with the License.
 * You must obtain explicit permission from Glassdoor Inc before sharing or distributing this file.
 * Mention Glassdoor Inc as the source if you use this code in any way.
 */

package com.glassdoor.intern.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * TODO: Define the structure of the DTO model based on the server response
 */
@JsonClass(generateAdapter = true)
internal class HeaderInfoDto(
    @Json(name = "title")
    val title: String,
    @Json(name = "subtitle")
    val subtitle: String,
    @Json(name = "lastUpdated")
    val lastUpdated: Long,
)

