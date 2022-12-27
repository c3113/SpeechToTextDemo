package com.hilary.speech.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @description:
 * @author ChenKai
 * @date 2022/12/26
 */
@Composable
fun ActionsComposable(startRecord: (Int) -> Unit, endRecord: (Int) -> Unit) {
    val modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    Column(modifier = modifier) {
        Button(onClick = {startRecord(1)}) {
            Text(text = "Start1 Record")
        }
        Button(onClick = { endRecord(2)}) {
            Text(text = "Stop2 Record")
        }
        Button(onClick = {startRecord(3)}) {
            Text(text = "Start3 Record")
        }
        Button(onClick = { endRecord(4)}) {
            Text(text = "Stop4 Record")
        }

    }
}

@Preview
@Composable
fun ActionsPreview() {
    ActionsComposable({}, {})
}