/*
 * Copyright (c) 2021. Patryk Goworowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.patrykgoworowski.vico.app.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pl.patrykgoworowski.vico.app.ShowcaseViewModel
import pl.patrykgoworowski.vico.app.extension.setUpChart
import pl.patrykgoworowski.vico.databinding.FragmentViewBinding

@Composable
@OptIn(ExperimentalCoroutinesApi::class)
internal fun ViewShowcase(showcaseViewModel: ShowcaseViewModel) {
    val marker = markerComponent()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.verticalScroll(state = scrollState)) {
        Box(modifier = Modifier.padding(16.dp)) {
            AndroidViewBinding(FragmentViewBinding::inflate) {
                with(showcaseViewModel) {
                    columnChart.setUpChart(entries, coroutineScope, marker)
                    lineChart.setUpChart(entries, coroutineScope, marker)
                    composedChart.setUpChart(composedEntries, coroutineScope, marker)
                    groupedColumnChart.setUpChart(multiEntries, coroutineScope, marker)
                    stackedColumnChart.setUpChart(multiEntries, coroutineScope, marker)
                }
            }
        }
    }
}
