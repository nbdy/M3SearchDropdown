package io.eberlein.m3searchdropdown

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@ExperimentalMaterial3Api
@Composable
fun <EntityType> SearchDropdown(
    label: String,
    onItemSelected: (EntityType) -> String,
    trailingIcon: @Composable (Boolean) -> Unit = {
        ExposedDropdownMenuDefaults.TrailingIcon(it)
    },
    searchFunction: (String, (List<EntityType>) -> Unit) -> Unit,
    dropdownItemContent: @Composable (EntityType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(TextFieldValue("")) }
    var options = remember { mutableStateListOf<EntityType>() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = currentValue,
            onValueChange = { newValue ->
                currentValue = newValue
                searchFunction(newValue.text) {
                    options = it.toMutableStateList()
                    expanded = true
                }
            },
            label = { Text(label) },
            trailingIcon = { trailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    onClick = {
                        val tmp = onItemSelected(it)
                        currentValue = TextFieldValue(tmp, TextRange(tmp.length))
                        expanded = false
                    },
                    text = { dropdownItemContent(it) }
                )
            }
        }
    }
}
