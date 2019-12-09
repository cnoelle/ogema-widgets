/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.widgets.reveal.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public interface ColumnTemplate {
	
	Map<String, PageSnippetI> update(OgemaWidget parent, OgemaHttpRequest req, 
			Collection<OgemaWidget> triggers, Collection<OgemaWidget> triggered);
	
	default Map<String, PageSnippetI> update(OgemaWidget parent, OgemaHttpRequest req) {
		return update(parent, req, Collections.emptyList(), Collections.emptyList());
	}
	
}