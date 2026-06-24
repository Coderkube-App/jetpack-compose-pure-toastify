package com.ckpackage.custominfobar.defaultInfoBars

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.ckpackage.custominfobar.main.ComposeInfoBar
import com.ckpackage.custominfobar.main.ComposeInfoBarDefaults
import com.ckpackage.custominfobar.utils.TextType

/**
 * Demo Slide to perform action [ComposeInfoBar]
 *
 * @param actionText The text of the Action in the [ComposeInfoBar].
 * @param onActionDoneText The text to be shown in the [ComposeInfoBar] when action is performed.
 * @param modifier  The [modifier] to be applied to the [ComposeInfoBar].
 * @param sliderIcon The [ImageVector] that will be displayed inside of slider.
 * @param backgroundShape The [Shape] of the background of the [ComposeInfoBar].
 * @param sliderShape The [Shape] of the slider.
 * @param onSlideComplete The lambda block to be executed when the sliding is completed.
 */
@Composable
fun SlideToPerformInfoBar(
    actionText: TextType,
    onActionDoneText: TextType,
    modifier: Modifier = Modifier,
    sliderIcon: ImageVector = Icons.Rounded.ChevronRight,
    backgroundShape: Shape = CircleShape,
    sliderShape: Shape = CircleShape,
    onSlideComplete: () -> Unit = {}
) {
    ComposeInfoBar(
        actionText = actionText,
        onActionDoneText = onActionDoneText,
        sliderIcon = sliderIcon,
        modifier = modifier,
        backgroundShape = backgroundShape,
        sliderShape = sliderShape,
        onSlideComplete = onSlideComplete,
        contentPadding = PaddingValues(ComposeInfoBarDefaults.contentPadding.calculateTopPadding())
    )
}