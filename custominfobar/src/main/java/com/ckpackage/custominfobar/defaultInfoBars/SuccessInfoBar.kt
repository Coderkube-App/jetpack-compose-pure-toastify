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
import com.ckpackage.custominfobar.res.successGreen
import com.ckpackage.custominfobar.res.white

private val successBackgroundColor = successGreen
private val successContentColor = white

/**
 * Custom [ComposeInfoBar] that is created to represent an success themed ComposeInfoBar.
 *
 * @param modifier The modifier which will be applied to the [SuccessInfoBar].
 * @param successData The [ComposeInfoBarData] that contains the title and description of the success.
 * @param textStyle The [TextStyle] to be applied to all the text in [ComposeInfoBar].
 * @param shape The [Shape] of the [SuccessInfoBar].
 * @param onCloseClicked Called when user clicks on the close icon in [ComposeInfoBar].
 * @param isInfinite The flag that represents whether the duration of the [ComposeInfoBar] is Infinite or not.
 */
@Composable
fun SuccessInfoBar(
    modifier: Modifier = Modifier,
    successData: ComposeInfoBarData,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = ComposeInfoBarDefaults.shape,
    onCloseClicked: () -> Unit = {},
    isInfinite: Boolean = false
) {
    ComposeInfoBar(
        modifier = modifier,
        title = successData.title,
        titleStyle = textStyle,
        description = successData.description,
        shape = shape,
        icon = successData.icon,
        customBackground = successBackgroundColor.toCustomBackground(),
        contentColors = ComposeInfoBarColors(
            iconColor = successContentColor,
            titleColor = successContentColor,
            descriptionColor = successContentColor,
            dismissIconColor = successContentColor
        ),
        onCloseClicked = onCloseClicked,
        isInfinite = isInfinite
    )
}