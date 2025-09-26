package net.lsafer.sundry.compose.table

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal expect fun isHorizontalScrollbarSupported(): Boolean

@Composable
internal expect fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
)
