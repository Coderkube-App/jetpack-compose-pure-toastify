package com.ckpackage.custominfobar.defaultInfoBars

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import com.ckpackage.custominfobar.main.ComposeInfoBar
import com.ckpackage.custominfobar.main.ComposeInfoBarColors
import com.ckpackage.custominfobar.main.ComposeInfoBarData
import com.ckpackage.custominfobar.main.ComposeInfoBarDefaults
import com.ckpackage.custominfobar.main.toCustomBackground
import com.ckpackage.custominfobar.res.warningOrange
import com.ckpackage.custominfobar.res.white

private val warningBackgroundColor = warningOrange
private val warningContentColor = white

/**
 * Custom [ComposeInfoBar] that is created to represent an error themed ComposeInfoBar.
 *
 * @param modifier The modifier which will be applied to the [WarningInfoBar].
 * @param warningData The [ComposeInfoBarData] that contains the title and description of the warning.
 * @param textStyle The [TextStyle] to be applied to all the text in [ComposeInfoBar].
 * @param shape The [Shape] of the [WarningInfoBar].
 * @param onCloseClicked Called when user clicks on the close icon in [ComposeInfoBar].
 * @param isInfinite The flag that represents whether the duration of the [ComposeInfoBar] is Infinite or not.
 */
@Composable
fun WarningInfoBar(
    modifier: Modifier = Modifier,
    warningData: ComposeInfoBarData,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = ComposeInfoBarDefaults.shape,
    onCloseClicked: () -> Unit = {},
    isInfinite: Boolean = false
) {
    ComposeInfoBar(
        modifier = modifier,
        title = warningData.title,
        titleStyle = textStyle,
        description = warningData.description,
        shape = shape,
        icon = warningData.icon,
        customBackground = warningBackgroundColor.toCustomBackground(),
        contentColors = ComposeInfoBarColors(
            iconColor = warningContentColor,
            titleColor = warningContentColor,
            descriptionColor = warningContentColor,
            dismissIconColor = warningContentColor
        ),
        onCloseClicked = onCloseClicked,
        isInfinite = isInfinite
    )
}