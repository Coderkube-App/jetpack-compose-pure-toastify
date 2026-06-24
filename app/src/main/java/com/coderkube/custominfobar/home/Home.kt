package com.coderkube.custominfobar.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ckpackage.custominfobar.animation.AnimationType
import com.ckpackage.custominfobar.main.ComposeInfoBarDirection
import com.ckpackage.custominfobar.main.ComposeInfoBarShapes
import com.ckpackage.custominfobar.main.ComposeInfoDuration
import com.ckpackage.custominfobar.main.ComposeInfoHost
import com.ckpackage.custominfobar.main.ComposeInfoHostState
import com.coderkube.custominfobar.R
import com.coderkube.custominfobar.home.SettingBottomSheet
import com.coderkube.custominfobar.ui.theme.Purple40
import com.coderkube.custominfobar.utils.AppDimens
import com.coderkube.custominfobar.utils.ButtonType
import com.coderkube.custominfobar.utils.InfoBarByButtonType
import com.coderkube.custominfobar.utils.getButtonTitle
import com.coderkube.custominfobar.utils.getInfoBarDescription
import com.coderkube.custominfobar.utils.getInfoBarTitle
import com.coderkube.custominfobar.utils.showComposeInfoBar
import java.util.LinkedList
import java.util.Queue

/**
 * Custom Button used in home screen of the demo.
 *
 * @param title Text in the button.
 * @param buttonType [ButtonType] of the button.
 * @param onClick Called when user clicks on this button.
 */
@Composable
private fun CustomHomeButton(
    title: String,
    buttonType: ButtonType,
    onClick: (ButtonType) -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(contentColor = Color.White, containerColor = Purple40),
        onClick = {
            onClick(buttonType)
        }) {
        Text(text = title)
    }
}

/**
 * Custom Action bar to display app title and a setting icon.
 *
 * @param modifier The [Modifier] that is applied to this Custom action bar.
 * @param onSettingClicked Called when the user clicks on the settings icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActionBar(modifier: Modifier = Modifier, onSettingClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.compose_info_bar_demo),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier,
        actions = {
            IconButton(onClick = onSettingClicked) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.info_bar_settings),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple40,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * Home Screen for the demonstration of the [ComposeInfoBar] Library.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInfoBarHome() {
    val context = LocalContext.current
    val composeInfoHostState by remember {
        mutableStateOf(ComposeInfoHostState())
    }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var shouldShowSettingSheet by remember {
        mutableStateOf(false)
    }
    var duration by remember {
        mutableStateOf(ComposeInfoDuration.Short)
    }
    var direction by remember {
        mutableStateOf((ComposeInfoBarDirection.Top))
    }
    var animationType by remember {
        mutableStateOf((AnimationType.SlideVertically))
    }
    val btnTypeQueue: Queue<ButtonType> = remember {
        LinkedList()
    }
    var isSwipeToDismissEnabled by remember {
        mutableStateOf(false)
    }
    var isNetworkMonitoringEnabled by remember {
        mutableStateOf(true)
    }
    composeInfoHostState.setOnInfoBarDismiss {
        btnTypeQueue.remove()
        Toast.makeText(
            context,
            context.getString(R.string.info_bar_dismissed_successfully),
            Toast.LENGTH_SHORT
        ).show()
    }
    val sheetState = rememberModalBottomSheetState(true)
    Scaffold(
        modifier = Modifier,
        topBar = {
            HomeActionBar(
                modifier = Modifier
            ) {
                shouldShowSettingSheet = shouldShowSettingSheet.not()
            }
        }
    ) {
        ComposeInfoHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            composeHostState = composeInfoHostState,
            direction = direction,
            animationType = animationType,
            contentScrollState = lazyListState,
            enableNetworkMonitoring = isNetworkMonitoringEnabled,
            isSwipeToDismissEnabled = isSwipeToDismissEnabled,
            composeInfoBar = { content ->
                val currentBtnType = btnTypeQueue.element()
                InfoBarByButtonType(
                    type = currentBtnType,
                    content = content,
                    isInfinite = composeInfoHostState.isInfinite.value,
                    shape = if (composeInfoHostState.direction.value == ComposeInfoBarDirection.Top) ComposeInfoBarShapes.roundedBottom else ComposeInfoBarShapes.roundedTop,
                    onClose = {
                        composeInfoHostState.dismiss()
                    }
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimens.DpMedium),
                state = lazyListState,
                contentPadding = PaddingValues(vertical = AppDimens.DpMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.DpMedium)
            ) {
                items(ButtonType.entries) { bType ->
                    CustomHomeButton(
                        title = stringResource(id = getButtonTitle(bType)),
                        buttonType = bType
                    ) { btnType ->
                        val title = getInfoBarTitle(context, btnType)
                        val desc = getInfoBarDescription(context, btnType)

                        // NOTE: This if and else logic is only need for demo purposes as we are using multiple themes for infobar with a queue.
                        if (duration != ComposeInfoDuration.Indefinite) {
                            btnTypeQueue.add(btnType)
                        } else if (btnTypeQueue.isEmpty()) {
                            // If the duration is infinite then only add the btnType in the queue for the very first time and not again.
                            btnTypeQueue.add(btnType)
                        }
                        coroutineScope.showComposeInfoBar(
                            title = title,
                            description = desc,
                            composeInfoHostState = composeInfoHostState,
                            duration = duration
                        )
                    }
                }
                // For temporary usage will be removed in future.
                // add these invisible boxes to make the current list scrollable
                // in order to demonstrate scroll to show and hide feature
                if (composeInfoHostState.isVisible) {
                    items(20) {
                        Box(modifier = Modifier.height(24.dp))
                    }
                }
            }
            if (shouldShowSettingSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        shouldShowSettingSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // Sheet content
                    SettingBottomSheet(
                        inputDuration = duration,
                        inputDirection = direction,
                        inputAnimationType = animationType,
                        inputSwipeToDismissState = isSwipeToDismissEnabled,
                        inputNetworkObserverState = isNetworkMonitoringEnabled,
                        onCancel = { shouldShowSettingSheet = false },
                        onConfirm = { selectedDuration, selectedDirection, selectedAnimation,
                                      swipeToDismissState, networkObserverState ->
                            duration = selectedDuration
                            direction = selectedDirection
                            animationType = selectedAnimation
                            isSwipeToDismissEnabled = swipeToDismissState
                            isNetworkMonitoringEnabled = networkObserverState
                            shouldShowSettingSheet = false
                        }
                    )
                }
            }
        }
    }
}