package io.eberlein.m3searchdropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
fun <EntityType: ISearchableEntity, ModelType: ISearchableViewModel<EntityType>> SearchDropdown(
    label: String,
    model: ModelType,
    value: String = "",
    enableAddButton: Boolean = false,
    onAddButtonClicked: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(TextFieldValue(value)) }
    var options = remember { mutableStateListOf<EntityType>() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = currentValue,
            onValueChange = { newValue ->
                currentValue = newValue
                model.search(newValue.text) {
                    options = it.toMutableStateList()
                    expanded = true
                }
            },
            label = { Text(label) },
            trailingIcon = {
                if (enableAddButton) {
                    if (currentValue.text.isNotEmpty()) {
                        if (options.isEmpty()) {
                            Icon(
                                Icons.Filled.Add,
                                null,
                                modifier = Modifier.clickable { onAddButtonClicked(currentValue.text) }
                            )
                        } else {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    }
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            },
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
                        val tmp = it.getValue()
                        currentValue = TextFieldValue(tmp, TextRange(tmp.length))
                        expanded = false
                    },
                    text = { Text(it.getValue()) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
