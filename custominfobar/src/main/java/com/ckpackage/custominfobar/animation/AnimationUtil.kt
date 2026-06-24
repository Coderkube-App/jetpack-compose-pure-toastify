package com.ckpackage.custominfobar.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.ckpackage.custominfobar.R
import androidx.compose.ui.Alignment
import com.ckpackage.custominfobar.main.ComposeInfoBarDefaults
import com.ckpackage.custominfobar.main.ComposeInfoBarDirection

internal const val DefaultHidingAnimationDuration = 300
internal const val DefaultAnimationDuration = 300
internal const val ExtraDelayForNewInfoBar = 300L

enum class AnimationType {
    SlideVertically,
    SlideHorizontally,
    Fade,
    Scale,
    ScaleVertically,
    ExpandShrinkVertically
}

/**
 * Internal function to get enter animation of compose info bar based on [ComposeInfoBarDirection] provided
 *
 * Example: If the animation direction is from top to bottom then we will get slideInVertically with initial offset from top
 * and if the animation direction is from bottom to top then the initial offset will be set to outside of bottom of the screen
 * @param direction direction from which [ComposeInfoBar] comes in and exits.
 * @param animationType [AnimationType] the type of animation.
 * @param duration duration of animation in milliseconds
 */
internal fun getEnterAnimation(
    direction: ComposeInfoBarDirection,
    animationType: AnimationType,
    duration: Int = DefaultAnimationDuration
): EnterTransition {
    when (animationType) {
        AnimationType.SlideVertically -> return slideInVertically(
            tween(duration),
            initialOffsetY = { height ->
                if (direction == ComposeInfoBarDirection.Top) {
                    -height
                } else {
                    height
                }
            })

        AnimationType.SlideHorizontally -> return slideInHorizontally(
            tween(duration),
            initialOffsetX = { width ->
                -width
            }
        )

        AnimationType.Fade -> return fadeIn(
            tween(duration)
        )

        AnimationType.Scale -> return scaleIn(
            tween(duration)
        )

        AnimationType.ScaleVertically -> return (scaleIn(
            tween(duration)
        ) + slideInVertically(tween(duration),
            initialOffsetY = { height ->
                if (direction == ComposeInfoBarDirection.Top) {
                    -height
                } else {
                    height
                }
            }))

        AnimationType.ExpandShrinkVertically -> return expandVertically(
            tween(duration),
            expandFrom = if (direction == ComposeInfoBarDirection.Top) Alignment.Top else Alignment.Bottom
        )
    }
}

/**
 * Internal function to get exit animation of compose info bar based on [ComposeInfoBarDirection] provided
 *
 * Example: If the animation direction is from top then we will get slideInVertically with target offset from top
 * and if the animation direction is from bottom then the target offset will be set to outside of bottom of the screen
 * @param direction direction from which [ComposeInfoBar] comes in and exits.
 * @param animationType [AnimationType] the type of animation.
 * @param duration duration of animation in milliseconds
 */
internal fun getExitAnimation(
    direction: ComposeInfoBarDirection,
    animationType: AnimationType,
    duration: Int = DefaultAnimationDuration
): ExitTransition {
    when (animationType) {
        AnimationType.SlideVertically -> return slideOutVertically(
            tween(duration),
            targetOffsetY = { height ->
                if (direction == ComposeInfoBarDirection.Top) {
                    -height
                } else {
                    height
                }
            })

        AnimationType.SlideHorizontally -> return slideOutHorizontally(
            tween(duration),
            targetOffsetX = { width ->
                -width
            }
        )

        AnimationType.Fade -> return fadeOut(
            tween(duration)
        )

        AnimationType.Scale -> return scaleOut(
            tween(duration)
        )

        AnimationType.ScaleVertically -> return (scaleOut(
            tween(duration)
        ) + slideOutVertically(
            tween(duration),
            targetOffsetY = { height ->
                if (direction == ComposeInfoBarDirection.Top) {
                    -height
                } else {
                    height
                }
            }))

        AnimationType.ExpandShrinkVertically -> return shrinkVertically(
            tween(duration),
            shrinkTowards = if (direction == ComposeInfoBarDirection.Top) Alignment.Top else Alignment.Bottom
        )
    }
}

/**
 * An animation utility method that returns animated yOffset for [ComposeInfoBar] when
 * scroll to hide and show is enabled.
 *
 * @param shouldBeVisible flag that denoted whether the [ComposeInfoBar] should be visible or not.
 * @param direction [ComposeInfoBarDirection] to show and hide [ComposeInfoBar] correctly.
 * @param duration Animation duration in milliseconds, default is set to 300 ms.
 * @return [State] object of type float that can be observed.
 */
@Composable
fun getAnimatedOffset(
    shouldBeVisible: Boolean,
    direction: ComposeInfoBarDirection,
    duration: Int = DefaultHidingAnimationDuration
): State<Float> {
    val density = LocalDensity.current
    val shownOffset = 0f
    val hiddenOffset =
        if (direction == ComposeInfoBarDirection.Top) with(density) { -ComposeInfoBarDefaults.defaultHeight.toPx() }
        else with(density) { ComposeInfoBarDefaults.defaultHeight.toPx() }
    return animateFloatAsState(
        targetValue = if (shouldBeVisible) shownOffset else hiddenOffset,
        animationSpec = tween(duration),
        label = stringResource(R.string.scroll_to_hide_animation)
    )
}