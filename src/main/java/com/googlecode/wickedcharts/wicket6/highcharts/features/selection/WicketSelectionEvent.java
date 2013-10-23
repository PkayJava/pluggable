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

import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wickedcharts.highcharts.options.interaction.SelectionEvent;

/**
 * Wicket-specific extension of {@link SelectionEvent}.
 * 
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public class WicketSelectionEvent extends SelectionEvent {

	private AjaxRequestTarget ajaxRequestTarget;

	WicketSelectionEvent(final AjaxRequestTarget ajaxRequestTarget, final SelectionEvent sourceEvent) {
		this.setAjaxRequestTarget(ajaxRequestTarget);
		this.xAxes = sourceEvent.getxAxes();
		this.yAxes = sourceEvent.getyAxes();
	}

	public void setAjaxRequestTarget(final AjaxRequestTarget ajaxRequestTarget) {
		this.ajaxRequestTarget = ajaxRequestTarget;
	}

	/**
	 * Returns the {@link AjaxRequestTarget} that is connected to the AJAX request
	 * that was triggered by interacting with a chart.
	 * 
	 * @return the Wicket {@link AjaxRequestTarget}.
	 */
	public AjaxRequestTarget getAjaxRequestTarget() {
		return this.ajaxRequestTarget;
	}

}
