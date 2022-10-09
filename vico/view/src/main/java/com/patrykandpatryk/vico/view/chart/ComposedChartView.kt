/*
 * Copyright 2022 Patryk Goworowski and Patryk Michalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatryk.vico.view.chart

import android.content.Context
import android.util.AttributeSet
import com.patrykandpatryk.vico.core.chart.composed.ComposedChartEntryModel
import com.patrykandpatryk.vico.core.entry.ChartEntryModel
import com.patrykandpatryk.vico.core.util.RandomEntriesGenerator
import com.patrykandpatryk.vico.view.theme.ThemeHandler

/**
 * A subclass of [BaseChartView] that displays charts that use [ComposedChartEntryModel].
 */
public class ComposedChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseChartView<ComposedChartEntryModel<ChartEntryModel>>(
    context = context,
    attrs = attrs,
    defStyleAttr = defStyleAttr,
    chartType = ThemeHandler.ChartType.Composed,
) {

    init {
        chart = themeHandler.composedChart
        if (isInEditMode) setModel(model = RandomEntriesGenerator().randomComposedEntryModel())
    }
}
