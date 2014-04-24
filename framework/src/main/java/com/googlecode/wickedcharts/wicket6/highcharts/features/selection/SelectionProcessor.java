/**
 *   Copyright 2012-2013 Wicked Charts (http://wicked-charts.googlecode.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.googlecode.wickedcharts.wicket6.highcharts.features.selection;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.interaction.SelectionEvent;
import com.googlecode.wickedcharts.highcharts.options.interaction.SelectionFunction;
import com.googlecode.wickedcharts.highcharts.options.processing.IOptionsProcessor;
import com.googlecode.wickedcharts.highcharts.options.processing.OptionsProcessorContext;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

public class SelectionProcessor implements IOptionsProcessor, Serializable {

	private final Chart chart;

	public SelectionProcessor(final Chart chart) {
		this.chart = chart;
	}

	@Override
	public void processOptions(final Options options, final OptionsProcessorContext context) {

		List<SelectionFunction> functions = context.getSelectionFunctions();
		for (final SelectionFunction function : functions) {

			// add server side AJAX event
			SelectionBehavior selectionBehavior = new SelectionBehavior() {
				@Override
				public void onSelection(final SelectionEvent event, final AjaxRequestTarget target) {
					WicketSelectionEvent wicketEvent = new WicketSelectionEvent(target, event);
					function.onSelect(wicketEvent);
				}
			};
			this.chart.add(selectionBehavior);

			// add client side javascript to trigger an AJAX call
			String functionBody = selectionBehavior.getCallbackScript().toString();
			function.setFunction(functionBody);
		}
	}

}
