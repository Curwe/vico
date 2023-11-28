/*
 * Copyright 2023 by Patryk Goworowski and Patrick Michalik.
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

package com.patrykandpatrick.vico.compose.chart

import android.annotation.SuppressLint
import android.graphics.RectF
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.entry.collectAsState
import com.patrykandpatrick.vico.compose.chart.entry.defaultDiffAnimationSpec
import com.patrykandpatrick.vico.compose.chart.layout.segmented
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollState
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.extension.chartTouchEvent
import com.patrykandpatrick.vico.compose.gesture.OnZoom
import com.patrykandpatrick.vico.compose.layout.getMeasureContext
import com.patrykandpatrick.vico.compose.state.component1
import com.patrykandpatrick.vico.compose.state.component2
import com.patrykandpatrick.vico.compose.state.component3
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.DEF_MAX_ZOOM
import com.patrykandpatrick.vico.core.DEF_MIN_ZOOM
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.AxisManager
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.AxisRenderer
import com.patrykandpatrick.vico.core.chart.Chart
import com.patrykandpatrick.vico.core.chart.composed.ComposedChart
import com.patrykandpatrick.vico.core.chart.dimensions.MutableHorizontalDimensions
import com.patrykandpatrick.vico.core.chart.draw.chartDrawContext
import com.patrykandpatrick.vico.core.chart.draw.drawMarker
import com.patrykandpatrick.vico.core.chart.draw.getAutoZoom
import com.patrykandpatrick.vico.core.chart.draw.getMaxScrollDistance
import com.patrykandpatrick.vico.core.chart.edges.FadingEdges
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.ChartValuesManager
import com.patrykandpatrick.vico.core.chart.values.ChartValuesProvider
import com.patrykandpatrick.vico.core.chart.values.toChartValuesProvider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartModelProducer
import com.patrykandpatrick.vico.core.extension.getClosestMarkerEntryModel
import com.patrykandpatrick.vico.core.extension.set
import com.patrykandpatrick.vico.core.extension.spToPx
import com.patrykandpatrick.vico.core.layout.VirtualLayout
import com.patrykandpatrick.vico.core.legend.Legend
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import com.patrykandpatrick.vico.core.model.Point
import com.patrykandpatrick.vico.core.scroll.ScrollListener
import com.patrykandpatrick.vico.core.util.ValueWrapper
import com.patrykandpatrick.vico.core.util.getValue
import com.patrykandpatrick.vico.core.util.setValue
import kotlinx.coroutines.launch

/**
 * Displays a chart.
 *
 * @param chart the chart itself (excluding axes, markers, etc.). You can use [lineChart] or [columnChart], or provide a
 * custom [Chart] implementation.
 * @param chartModelProducer creates and updates the [ChartEntryModel] for the chart.
 * @param modifier the modifier to be applied to the chart.
 * @param startAxis the axis displayed at the start of the chart.
 * @param topAxis the axis displayed at the top of the chart.
 * @param endAxis the axis displayed at the end of the chart.
 * @param bottomAxis the axis displayed at the bottom of the chart.
 * @param marker appears when the chart is touched, highlighting the entry or entries nearest to the touch point.
 * @param markerVisibilityChangeListener allows for listening to [marker] visibility changes.
 * @param legend an optional legend for the chart.
 * @param chartScrollSpec houses scrolling-related settings.
 * @param isZoomEnabled whether zooming in and out is enabled.
 * @param diffAnimationSpec the animation spec used for difference animations.
 * @param runInitialAnimation whether to display an animation when the chart is created. In this animation, the value
 * of each chart entry is animated from zero to the actual value. This animation isn’t run in previews.
 * @param fadingEdges applies a horizontal fade to the edges of the chart area for scrollable charts.
 * @param autoScaleUp defines whether the content of the chart should be scaled up when the dimensions are such that, at
 * a scale factor of 1, an empty space would be visible near the end edge of the chart.
 * @param chartScrollState houses information on the chart’s scroll state. Allows for programmatic scrolling.
 * @param horizontalLayout defines how the chart’s content is positioned horizontally.
 * @param getXStep overrides the _x_ step (the difference between the _x_ values of neighboring major entries). If this
 * is null, the default _x_ step ([ChartEntryModel.xGcd]) is used.
 * @param placeholder shown when no [ChartEntryModel] is available.
 */
@Composable
public fun <Model : ChartEntryModel> Chart(
    chart: Chart<Model>,
    chartModelProducer: ChartModelProducer<Model>,
    modifier: Modifier = Modifier,
    startAxis: AxisRenderer<AxisPosition.Vertical.Start>? = null,
    topAxis: AxisRenderer<AxisPosition.Horizontal.Top>? = null,
    endAxis: AxisRenderer<AxisPosition.Vertical.End>? = null,
    bottomAxis: AxisRenderer<AxisPosition.Horizontal.Bottom>? = null,
    marker: Marker? = null,
    markerVisibilityChangeListener: MarkerVisibilityChangeListener? = null,
    legend: Legend? = null,
    chartScrollSpec: ChartScrollSpec<Model> = rememberChartScrollSpec(),
    isZoomEnabled: Boolean = true,
    diffAnimationSpec: AnimationSpec<Float>? = defaultDiffAnimationSpec,
    runInitialAnimation: Boolean = true,
    fadingEdges: FadingEdges? = null,
    autoScaleUp: AutoScaleUp = AutoScaleUp.Full,
    chartScrollState: ChartScrollState = rememberChartScrollState(),
    horizontalLayout: HorizontalLayout = HorizontalLayout.segmented(),
    getXStep: ((Model) -> Float)? = null,
    placeholder: @Composable BoxScope.() -> Unit = {},
) {
    val chartValuesManager = remember(chart) { ChartValuesManager() }
    val chartEntryModelWrapper by chartModelProducer
        .collectAsState(chart, chartModelProducer, diffAnimationSpec, runInitialAnimation, chartValuesManager, getXStep)
    val (chartEntryModel, previousChartEntryModel, chartValuesProvider) = chartEntryModelWrapper

    ChartBox(modifier = modifier) {
        if (chartEntryModel != null) {
            ChartImpl(
                chart = chart,
                model = chartEntryModel,
                oldModel = previousChartEntryModel,
                startAxis = startAxis,
                topAxis = topAxis,
                endAxis = endAxis,
                bottomAxis = bottomAxis,
                marker = marker,
                markerVisibilityChangeListener = markerVisibilityChangeListener,
                legend = legend,
                chartScrollSpec = chartScrollSpec,
                isZoomEnabled = isZoomEnabled,
                fadingEdges = fadingEdges,
                autoScaleUp = autoScaleUp,
                chartScrollState = chartScrollState,
                horizontalLayout = horizontalLayout,
                chartValuesProvider = chartValuesProvider,
            )
        } else {
            placeholder()
        }
    }
}

/**
 * Displays a chart.
 *
 * This function accepts a [ChartEntryModel]. For dynamic data, use the function overload that accepts a
 * [ChartModelProducer] instance.
 *
 * @param chart the chart itself (excluding axes, markers, etc.). You can use [lineChart] or [columnChart], or provide a
 * custom [Chart] implementation.
 * @param model the [ChartEntryModel] for the chart.
 * @param modifier the modifier to be applied to the chart.
 * @param startAxis the axis displayed at the start of the chart.
 * @param topAxis the axis displayed at the top of the chart.
 * @param endAxis the axis displayed at the end of the chart.
 * @param bottomAxis the axis displayed at the bottom of the chart.
 * @param marker appears when the chart is touched, highlighting the entry or entries nearest to the touch point.
 * @param markerVisibilityChangeListener allows for listening to [marker] visibility changes.
 * @param legend an optional legend for the chart.
 * @param chartScrollSpec houses scrolling-related settings.
 * @param isZoomEnabled whether zooming in and out is enabled.
 * @param oldModel the chart’s previous [ChartEntryModel]. This is used to determine whether to perform an automatic
 * scroll.
 * @param fadingEdges applies a horizontal fade to the edges of the chart area for scrollable charts.
 * @param autoScaleUp defines whether the content of the chart should be scaled up when the dimensions are such that, at
 * a scale factor of 1, an empty space would be visible near the end edge of the chart.
 * @param chartScrollState houses information on the chart’s scroll state. Allows for programmatic scrolling.
 * @param horizontalLayout defines how the chart’s content is positioned horizontally.
 * @param getXStep overrides the _x_ step (the difference between the _x_ values of neighboring major entries). If this
 * is null, the default _x_ step ([ChartEntryModel.xGcd]) is used.
 */
@Composable
@SuppressLint("RememberReturnType")
public fun <Model : ChartEntryModel> Chart(
    chart: Chart<Model>,
    model: Model,
    modifier: Modifier = Modifier,
    startAxis: AxisRenderer<AxisPosition.Vertical.Start>? = null,
    topAxis: AxisRenderer<AxisPosition.Horizontal.Top>? = null,
    endAxis: AxisRenderer<AxisPosition.Vertical.End>? = null,
    bottomAxis: AxisRenderer<AxisPosition.Horizontal.Bottom>? = null,
    marker: Marker? = null,
    markerVisibilityChangeListener: MarkerVisibilityChangeListener? = null,
    legend: Legend? = null,
    chartScrollSpec: ChartScrollSpec<Model> = rememberChartScrollSpec(),
    isZoomEnabled: Boolean = true,
    oldModel: Model? = null,
    fadingEdges: FadingEdges? = null,
    autoScaleUp: AutoScaleUp = AutoScaleUp.Full,
    chartScrollState: ChartScrollState = rememberChartScrollState(),
    horizontalLayout: HorizontalLayout = HorizontalLayout.segmented(),
    getXStep: ((Model) -> Float)? = null,
) {
    val chartValuesManager = remember(chart) { ChartValuesManager() }
    remember(chartValuesManager, model, getXStep) {
        chartValuesManager.resetChartValues()
        chart.updateChartValues(chartValuesManager, model, getXStep?.invoke(model))
    }
    ChartBox(modifier = modifier) {
        ChartImpl(
            chart = chart,
            model = model,
            startAxis = startAxis,
            topAxis = topAxis,
            endAxis = endAxis,
            bottomAxis = bottomAxis,
            marker = marker,
            markerVisibilityChangeListener = markerVisibilityChangeListener,
            legend = legend,
            chartScrollSpec = chartScrollSpec,
            isZoomEnabled = isZoomEnabled,
            oldModel = oldModel,
            fadingEdges = fadingEdges,
            autoScaleUp = autoScaleUp,
            chartScrollState = chartScrollState,
            horizontalLayout = horizontalLayout,
            chartValuesProvider = chartValuesManager.toChartValuesProvider(),
        )
    }
}

@Suppress("LongMethod")
@Composable
internal fun <Model : ChartEntryModel> ChartImpl(
    chart: Chart<Model>,
    model: Model,
    startAxis: AxisRenderer<AxisPosition.Vertical.Start>?,
    topAxis: AxisRenderer<AxisPosition.Horizontal.Top>?,
    endAxis: AxisRenderer<AxisPosition.Vertical.End>?,
    bottomAxis: AxisRenderer<AxisPosition.Horizontal.Bottom>?,
    marker: Marker?,
    markerVisibilityChangeListener: MarkerVisibilityChangeListener?,
    legend: Legend?,
    chartScrollSpec: ChartScrollSpec<Model>,
    isZoomEnabled: Boolean,
    oldModel: Model? = null,
    fadingEdges: FadingEdges?,
    autoScaleUp: AutoScaleUp,
    chartScrollState: ChartScrollState = rememberChartScrollState(),
    horizontalLayout: HorizontalLayout,
    chartValuesProvider: ChartValuesProvider,
) {
    val axisManager = remember { AxisManager() }
    val bounds = remember { RectF() }
    val markerTouchPoint = remember { mutableStateOf<Point?>(null) }
    val zoom = remember { mutableFloatStateOf(0f) }
    val wasZoomOverridden = remember { mutableStateOf(false) }
    val measureContext = getMeasureContext(
        chartScrollSpec.isScrollEnabled,
        bounds,
        horizontalLayout,
        with(LocalContext.current) { ::spToPx },
        chartValuesProvider,
    )
    val scrollListener = rememberScrollListener(markerTouchPoint)
    val lastMarkerEntryModels = remember { mutableStateOf(emptyList<Marker.EntryModel>()) }
    var previousIndex by remember { mutableIntStateOf(-1) }

    axisManager.setAxes(startAxis, topAxis, endAxis, bottomAxis)
    chartScrollState.registerScrollListener(scrollListener)

    val virtualLayout = remember { VirtualLayout(axisManager) }
    val elevationOverlayColor = currentChartStyle.elevationOverlayColor.toArgb()
    val (wasMarkerVisible, setWasMarkerVisible) = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var previousModelID by remember { ValueWrapper(model.id) }
    val horizontalDimensions = remember { MutableHorizontalDimensions() }
    val clickWrapper = remember { ClickWrapper(false) }
    val touchPoint = remember(marker, clickWrapper.isClicked) {
        markerTouchPoint
            .component2()
            .takeIf { marker != null }
    }

    val onZoom = rememberZoomState(
        zoom = zoom,
        wasZoomOverridden = wasZoomOverridden,
        getScroll = { chartScrollState.value },
        scrollBy = { value -> coroutineScope.launch { chartScrollState.scrollBy(value) } },
        chartBounds = chart.bounds,
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .chartTouchEvent(
                setTouchPoint = touchPoint,
                isScrollEnabled = chartScrollSpec.isScrollEnabled,
                scrollableState = chartScrollState,
                onZoom = onZoom.takeIf { isZoomEnabled },
                clickWrapper = clickWrapper
            ),
    ) {
        bounds.set(left = 0, top = 0, right = size.width, bottom = size.height)

        horizontalDimensions.clear()
        chart.updateHorizontalDimensions(measureContext, horizontalDimensions, model)

        startAxis?.updateHorizontalDimensions(measureContext, horizontalDimensions)
        topAxis?.updateHorizontalDimensions(measureContext, horizontalDimensions)
        endAxis?.updateHorizontalDimensions(measureContext, horizontalDimensions)
        bottomAxis?.updateHorizontalDimensions(measureContext, horizontalDimensions)

        if (
            virtualLayout
                .setBounds(
                    context = measureContext,
                    contentBounds = bounds,
                    chart = chart,
                    legend = legend,
                    horizontalDimensions = horizontalDimensions,
                    marker,
                )
                .isEmpty
        ) {
            return@Canvas
        }

        var finalZoom = zoom.floatValue

        if (!wasZoomOverridden.value || !chartScrollSpec.isScrollEnabled) {
            finalZoom = measureContext.getAutoZoom(horizontalDimensions, chart.bounds, autoScaleUp)
            if (chartScrollSpec.isScrollEnabled) zoom.floatValue = finalZoom
        }

        chartScrollState.maxValue = measureContext.getMaxScrollDistance(
            chartWidth = chart.bounds.width(),
            horizontalDimensions = horizontalDimensions,
            zoom = finalZoom,
        )

        if (model.id != previousModelID) {
            coroutineScope.launch { chartScrollSpec.performAutoScroll(model, oldModel, chartScrollState) }
            previousModelID = model.id
        }

        chartScrollState.handleInitialScroll(initialScroll = chartScrollSpec.initialScroll)

        val chartDrawContext = chartDrawContext(
            canvas = drawContext.canvas.nativeCanvas,
            elevationOverlayColor = elevationOverlayColor,
            measureContext = measureContext,
            markerTouchPoint = markerTouchPoint.value,
            horizontalDimensions = horizontalDimensions,
            chartBounds = chart.bounds,
            horizontalScroll = chartScrollState.value,
            zoom = finalZoom,
        )

        val tappedChart = if (chart is ComposedChart<*>) chart.charts[1] else chart
        val entryModel = markerTouchPoint.value?.let {
            tappedChart.entryLocationMap.getClosestMarkerEntryModel(it)
        }

        var clickedChartItemIndex = entryModel?.firstOrNull()?.index

        clickedChartItemIndex?.let {
            if (clickWrapper.isClicked) {
                clickWrapper.isClicked = false
                if (previousIndex == clickedChartItemIndex) {
                    previousIndex = -1
                    clickedChartItemIndex = null
                    touchPoint?.invoke(null)
                }

                previousIndex = clickedChartItemIndex ?: -1
            }
        }

        val count = if (fadingEdges != null) chartDrawContext.saveLayer() else -1

        axisManager.drawBehindChart(chartDrawContext)
        chart.drawScrollableContent(chartDrawContext, model, clickedChartItemIndex)

        fadingEdges?.apply {
            applyFadingEdges(chartDrawContext, chart.bounds)
            chartDrawContext.restoreCanvasToCount(count)
        }

        axisManager.drawAboveChart(chartDrawContext)
        chart.drawNonScrollableContent(chartDrawContext, model)
        legend?.draw(chartDrawContext)

        if (marker != null) {
            chartDrawContext.drawMarker(
                marker = marker,
                markerTouchPoint = markerTouchPoint.value,
                chart = chart,
                markerVisibilityChangeListener = markerVisibilityChangeListener,
                wasMarkerVisible = wasMarkerVisible,
                setWasMarkerVisible = setWasMarkerVisible,
                lastMarkerEntryModels = lastMarkerEntryModels.value,
                onMarkerEntryModelsChange = lastMarkerEntryModels.component2(),
            )
        }

        measureContext.reset()
    }
}

public data class ClickWrapper(var isClicked: Boolean)

@Composable
internal fun ChartBox(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.height(DefaultDimens.CHART_HEIGHT.dp).fillMaxWidth(),
        content = content,
    )
}

@Composable
internal fun rememberScrollListener(touchPoint: MutableState<Point?>): ScrollListener = remember {
    object : ScrollListener {
        override fun onValueChanged(oldValue: Float, newValue: Float) {
            touchPoint.value?.let { point ->
                touchPoint.value = point.copy(x = point.x + oldValue - newValue)
            }
        }

        override fun onScrollNotConsumed(delta: Float) {
            touchPoint.value?.let { point ->
                touchPoint.value = point.copy(x = point.x - delta)
            }
        }
    }
}

@Composable
internal fun rememberZoomState(
    zoom: MutableFloatState,
    wasZoomOverridden: MutableState<Boolean>,
    getScroll: () -> Float,
    scrollBy: (value: Float) -> Unit,
    chartBounds: RectF,
): OnZoom = remember {
    onZoom@{ centroid, zoomChange ->
        val newZoom = zoom.floatValue * zoomChange
        if (newZoom !in DEF_MIN_ZOOM..DEF_MAX_ZOOM) return@onZoom
        val transformationAxisX = getScroll() + centroid.x - chartBounds.left
        val zoomedTransformationAxisX = transformationAxisX * zoomChange
        zoom.floatValue = newZoom
        scrollBy(zoomedTransformationAxisX - transformationAxisX)
        wasZoomOverridden.value = true
    }
}
