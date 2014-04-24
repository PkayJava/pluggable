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
package com.googlecode.wickedcharts.highcharts.options.livedata;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.wickedcharts.highcharts.options.IProcessableOption;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;

/**
 * This Series class supports regular updating via AJAX.
 * 
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public abstract class LiveDataSeries extends PointSeries implements IProcessableOption {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> javascriptParameters = new HashMap<String, String>();

	/**
	 * The key under which {@link LiveDataSeries} are registered in the parent
	 * options. See {@link Options#markForProcessing(IProcessableOption)} .
	 */
	public static final String PROCESSING_KEY = "LIVEUPDATE";

	@JsonIgnore
	private final Options parentOptions;

	@JsonIgnore
	private int updateIntervalMs;

	/**
	 * Constructs a new {@link LiveDataSeries}.
	 * 
	 * @param parentOptions
	 *          the {@link Options} to which this series are added.
	 * @param updateIntervalMs
	 *          the interval in which to update the series in milliseconds.
	 */
	public LiveDataSeries(final Options parentOptions, final int updateIntervalMs) {
		this.parentOptions = parentOptions;
		this.setUpdateIntervalMs(updateIntervalMs);
		parentOptions.markForProcessing(this);
	}

	public Options getParentOptions() {
		return this.parentOptions;
	}

	public LiveDataSeries setUpdateIntervalMs(final int updateIntervalMs) {
		this.updateIntervalMs = updateIntervalMs;
		return this;
	}

	/**
	 * Adds a javascript parameter that will be passed into the
	 * {@link #update(JavaScriptParameters)} method.
	 * 
	 * @param parameterName
	 *          the name of the parameter
	 * @param javascriptExpression
	 *          a javascript expression. The value this expression evaluates to
	 *          will be transmitted to the server via AJAX and will be passed into
	 *          {@link #update(JavaScriptParameters)}. The javascript expression
	 *          may be a function call or a literal. If it is a literal string,
	 *          you have to surround the string with quotes.
	 * @return this object for chaining
	 */
	public LiveDataSeries addJavaScriptParameter(final String parameterName, final String javascriptExpression) {
		this.javascriptParameters.put(parameterName, javascriptExpression);
		return this;
	}

	public Map<String, String> getJavaScriptParameters() {
		return this.javascriptParameters;
	}

	public int getUpdateIntervalMs() {
		return this.updateIntervalMs;
	}

	@Override
	@JsonIgnore
	public String getProcessingKey() {
		return PROCESSING_KEY;
	};

	/**
	 * This method is called for each update interval. It must return a point
	 * which is then added to the series on the fly.
	 * <p/>
	 * May return null. In that case, the chart is simply not updated.
	 * 
	 * @param event
	 *          the {@link LiveDataUpdateEvent}
	 * 
	 * @return the new point to add to the series. This point is added by calling
	 *         Highcharts' addPoint() function. Please note that Highcharts does
	 *         not evaluate all attributes of this point. Also not that to define
	 *         the color of the point you have to add a Marker with the fillColor
	 *         attribute defined!
	 */
	public abstract Point update(final LiveDataUpdateEvent event);

}
