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
package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalValueContainer;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
public class GenericGaRoSingleEvalValueContainer extends SpecificEvalValueContainer {
	private final List<GenericGaRoResultType> requestedResultsGaRo;
	private GenericGaRoEvaluationCore evalContainer = null;

	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    public GenericGaRoSingleEvalValueContainer(final int size, 
    		List<GenericGaRoResultType> requestedResultsGaRo, List<ResultType> requestedResults2,
    		List<EvaluationInput> input) {
    	super(size, requestedResults2, input);
    	this.requestedResultsGaRo = requestedResultsGaRo;
    }
    public void setEvalContainer(GenericGaRoEvaluationCore evalContainer) {
    	this.evalContainer = evalContainer;
    }
    
    public synchronized Map<ResultType, EvaluationResult> getCurrentResults() {
    	final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
    	List<TimeSeriesData> inputData;
    	if(input == null || input.isEmpty())
    		inputData = new ArrayList<>();
    	else
    		inputData = input.get(0).getInputData();
    	evalContainer.gapTime = gapTime;
     	for (GenericGaRoResultType rt : requestedResultsGaRo) {
    		final SingleEvaluationResult singleRes;
    		if (rt == GenericGaRoSingleEvalProvider.GAP_TIME) {
        		singleRes = new SingleValueResultImpl<Long>(rt, gapTime, inputData);
    		} else
    			singleRes = rt.getEvalResult(evalContainer, rt, inputData);
     		if(singleRes == null)
     			throw new IllegalArgumentException("Invalid result type requested: " + (rt != null ? rt.id() : null));
    		results.put(rt, new EvaluationResultImpl(Collections.singletonList(singleRes), rt));
    	}
    	return results;
    }

}