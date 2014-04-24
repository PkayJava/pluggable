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
 * This class is used to mark Highcharts feature which are not (yet) supported
 * by wicked-charts. If you need this feature, please post a feature request at
 * http://code.google.com/p/wicked-charts/issues/list.
 * <p/>
 * Do not use this class, since it's uses in the API are subject to change!
 * 
 * @author Tom Hombergs (tom.hombergs@gmail.com)
 * 
 */
@Deprecated
public class DummyOption implements Serializable {

	private static final long serialVersionUID = 1L;

}
