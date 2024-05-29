/*
 * Copyright 2024 by Patryk Goworowski and Patrick Michalik.
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

package com.patrykandpatrick.vico.core.cartesian.decoration

import android.graphics.RectF
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext

/**
 * A [Decoration] presents additional information on a [CartesianChart].
 *
 * @see [HorizontalBox]
 * @see [HorizontalLine]
 */
public interface Decoration {
  /**
   * Called before the [CartesianChart] starts drawing itself.
   *
   * @param [context] holds the information needed to draw the [CartesianChart].
   * @param [bounds] the bounding box of the [CartesianChart].
   */
  public fun onDrawBehindChart(context: CartesianDrawContext, bounds: RectF): Unit = Unit

  /**
   * Called immediately after the [CartesianChart] finishes drawing itself.
   *
   * @param [context] holds the information needed to draw the [CartesianChart].
   * @param [bounds] the bounding box of the [CartesianChart].
   */
  public fun onDrawAboveChart(context: CartesianDrawContext, bounds: RectF): Unit = Unit
}
