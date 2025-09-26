package net.lsafer.sundry.compose.table

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

abstract class TableScope {
    /**
     * The width of the table itself.
     */
    abstract val tableOuterWidth: Dp

    /**
     * The width of the inner table, the scrollable content.
     */
    abstract val tableInnerWidth: Dp

    internal abstract val columnWeightList: MutableList<Float>
    internal abstract val columnWidthList: MutableList<Dp>

    internal var rowCursor = 0
    internal var columnCursor = 0
}

/**
 * Data table component supporting horizontal scrolling and
 * both weight-based and manual column width adjustment.
 *
 * @param modifier modifies the root container.
 * @param content specify the [TableRow]s. (arbitrary composable are allowed)
 *
 * @param verticalArrangement The vertical arrangement of the layout's children.
 * @param horizontalAlignment The horizontal alignment of the layout's children.
 *
 * @param horizontalScrollState state of the horizontal scroll.
 * @param flingBehavior logic describing fling behavior when drag has finished with velocity. If
 * `null`, default from [ScrollableDefaults.flingBehavior] will be used.
 * @param reverseScrolling reverse the direction of scrolling, when `true`, 0 [ScrollState.value]
 * will mean right, when `false`, 0 [ScrollState.value] will mean left
 */
@Composable
fun BasicTable(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    horizontalScrollState: ScrollState = rememberScrollState(),
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    content: @Composable TableScope.() -> Unit = {},
) {
    BoxWithConstraints(Modifier.fillMaxWidth().then(modifier)) {
        val tableOuterWidthState = rememberUpdatedState(maxWidth)
        val tableScope = remember {
            object : TableScope() {
                override val columnWeightList = mutableStateListOf<Float>()
                override val columnWidthList = mutableStateListOf<Dp>()
                override val tableInnerWidth by derivedStateOf {
                    maxOf(tableOuterWidth, columnWidthList.fold(0.dp) { a, b -> a + b } + 1.dp)
                }
                override val tableOuterWidth by tableOuterWidthState
            }
        }

        with(tableScope) {
            LaunchedEffect(columnWeightList.toList()) {
                val fr = tableOuterWidth / columnWeightList.sum()
                columnWeightList.forEachIndexed { i, weight ->
                    columnWidthList[i] = fr * weight
                }
            }

            val tableScrollWidth by derivedStateOf {
                val weightSum = columnWeightList.sum()
                tableOuterWidth / weightSum * weightSum + 1.dp
            }

            Column(
                modifier = Modifier
                    .requiredWidth(width = tableScrollWidth)
                    .horizontalScroll(
                        state = horizontalScrollState,
                        flingBehavior = flingBehavior,
                        reverseScrolling = reverseScrolling,
                    ),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                content()
            }
        }
    }
}

/**
 * Data table component supporting horizontal scrolling and
 * both weight-based and manual column width adjustment.
 *
 * This variant is wrapped in an [OutlinedCard] with
 * a [androidx.compose.foundation.HorizontalScrollbar]
 * on the bottom.
 *
 * @param modifier modifies the root container.
 * @param content specify the [TableRow]s. (arbitrary composable are allowed)
 *
 * @param verticalArrangement The vertical arrangement of the layout's children.
 * @param horizontalAlignment The horizontal alignment of the layout's children.
 *
 * @param shape defines the shape of this card's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [CardColors] that will be used to resolve the color(s) used for this card in
 *   different states. See [CardDefaults.outlinedCardColors].
 * @param elevation [CardElevation] used to resolve the elevation for this card in different states.
 *   This controls the size of the shadow below the card. Additionally, when the container color is
 *   [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 *   also: [Surface].
 * @param border the border to draw around the container of this card
 *
 * @param horizontalScrollState state of the horizontal scroll.
 * @param flingBehavior logic describing fling behavior when drag has finished with velocity. If
 * `null`, default from [ScrollableDefaults.flingBehavior] will be used.
 * @param reverseScrolling reverse the direction of scrolling, when `true`, 0 [ScrollState.value]
 * will mean right, when `false`, 0 [ScrollState.value] will mean left
 */
@Composable
fun OutlinedTable(
    modifier: Modifier = Modifier,

    shape: Shape = MaterialTheme.shapes.extraSmall,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),

    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,

    horizontalScrollState: ScrollState = rememberScrollState(),
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,

    content: @Composable TableScope.() -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        BasicTable(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            horizontalScrollState = horizontalScrollState,
            flingBehavior = flingBehavior,
            reverseScrolling = reverseScrolling,
            content = content,
        )

        Spacer(Modifier.fillMaxHeight().weight(1f))

        if (isHorizontalScrollbarSupported()) {
            Box(Modifier.fillMaxWidth().padding(15.dp, 5.dp)) {
                HorizontalScrollbar(
                    scrollState = horizontalScrollState,
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    reverseLayout = reverseScrolling,
                )
            }
        }
    }
}

/**
 * Specify the next table row with [content].
 *
 * @param modifier modifies the entire row.
 * @param content specify the [TableCell]s. (arbitrary composable are allowed)
 *
 * @param horizontalArrangement The horizontal arrangement of the layout's children.
 * @param verticalAlignment The vertical alignment of the layout's children.
 */
@Composable
fun TableScope.TableRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable (iRow: Int) -> Unit = {},
) {
    val iRow = remember { columnCursor = 0; rowCursor++ }

    Row(
        modifier = Modifier.defaultMinSize(minWidth = tableInnerWidth).height(IntrinsicSize.Min).then(modifier),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        content(iRow)
    }
}

/**
 * Specify the next table cell with [content].
 *
 * @param modifier modifies the entire cell.
 * @param columnWeight the width stake the column should take. (affects the whole column)
 * @param alignment the alignment of the cell contents.
 * @param content specify cell contents.
 */
@Composable
fun TableScope.TableCell(
    modifier: Modifier = Modifier,
    columnWeight: Float? = null,
    alignment: Alignment = Alignment.CenterStart,
    content: @Composable BoxScope.(iColumn: Int) -> Unit = {},
) {
    val iColumn = remember { columnCursor++ }

    if (iColumn !in columnWidthList.indices) {
        columnWeightList += 1f
        columnWidthList += 0.dp
    }

    remember(columnWeight) {
        if (columnWeight != null) {
            columnWeightList[iColumn] = columnWeight
        }
    }

    val columnWidth by derivedStateOf { columnWidthList[iColumn] }

    Box(
        modifier = Modifier.width(columnWidth).fillMaxHeight().then(modifier),
        contentAlignment = alignment,
    ) {
        content(iColumn)
    }
}

/**
 * Specify the next table cell with [content].
 *
 * This variant has [ColumnResizeHandle] on its end.
 *
 * @param modifier modifies the entire cell.
 * @param columnWeight the width stake the column should take. (affects the whole column)
 * @param alignment the alignment of the cell contents.
 * @param content specify cell contents.
 */
@Composable
fun TableScope.TableResizableCell(
    modifier: Modifier = Modifier,
    columnWeight: Float? = null,
    alignment: Alignment = Alignment.CenterStart,
    minColumnWidth: Dp = 50.dp,
    maxColumnWidth: Dp = 500.dp,
    content: @Composable BoxScope.(iColumn: Int) -> Unit = {},
) {
    TableCell(modifier, columnWeight) { iColumn ->
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), alignment) {
                content(iColumn)
            }
            ColumnResizeHandle(iColumn, minColumnWidth, maxColumnWidth)
        }
    }
}

/**
 * Specify the next table cells with [content].
 *
 * @param modifier modifies the entire spanned cells
 * @param columnSpan how many columns to occupy.
 * @param alignment the alignment of the cell contents.
 * @param content specify cell contents.
 */
@Composable
fun TableScope.TableSpanCell(
    modifier: Modifier = Modifier,
    columnSpan: Int = 1,
    alignment: Alignment = Alignment.CenterStart,
    content: @Composable BoxScope.(iColumn: Int) -> Unit = {},
) {
    val iColumn = remember {
        columnCursor.also { columnCursor += columnSpan }
    }

    for (jColumn in iColumn..columnSpan) {
        if (jColumn !in columnWidthList.indices) {
            columnWeightList += 1f
            columnWidthList += 0.dp
        }
    }

    val columnCombinedWidth by derivedStateOf {
        columnWidthList.subList(iColumn, iColumn + columnSpan)
            .fold(0.dp) { a, b -> a + b }
    }

    Box(
        Modifier.width(columnCombinedWidth).fillMaxHeight().then(modifier),
        alignment,
    ) {
        content(iColumn)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableScope.ColumnResizeHandle(
    iColumn: Int,
    minColumnWidth: Dp = 50.dp,
    maxColumnWidth: Dp = 500.dp,
    modifier: Modifier = Modifier,
) {
    val direction = LocalLayoutDirection.current
    val density = LocalDensity.current

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        Modifier
            .fillMaxHeight()
            .requiredWidth(10.dp)
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()

                    val columnWidth = columnWidthList[iColumn]
                    val widthChange = with(density) { dragAmount.toDp() }

                    columnWidthList[iColumn] = when (direction) {
                        LayoutDirection.Rtl -> (columnWidth - widthChange)
                            .coerceIn(minColumnWidth, maxColumnWidth)

                        else -> (columnWidth + widthChange)
                            .coerceIn(minColumnWidth, maxColumnWidth)
                    }
                }
            }
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onDoubleClick = {
                    val fr = tableOuterWidth / columnWeightList.sum()
                    val columnWeight = columnWeightList[iColumn]
                    columnWidthList[iColumn] = fr * columnWeight
                },
                onClick = {},
            )
            .drawBehind {
                val ph = size.width / if (isHovered) 2.8f else 2.2f // horizontal padding
                val pv = size.height / if (isHovered) 6f else 5f // vertical padding

                drawRoundRect(
                    color = Color.Gray,
                    topLeft = Offset(ph, pv),
                    size = Size(size.width - ph - ph, size.height - pv - pv),
                    cornerRadius = CornerRadius(16f, 16f),
                )
            }
            .then(modifier)
    )
}

@Composable
fun TablePaging(
    modifier: Modifier = Modifier,
    pageIndex: Long,
    pageCount: Long,
    onPageChangeRequest: (Long) -> Unit,
) {
    Row(modifier) {
        IconButton(onClick = { onPageChangeRequest(1) }, enabled = pageIndex > 1) {
            Icon(Icons.AutoMirrored.Filled.LastPage, null, Modifier.rotate(180f))
        }
        IconButton(onClick = { onPageChangeRequest(pageIndex - 1) }, enabled = pageIndex > 1) {
            Icon(Icons.AutoMirrored.Filled.NavigateBefore, null)
        }
        IconButton(onClick = { onPageChangeRequest(pageIndex + 1) }, enabled = pageIndex < pageCount) {
            Icon(Icons.AutoMirrored.Filled.NavigateNext, null)
        }
        IconButton(onClick = { onPageChangeRequest(pageCount) }, enabled = pageIndex < pageCount) {
            Icon(Icons.AutoMirrored.Filled.LastPage, null)
        }
    }
}
