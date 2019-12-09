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
package de.iwes.timeseries.eval.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface OnlineEvaluation extends EvaluationInstance {

	/**
	 * Finish the online evaluation
	 * @return
	 */
	Status finish();
	
	/**
	 * Finish the online evaluation
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws TimeoutException
	 */
	@Deprecated
	Status finish(long timeout, TimeUnit unit) throws TimeoutException;
	
	/**
	 * Get current results but continue the evaluation. Contrary to
	 * {@link #getResults()} this does not throw {@link IllegalStateException}
	 * when called before the evaluation is finished.
	 * @throws IllegalStateException
	 * 		If the evaluation failed
	 * @return
	 */
	Map<ResultType, EvaluationResult> getIntermediateResults();
	
	
}