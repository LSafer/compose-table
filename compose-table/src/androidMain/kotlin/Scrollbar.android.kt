package net.lsafer.sundry.compose.table

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal actual fun isHorizontalScrollbarSupported(): Boolean {
    return false
}

@Composable
internal actual fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    reverseLayout: Boolean,
) {
    // TODO HorizontalScrollbar is not supported in android
}
