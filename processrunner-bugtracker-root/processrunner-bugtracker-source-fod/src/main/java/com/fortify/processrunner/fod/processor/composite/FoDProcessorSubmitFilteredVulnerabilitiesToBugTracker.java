/*******************************************************************************
 * (c) Copyright 2017 Hewlett Packard Enterprise Development LP
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.processrunner.fod.processor.composite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fortify.processrunner.common.processor.IProcessorSubmitIssueForVulnerabilities;
import com.fortify.processrunner.fod.processor.enrich.FoDProcessorEnrichWithVulnState;
import com.fortify.processrunner.fod.vulnerability.FoDVulnerabilityUpdater;
import com.fortify.processrunner.processor.CompositeProcessor;
import com.fortify.processrunner.processor.IProcessor;

/**
 * TODO Update class comment
 */
@Component
public class FoDProcessorSubmitFilteredVulnerabilitiesToBugTracker extends AbstractFoDProcessorRetrieveFilteredVulnerabilities {
	private final FoDProcessorEnrichWithVulnState enrichWithVulnStateProcessor = new FoDProcessorEnrichWithVulnState(); 
	private IProcessorSubmitIssueForVulnerabilities submitIssueProcessor;
	private FoDVulnerabilityUpdater vulnerabilityUpdater;
	
	@Override
	protected CompositeProcessor createTopLevelFieldFilters() {
		CompositeProcessor result = super.createTopLevelFieldFilters();
		if ( submitIssueProcessor.isIgnorePreviouslySubmittedIssues() && getVulnerabilityUpdater()!=null ) {
			result.getProcessors().add(getVulnerabilityUpdater().createVulnerabilityNotYetSubmittedFilter());
		}
		return result;
	}
	
	@Override
	protected IProcessor getVulnerabilityProcessor() {
		return new CompositeProcessor(getVulnState(), getSubmitIssueProcessor());
	}

	public IProcessorSubmitIssueForVulnerabilities getSubmitIssueProcessor() {
		return submitIssueProcessor;
	}

	@Autowired
	public void setSubmitIssueProcessor(IProcessorSubmitIssueForVulnerabilities submitIssueProcessor) {
		this.submitIssueProcessor = submitIssueProcessor;
	}
	
	public FoDProcessorEnrichWithVulnState getVulnState() {
		return enrichWithVulnStateProcessor;
	}
	
	@Autowired(required=false)
	public void setConfiguration(FoDBugTrackerProcessorConfiguration config) {
		setAllFieldRegExFilters(config.getAllFieldRegExFilters());
		setExtraFields(config.getExtraFields());
		setTopLevelFieldRegExFilters(config.getTopLevelFieldRegExFilters());
		setTopLevelFieldSimpleFilters(config.getTopLevelFieldSimpleFilters());
		getVulnState().setIsVulnerabilityOpenExpression(config.getIsVulnerabilityOpenExpression());
	}

	public FoDVulnerabilityUpdater getVulnerabilityUpdater() {
		return vulnerabilityUpdater;
	}

	@Autowired(required=false)
	public void setVulnerabilityUpdater(FoDVulnerabilityUpdater vulnerabilityUpdater) {
		this.vulnerabilityUpdater = vulnerabilityUpdater;
	}

	
}
