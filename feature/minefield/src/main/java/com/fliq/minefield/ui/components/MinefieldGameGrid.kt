package com.fliq.minefield.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.fliq.game_engine.models.Tile

@Composable
fun MinefieldGameGrid(
    tiles: List<Tile>,
    onTileTapped: (Int, Offset?) -> Unit,
    onTilePositioned: (Int, Offset, Size) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 4
    val rows = (tiles.size + columns - 1) / columns

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        for (i in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    if (index < tiles.size) {
                        val tile = tiles[index]
                        var tileCenter by remember { mutableStateOf(Offset.Zero) }
                        MinefieldTile(
                            tile = tile,
                            onClick = { onTileTapped(tile.id, tileCenter) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .onGloballyPositioned { coords ->
                                    val center = Offset(
                                        coords.positionInRoot().x + coords.size.width / 2,
                                        coords.positionInRoot().y + coords.size.height / 2
                                    )
                                    tileCenter = center
                                    onTilePositioned(
                                        tile.id,
                                        center,
                                        Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                                    )
                                }
                        )
                    } else Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
