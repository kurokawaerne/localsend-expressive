package dev.kurokawa.localsend.expressive.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * The newest official MD3 Expressive components, isolated in one file
 * (v0.6.0): if a future material3 bump renames an API, this is the only
 * file that changes. Ernest's rule: official components first, custom
 * only where Google ships nothing.
 */

/** One entry of the connection fan (FAB menu). */
data class FanAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

/**
 * The FAB menu "fan" for connection setup, anchored by the scaffold at
 * the bottom end above the navigation pill. Official component:
 * FloatingActionButtonMenu + ToggleFloatingActionButton (MD3 Expressive).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectionFan(
    actions: List<FanAction>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    FloatingActionButtonMenu(
        expanded = expanded,
        modifier = modifier,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = { expanded = it },
            ) {
                Icon(
                    if (expanded) Icons.Filled.Close else Icons.Filled.Hub,
                    contentDescription = if (expanded) "Close connection menu" else "Connection",
                )
            }
        },
    ) {
        actions.forEach { action ->
            FloatingActionButton(
                onClick = {
                    expanded = false
                    action.onClick()
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(action.icon, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(action.label, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

/**
 * Quick Save as ONE connected button group (MD3 Expressive): a row of
 * ToggleButtons with connected shapes — the selected one is a full pill,
 * inner corners tighten, exactly per the button-group spec.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuickSaveConnectedGroup(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            val shapes = when {
                options.size == 1 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                index == 0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                index == options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }
            ToggleButton(
                checked = index == selectedIndex,
                onCheckedChange = { if (it) onSelect(index) },
                shapes = shapes,
                modifier = Modifier.weight(1f).padding(horizontal = 1.dp),
            ) {
                if (index == selectedIndex) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp),
                    )
                }
                Text(label)
            }
        }
    }
}
