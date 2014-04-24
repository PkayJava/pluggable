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
package com.googlecode.wickedcharts.highcharts.options.series;

import java.util.Arrays;
import java.util.List;

/**
 * A series of pairs of numbers.
 * 
 * @see <a
 *      href="http://api.highcharts.com/highcharts#series.data">http://api.highcharts.com/highcharts#series.data</a>
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public class CoordinatesSeries extends Series<Coordinate<Number, Number>> {

	private static final long serialVersionUID = 1L;

	public CoordinatesSeries addPoint(final Number x, final Number y) {
		super.addPoint(new Coordinate<Number, Number>(x, y));
		return this;
	}

	@Override
	public List<Coordinate<Number, Number>> getData() {
		return super.getData();
	}

	@Override
	public CoordinatesSeries setData(final Coordinate<Number, Number>... data) {
		super.setData(Arrays.asList(data));
		return this;
	}

	@Override
	public CoordinatesSeries setData(final List<Coordinate<Number, Number>> data) {
		super.setData(data);
		return this;
	}

}
