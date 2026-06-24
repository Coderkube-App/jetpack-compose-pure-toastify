package com.ckpackage.custominfobar.defaultInfoBars

import androidx.compose.material3.ButtonDefaults
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
import com.ckpackage.custominfobar.res.errorRed
import com.ckpackage.custominfobar.res.white

private val errorBackgroundColor = errorRed
private val errorContentColor = white

/**
 * Custom [ComposeInfoBar] that is created to represent an error themed ComposeInfoBar.
 *
 * @param modifier The modifier which will be applied to the [ErrorInfoBar].
 * @param errorData The [ComposeInfoBarData] that contains the title and description of the error.
 * @param textStyle The [TextStyle] to be applied to all the text in [ComposeInfoBar].
 * @param shape The [Shape] of the [ErrorInfoBar].
 * @param onCloseClicked Called when user clicks on the close icon in [ComposeInfoBar].
 * @param isInfinite The flag that represents whether the duration of the [ComposeInfoBar] is Infinite or not.
 */
@Composable
fun ErrorInfoBar(
    modifier: Modifier = Modifier,
    errorData: ComposeInfoBarData,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = ComposeInfoBarDefaults.shape,
    onCloseClicked: () -> Unit = {},
    isInfinite: Boolean = false
) {
    ComposeInfoBar(
        modifier = modifier,
        title = errorData.title,
        titleStyle = textStyle,
        description = errorData.description,
        shape = shape,
        icon = errorData.icon,
        customBackground = errorBackgroundColor.toCustomBackground(),
        contentColors = ComposeInfoBarColors(
            iconColor = errorContentColor,
            titleColor = errorContentColor,
            descriptionColor = errorContentColor,
            dismissIconColor = errorContentColor,
            actionButtonColors = ButtonDefaults.elevatedButtonColors()
        ),
        onCloseClicked = onCloseClicked,
        isInfinite = isInfinite
    )
}