package com.ckpackage.custominfobar.utils

import com.ckpackage.custominfobar.main.ComposeInfoBar
import com.ckpackage.custominfobar.main.ComposeInfoBarDirection
import com.ckpackage.custominfobar.main.ComposeInfoBarShapes
import com.ckpackage.custominfobar.main.ComposeInfoDuration

/**
 * Duration in milliseconds
 */
private const val DURATION_LONG = 10000L
private const val DURATION_SHORT = 4000L

/**
 * Internal function to create shape for [ComposeInfoBar] based on the [ComposeInfoBarDirection] provided.
 *
 * @param direction The [ComposeInfoBarDirection] type provided by user.
 */
internal fun getShapeByDirection(direction: ComposeInfoBarDirection) = when (direction) {
    ComposeInfoBarDirection.Top -> ComposeInfoBarShapes.roundedBottom
    ComposeInfoBarDirection.Bottom -> ComposeInfoBarShapes.roundedTop
}

/**
 * Internal util function to get Duration of [ComposeInfoBar] based on [ComposeInfoDuration].
 *
 * @return Durations in milli Seconds.
 */
internal fun ComposeInfoDuration.toMillis(): Long {
    val original = when (this) {
        ComposeInfoDuration.Indefinite -> Long.MAX_VALUE
        ComposeInfoDuration.Long -> DURATION_LONG
        ComposeInfoDuration.Short -> DURATION_SHORT
    }
    return original
}