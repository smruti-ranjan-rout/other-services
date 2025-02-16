package org.egov.report.web.model;

import java.util.List;

import org.egov.report.model.IncentiveAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncentiveResponse {

	@JsonProperty("Incentives")
	List<IncentiveAnalysis> incentiveAnalysis;
}
