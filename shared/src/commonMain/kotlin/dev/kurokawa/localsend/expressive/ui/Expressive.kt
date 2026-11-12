package dev.kurokawa.localsend.expressive.ui
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.*
import com.composables.icons.materialsymbols.outlinedfilled.*

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The newest official MD3 Expressive components, isolated in one file:
 * if a future material3 bump renames an API, this is the only file that
 * changes. Ernest's rule: official components first, custom only where
 * Google ships nothing.
 *
 * (v0.8.0: the FAB connection fan that used to live here is gone —
 * Ernest ordered it removed: it duplicated the connection actions and
 * never collapsed. Connection setup lives on the Home page now.)
 */

/**
 * Quick Save as ONE connected button group (MD3 Expressive): a row of
 * ToggleButtons with connected shapes — the selected one is a full pill,
 * inner corners tighten, exactly per the button-group spec.
 *
 * No check icon inside the segments: with three options ("Off",
 * "Favorites", "Everyone") the icon + label overflowed the segment and
 * the label wrapped mid-word (Ernest's v0.6.0 screenshot). The filled
 * selected shape already communicates the choice.
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
                Text(label, maxLines = 1, softWrap = false)
            }
        }
    }
}
