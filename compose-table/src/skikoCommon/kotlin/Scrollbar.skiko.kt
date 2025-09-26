package net.lsafer.sundry.compose.table

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal actual fun isHorizontalScrollbarSupported(): Boolean {
    return true
}

@Composable
internal actual fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    reverseLayout: Boolean,
) {
    Box(Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 15.dp)) {
        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = modifier,
            reverseLayout = reverseLayout,
        )
    }
}
