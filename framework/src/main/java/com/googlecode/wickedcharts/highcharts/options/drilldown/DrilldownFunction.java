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
package com.googlecode.wickedcharts.highcharts.options.drilldown;

import com.googlecode.wickedcharts.highcharts.options.Function;

/**
 * A javascript function that when triggered changes the options of the current
 * chart and rerenders the chart.
 * <p/>
 * This class is not part of the public API! Use {@link DrilldownPoint}s to
 * enable drilldown in your charts.
 * 
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public class DrilldownFunction extends Function {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param drilldownArrayName
	 *          name of the javascript array that holds the drilldown options for
	 *          each point.
	 */
	public DrilldownFunction(String drilldownArrayName) {
		setFunction("drilldown(this, " + drilldownArrayName + ");");
	}

}
