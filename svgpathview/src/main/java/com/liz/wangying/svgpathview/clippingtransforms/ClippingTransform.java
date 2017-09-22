/*
 * Copyright (C) 2015 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liz.wangying.svgpathview.clippingtransforms;

import android.graphics.Canvas;
import android.view.View;

/**
 * Public contract to give users the possibility to create custom invisible clipping figures. They
 * will be able to use this thinking it as a "DIFFERENCE" figure to achieve the filling figure
 * he is looking for.
 *
 * @author jorge
 * @since 12/08/15
 */
public interface ClippingTransform  {

  void transform(Canvas canvas, float currentFillPhase, View view);
}
