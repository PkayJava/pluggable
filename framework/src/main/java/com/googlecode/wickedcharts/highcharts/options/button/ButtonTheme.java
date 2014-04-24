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
package com.googlecode.wickedcharts.highcharts.options.button;

import java.io.Serializable;

import com.googlecode.wickedcharts.highcharts.options.StatesChoice;
import com.googlecode.wickedcharts.highcharts.options.color.ColorReference;

/**
 * Theme for a button.
 * 
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
public class ButtonTheme implements Serializable {

	private ColorReference fill;

	private ColorReference stroke;

	private Integer r;

	private StatesChoice states;

	public ButtonTheme setStates(final StatesChoice states) {
		this.states = states;
		return this;
	}

	public StatesChoice getStates() {
		return this.states;
	}

	public ButtonTheme setR(final Integer r) {
		this.r = r;
		return this;
	}

	public Integer getR() {
		return this.r;
	}

	public ButtonTheme setStroke(final ColorReference stroke) {
		this.stroke = stroke;
		return this;
	}

	public ColorReference getStroke() {
		return this.stroke;
	}

	public ButtonTheme setFill(final ColorReference fill) {
		this.fill = fill;
		return this;
	}

	public ColorReference getFill() {
		return this.fill;
	}

}
