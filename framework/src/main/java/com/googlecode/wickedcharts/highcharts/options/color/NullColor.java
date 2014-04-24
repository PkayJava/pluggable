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
package com.googlecode.wickedcharts.highcharts.options.color;

/**
 * A {@link ColorReference} that is always rendered as JSON "null". Use this
 * class if you want to explicitly set a color to null to override a Highcharts
 * default.
 * 
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public class NullColor extends ColorReference {

	private static final long serialVersionUID = 1L;

	@Override
	protected ColorReference copy() {
		return new NullColor();
	}

	@Override
	public boolean isNull() {
		return true;
	}

}
