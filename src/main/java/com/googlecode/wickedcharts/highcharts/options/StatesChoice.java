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
package com.googlecode.wickedcharts.highcharts.options;

import java.io.Serializable;

/**
 * Defines a choice of one or more "states" options.
 * 
 * @see <a
 *      href="http://api.highcharts.com/highcharts#plotOptions.area.marker.states">http://api.highcharts.com/highcharts#plotOptions.area.marker.states</a>
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public class StatesChoice implements Serializable {

	public enum StateType {
		HOVER, SELECT;
	}

	private static final long serialVersionUID = 1L;

	private State hover;

	private State select;

	public StatesChoice() {

	}

	public StatesChoice(final State state, final StateType type) {
		switch (type) {
			case HOVER:
				this.hover = state;
				break;
			case SELECT:
				this.select = state;
				break;
			default:
				throw new IllegalArgumentException("Invalid StateType: " + type);
		}
	}

	public State getHover() {
		return this.hover;
	}

	public State getSelect() {
		return this.select;
	}

	public StatesChoice setHover(final State hover) {
		this.hover = hover;
		return this;
	}

	public StatesChoice setSelect(final State select) {
		this.select = select;
		return this;
	}

}
