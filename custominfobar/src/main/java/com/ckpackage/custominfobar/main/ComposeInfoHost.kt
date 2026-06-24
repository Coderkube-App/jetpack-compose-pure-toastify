package com.ckpackage.custominfobar.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ckpackage.custominfobar.R
import com.ckpackage.custominfobar.animation.AnimationType
import com.ckpackage.custominfobar.animation.DefaultAnimationDuration
import com.ckpackage.custominfobar.animation.ExtraDelayForNewInfoBar
import com.ckpackage.custominfobar.animation.getAnimatedOffset
import com.ckpackage.custominfobar.animation.getEnterAnimation
import com.ckpackage.custominfobar.animation.getExitAnimation
import com.ckpackage.custominfobar.main.ComposeInfoBarDirection.Top
import com.ckpackage.custominfobar.main.ComposeInfoBarShapes.roundedBottom
import com.ckpackage.custominfobar.main.ComposeInfoBarShapes.roundedTop
import com.ckpackage.custominfobar.main.ComposeInfoBarState.Hidden
import com.ckpackage.custominfobar.main.ComposeInfoBarState.Visible
import com.ckpackage.custominfobar.main.ComposeInfoDuration.Indefinite
import com.ckpackage.custominfobar.res.Dimens
import com.ckpackage.custominfobar.res.Dimens.DpEighty
import com.ckpackage.custominfobar.res.Dimens.DpMedium
import com.ckpackage.custominfobar.res.Dimens.DpSmall
import com.ckpackage.custominfobar.res.Dimens.DpTwelve
import com.ckpackage.custominfobar.res.Dimens.DpZero
import com.ckpackage.custominfobar.utils.ConnectivityObserver
import com.ckpackage.custominfobar.utils.DirectionalLazyListState
import com.ckpackage.custominfobar.utils.SCROLL_THRESHOLD
import com.ckpackage.custominfobar.utils.ScrollDirection
import com.ckpackage.custominfobar.utils.TextType
import com.ckpackage.custominfobar.utils.getShapeByDirection
import com.ckpackage.custominfobar.utils.rememberDirectionalLazyListState
import com.ckpackage.custominfobar.utils.swipeable
import com.ckpackage.custominfobar.utils.toMillis
import com.ckpackage.custominfobar.utils.toTextType
import com.ckpackage.custominfobar.defaultInfoBars.OfflineInfoBar
import kotlinx.coroutines.delay
import java.util.LinkedList
import java.util.Queue

/**
 * Max num of lines for description and title in [SomposeInfoBar]
 */
private const val DESC_MAX_LINE = 2
private const val TITLE_MAX_LINE = 2

/**
 * Wrapper data class for information that will be displayed in [ComposeInfoBar].
 *
 * @property title
 * @property description
 * @property icon
 */
data class ComposeInfoBarData(
    val title: TextType,
    val description: TextType? = null,
    val icon: ImageVector = Icons.Default.Info
)

/**
 * Enum class that represents the 3 types of duration of [ComposeInfoBar]
 * @property Short 4 seconds
 * @property Long 10 seconds
 * @property Indefinite [ComposeInfoBar] won't be dismissed until dismissed by user.
 */
enum class ComposeInfoDuration {
    Short,
    Long,
    Indefinite
}

/**
 * Enum class that represents the two states of [ComposeInfoBar]
 * - Visible
 * - Hidden
 *
 * @property value Boolean value that is wrapped by this enum class.
 * (true for [Visible] and false for [Hidden])
 */
enum class ComposeInfoBarState(val value: Boolean) {
    Visible(true), Hidden(false)
}

/**
 * Enum class that represents the direction from which the [ComposeInfoBar] will be presented.
 * @property Top
 * @property Bottom
 */
enum class ComposeInfoBarDirection(internal val alignment: Alignment) {
    Top(Alignment.TopCenter), Bottom(Alignment.BottomCenter)
}

/**
 * State of the [ComposeInfoHost], which controls the current [ComposeInfoBar] being shown
 * inside the [ComposeInfoHost].
 *
 * This state is usually [remember]ed and used to provide to a [ComposeInfoHost].
 */
class ComposeInfoHostState {
    internal var previousState = Hidden
    internal var onDismissCallback: (() -> Unit)? = null
    fun setOnInfoBarDismiss(callback: () -> Unit) {
        onDismissCallback = callback
    }

    // TODO: If everything goes fine add one parameter in the class's constructor for the queue
    //  size and then use that here to create a fixed size linked list.
    private var infoBarDataQueue: Queue<ComposeInfoBarData> = LinkedList()
    private var isQueueBeingProcessed = false

    /**
     * [MutableTransitionState] which is used internally by [ComposeInfoHost] to show and hide [ComposeInfoBar]
     */
    internal var visibilityState = MutableTransitionState(Hidden.value)

    private var _direction = mutableStateOf(Top)
    val direction: State<ComposeInfoBarDirection> = _direction

    /**
     * Function that is used in [ComposeInfoHost] internally to set the direction given by the user.
     */
    internal fun setDirection(direction: ComposeInfoBarDirection) {
        this._direction.value = direction
    }

    /**
     * Private backing property for isInfinite.
     */
    internal var _isInfinite = mutableStateOf(false)

    /**
     * A read only [State] property of type [Boolean] which is used to represent whether the current [ComposeInfoBar]'s Duration is Infinite or not.
     */
    val isInfinite: State<Boolean> = _isInfinite

    /**
     * A read only property that represents whether a [ComposeInfoBar] is currently being shown or not.
     */
    val isVisible: Boolean
        get() = visibilityState.currentState == Visible.value

    /**
     * Private backing property for [currentComposeInfoBarData].
     */
    private var _currentComposeInfoBarData: MutableState<ComposeInfoBarData?> =
        mutableStateOf(null)

    /**
     * A read only property that represents the data(title, description and icon) that should be displayed in the [SSComposeInfoBar].
     */
    val currentComposeInfoBarData: State<ComposeInfoBarData?> = _currentComposeInfoBarData

    /**
     * Private backing property for offline [currentComposeInfoBarData].
     */
    private var _offlineInfoBarData: MutableState<ComposeInfoBarData?> = mutableStateOf(null)
    
    /**
     * A read only property that represents the data(title, description and icon) 
     * that should be displayed in the offline [ComposeInfoBar].
     */
    val offlineInfoBarData: State<ComposeInfoBarData?> = _offlineInfoBarData

    fun setOfflineInfoBarData(offlineInfoBarData: ComposeInfoBarData) {
        _offlineInfoBarData.value = offlineInfoBarData
    }

    /**
     * Function that is used to manually hide the currently displayed [ComposeInfoBar].
     */
    fun dismiss() {
        visibilityState.targetState = Hidden.value
        previousState = Visible
    }

    /**
     * Scroll threshold to use for scroll to hide feature.
     */
    private var scrollThreshold = SCROLL_THRESHOLD

    /**
     * setter function for scrollThreshold.
     *
     * @param threshold
     */
    fun setScrollThreshold(threshold: Int) {
        scrollThreshold = threshold
        directionalLazyListState?.updateScrollThreshold(scrollThreshold)
    }

    // For Scroll-to-hide feature
    internal var directionalLazyListState: DirectionalLazyListState? = null

    // Initialize the directionalLazyListState using contentScrollState
    @Composable
    internal fun InitializeDirectionalLazyListState(contentScrollState: LazyListState?) {
        if (contentScrollState != null && directionalLazyListState == null) {
            directionalLazyListState =
                rememberDirectionalLazyListState(
                    lazyListState = contentScrollState,
                    scrollThreshold = scrollThreshold
                )
        }
    }

    /**
     * This suspend function is used to show [ComposeInfoBar] in [ComposeInfoHost].
     *
     * @param infoBarData [ComposeInfoBarData] which will be used to display content(title, description) in [ComposeInfoBar].
     * @param duration [ComposeInfoDuration] which will determine how long the [ComposeInfoBar] stays visible.
     */
    suspend fun show(
        infoBarData: ComposeInfoBarData,
        duration: ComposeInfoDuration
    ) {
        if (duration == Indefinite) {
            if (!isVisible) {
                _currentComposeInfoBarData.value = infoBarData
                _isInfinite.value = true
                visibilityState.targetState = Visible.value
                previousState = Hidden
            }
        } else {
            if (!isInfinite.value) {
                infoBarDataQueue.add(infoBarData)
                if (!isQueueBeingProcessed) {
                    isQueueBeingProcessed = true
                    while (infoBarDataQueue.isNotEmpty()) {
                        _currentComposeInfoBarData.value = infoBarDataQueue.remove()
                        visibilityState.targetState = Visible.value
                        // Wait for the given duration
                        delay(duration.toMillis())
                        visibilityState.targetState = Hidden.value
                        previousState = Visible
                        // Here we are using the default value of exit animation but when we will give custom animations we will have to use that duration.
                        // Store that exit animation in the SSComposeHostState.
                        delay(DefaultAnimationDuration.toLong() + ExtraDelayForNewInfoBar)
                    }
                    isQueueBeingProcessed = false
                }
            }
        }
    }
}

/**
 * Contains the default values used by [ComposeInfoBar].
 */
object ComposeInfoBarDefaults {
    /**
     * Default horizontalPadding and verticalPadding.
     */
    private val ComposeInfoBarHorizontalPadding = DpMedium
    private val ComposeInfoBarVerticalPadding = DpSmall

    /**
     * Default action title
     */
    val defaultActionTitle = "Action"

    /**
     * Default content padding for [ComposeInfoBar].
     */
    val contentPadding = PaddingValues(
        start = ComposeInfoBarHorizontalPadding,
        end = ComposeInfoBarHorizontalPadding,
        top = ComposeInfoBarVerticalPadding,
        bottom = ComposeInfoBarVerticalPadding
    )

    /**
     * Default height of [ComposeInfoBar].
     */
    internal val defaultHeight = DpEighty

    /**
     * Default Max lines for description and title in [ComposeInfoBar].
     */
    internal const val descriptionMaxLine = DESC_MAX_LINE
    internal const val titleMaxLine = TITLE_MAX_LINE

    /**
     * Default [Shape] of [ComposeInfoBar].
     */
    val shape: Shape = roundedBottom

    /**
     * Creates default [ComposeInfoBarColors] for [ComposeInfoBar].
     */
    val colors: ComposeInfoBarColors
        @Composable get() = ComposeInfoBarColors(
            iconColor = MaterialTheme.colorScheme.onPrimary,
            titleColor = MaterialTheme.colorScheme.onPrimary,
            descriptionColor = MaterialTheme.colorScheme.onPrimary,
            dismissIconColor = MaterialTheme.colorScheme.onPrimary,
            actionButtonColors = ButtonDefaults.elevatedButtonColors()
        )

    /**
     * Default [CustomBackground] for [ComposeInfoBar].
     */
    val defaultCustomBackground @Composable get() = MaterialTheme.colorScheme.primary.toCustomBackground()

    /**
     * Creates default [ComposeInfoBarElevation] for [ComposeInfoBar]
     */
    val elevations =
        ComposeInfoBarElevation(tonalElevation = DpSmall, shadowElevation = DpSmall)

    /**
     * Default title style of [ComposeInfoBar].
     */
    val defaultTitleStyle
        @Composable get() = LocalTextStyle.current.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.SpEighteen
        )

    /**
     * Default description style of [ComposeInfoBar].
     */
    val defaultDescriptionStyle
        @Composable get() = LocalTextStyle.current.copy(
            fontWeight = FontWeight.Light,
            fontSize = Dimens.SpFourteen
        )
}

/**
 * Object that contains the shapes for [ComposeInfoBar] depending upon the [ComposeInfoBarDirection].
 *
 * @property roundedBottom Used when the [ComposeInfoBarDirection] is [ComposeInfoBarDirection.Top]
 * @property roundedTop Used when the [ComposeInfoBarDirection] is [ComposeInfoBarDirection.Bottom]
 */
object ComposeInfoBarShapes {
    val roundedBottom =
        RoundedCornerShape(
            topStart = DpZero,
            topEnd = DpZero,
            bottomStart = DpTwelve,
            bottomEnd = DpTwelve
        )
    val roundedTop =
        RoundedCornerShape(
            topStart = DpTwelve,
            topEnd = DpTwelve,
            bottomStart = DpZero,
            bottomEnd = DpZero
        )
}

/**
 * Host for [ComposeInfoBar]s to properly show, hide and dismiss items base on [ComposeInfoHostState].
 *
 * @param modifier The Modifier to be applied to [ComposeInfoHost].
 * @param composeHostState The state of the current [ComposeInfoHost].
 * @param direction The direction from which the [ComposeInfoBar] will be shown.
 * @param animationType The Animation with the [ComposeInfoBar] will be shown.
 * @param contentScrollState The LazyListState which wil be used to show and hide the [ComposeInfoBar] on scrolling.
 * @param enableNetworkMonitoring The flag that will decide whether the network monitoring feature is enabled or not.
 * @param composeInfoBar The [ComposeInfoBar] that will be displayed in [ComposeInfoHost].
 * @param content content of the screen on which the [ComposeInfoBar] will be shown.
 */
@Composable
fun ComposeInfoHost(
    modifier: Modifier = Modifier,
    composeHostState: ComposeInfoHostState,
    direction: ComposeInfoBarDirection = Top,
    animationType: AnimationType = AnimationType.SlideVertically,
    contentScrollState: LazyListState? = null,
    enableNetworkMonitoring: Boolean = false,
    isSwipeToDismissEnabled: Boolean = false,
    composeInfoBar: @Composable (ComposeInfoBarData) -> Unit,
    content: @Composable () -> Unit
) {
    composeHostState.setDirection(direction)
    composeHostState.InitializeDirectionalLazyListState(contentScrollState = contentScrollState)
    val exitAnimation = getExitAnimation(composeHostState.direction.value, animationType)
    val enterAnimation = getEnterAnimation(composeHostState.direction.value, animationType)

    // For dismiss callback
    LaunchedEffect(key1 = composeHostState.isVisible) {
        // Here we are checking whether the info bar was first visible and then it went into dismissed state
        // This will help in when we don't want the callback to be called initially when the infoBar is not visible and the launched is called initially.
        if (composeHostState.previousState == Visible && !composeHostState.isVisible) {
            composeHostState.onDismissCallback?.let { it() }
            if (composeHostState.isInfinite.value) {
                composeHostState._isInfinite.value = false
            }
        }
    }

    // For network monitoring
    val context = LocalContext.current
    val monitor = remember { ConnectivityObserver(context) }
    var isOnline: State<Boolean>? = null
    if (enableNetworkMonitoring) {
        isOnline = monitor.isOnline.collectAsStateWithLifecycle(initialValue = true)
    }
    val shouldBeVisible by remember(composeHostState.isVisible) {
        derivedStateOf {
            if (composeHostState.isVisible) {
                // The scroll to hide behaviour should only be allowed when a InfoBar is currently being shown.
                (composeHostState.directionalLazyListState?.scrollDirection == ScrollDirection.SettledAtTop
                        || composeHostState.directionalLazyListState?.scrollDirection == ScrollDirection.SettleAfterUpScroll)
            } else true
        }
    }
    val animatedYOffset by getAnimatedOffset(
        shouldBeVisible = shouldBeVisible,
        direction = direction
    )
    Box(
        modifier = modifier
    ) {
        content()
        AnimatedVisibility(
            visibleState = composeHostState.visibilityState,
            modifier = Modifier
                .align(composeHostState.direction.value.alignment)
                .then(
                    if (isSwipeToDismissEnabled && composeHostState.isInfinite.value) Modifier.swipeable { composeHostState.dismiss() } else Modifier
                )
                .then(
                    if (composeHostState.directionalLazyListState != null) Modifier
                        .graphicsLayer {
                            translationY = animatedYOffset
                        } else Modifier
                ),
            enter = enterAnimation,
            exit = exitAnimation
        ) {
            composeHostState.currentComposeInfoBarData.value?.let { content ->
                composeInfoBar(content)
            }
        }
        if (enableNetworkMonitoring) {
            isOnline?.value?.let {
                AnimatedVisibility(
                    visible = !it,
                    modifier = Modifier
                        .align(composeHostState.direction.value.alignment),
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    val offlineInfoBarData = composeHostState.offlineInfoBarData.value
                    if (offlineInfoBarData != null) {
                        OfflineInfoBar(offlineData = offlineInfoBarData)
                    } else {
                        OfflineInfoBar(
                            offlineData = ComposeInfoBarData(
                                title = stringResource(R.string.offline_info_bar_title).toTextType(),
                                description = stringResource(R.string.offline_info_bar_description).toTextType()
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Host for [ComposeInfoBar]s to properly show, hide and dismiss items base on [ComposeInfoHostState].
 *
 * Note: This ComposeInfoHost does not provide a way to show custom [ComposeInfoBar]. To provide custom [ComposeInfoBar] checkout other ComposeInfoHost overloads.
 *
 * @param modifier The Modifier to be applied to [ComposeInfoHost].
 * @param composeHostState The state of the current [ComposeInfoHost].
 * @param direction The direction from which the [ComposeInfoBar] will be shown.
 * @param animationType The Animation with the [ComposeInfoBar] will be shown.
 * @param contentScrollState The LazyListState which wil be used to show and hide the [ComposeInfoBar] on scrolling.
 * @param enableNetworkMonitoring The flag that will decide whether the network monitoring feature is enabled or not.
 * @param content content of the screen on which the [ComposeInfoBar] will be shown.
 */
@Composable
fun ComposeInfoHost(
    modifier: Modifier = Modifier,
    composeHostState: ComposeInfoHostState,
    direction: ComposeInfoBarDirection = Top,
    animationType: AnimationType = AnimationType.SlideVertically,
    contentScrollState: LazyListState? = null,
    enableNetworkMonitoring: Boolean = false,
    isSwipeToDismissEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    composeHostState.setDirection(direction)
    composeHostState.InitializeDirectionalLazyListState(contentScrollState = contentScrollState)
    val exitAnimation = getExitAnimation(composeHostState.direction.value, animationType)
    val enterAnimation = getEnterAnimation(composeHostState.direction.value, animationType)

    // For dismiss callback
    LaunchedEffect(key1 = composeHostState.isVisible) {
        // Here we are checking whether the info bar was first visible and then it went into dismissed state
        // This will help in when we don't want the callback to be called initially when the infoBar is not visible and the launched is called initially.
        if (composeHostState.previousState == Visible && !composeHostState.isVisible) {
            composeHostState.onDismissCallback?.let { it() }
            if (composeHostState.isInfinite.value) {
                composeHostState._isInfinite.value = false
            }
        }
    }

    // For network monitoring
    val context = LocalContext.current
    val monitor = remember { ConnectivityObserver(context) }
    var isOnline: State<Boolean>? = null
    if (enableNetworkMonitoring) {
        isOnline = monitor.isOnline.collectAsStateWithLifecycle(initialValue = true)
    }

    val shouldBeVisible by remember(composeHostState.isVisible) {
        derivedStateOf {
            if (composeHostState.isVisible) {
                // The scroll to hide behaviour should only be allowed when an InfoBar is currently being shown.
                (composeHostState.directionalLazyListState?.scrollDirection == ScrollDirection.SettledAtTop
                        || composeHostState.directionalLazyListState?.scrollDirection == ScrollDirection.SettleAfterUpScroll)
            } else true
        }
    }
    val animatedYOffset by getAnimatedOffset(
        shouldBeVisible = shouldBeVisible,
        direction = direction
    )
    Box(
        modifier = modifier
    ) {
        content()
        AnimatedVisibility(
            visibleState = composeHostState.visibilityState,
            modifier = Modifier
                .align(composeHostState.direction.value.alignment)
                .then(
                    if (isSwipeToDismissEnabled && composeHostState.isInfinite.value) Modifier.swipeable { composeHostState.dismiss() } else Modifier
                )
                .then(
                    if (composeHostState.directionalLazyListState != null) Modifier
                        .graphicsLayer {
                            translationY = animatedYOffset
                        } else Modifier
                ),
            enter = enterAnimation,
            exit = exitAnimation
        ) {
            composeHostState.currentComposeInfoBarData.value?.let { infoBarData ->
                ComposeInfoBar(
                    title = infoBarData.title,
                    description = infoBarData.description,
                    shape = getShapeByDirection(composeHostState.direction.value),
                    isInfinite = composeHostState.isInfinite.value,
                    onCloseClicked = {
                        composeHostState.dismiss()
                    }
                )
            }
        }
        if (enableNetworkMonitoring) {
            isOnline?.value?.let {
                AnimatedVisibility(
                    visible = !it,
                    modifier = Modifier
                        .align(composeHostState.direction.value.alignment),
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    val offlineInfoBarData = composeHostState.offlineInfoBarData.value
                    if (offlineInfoBarData != null) {
                        OfflineInfoBar(offlineData = offlineInfoBarData)
                    } else {
                        OfflineInfoBar(
                            offlineData = ComposeInfoBarData(
                                title = stringResource(R.string.offline_info_bar_title).toTextType(),
                                description = stringResource(R.string.offline_info_bar_description).toTextType()
                            )
                        )
                    }
                }
            }
        }
    }
}