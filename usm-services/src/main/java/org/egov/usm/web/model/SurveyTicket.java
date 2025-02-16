package org.egov.usm.web.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.usm.model.enums.TicketStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyTicket {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("ward")
	private String ward;

	@JsonProperty("slumCode")
	private String slumCode;

	@JsonProperty("ticketNo")
	private String ticketNo;

	@JsonProperty("surveyAnswerId")
	private String surveyAnswerId;

	@JsonProperty("questionId")
	private String questionId;

	@JsonProperty("questionCategory")
	private String questionCategory;

	@JsonProperty("ticketDescription")
	private String ticketDescription;
	
	@JsonProperty("ticketDescriptionOdia")
	private String ticketDescriptionOdia;

	@JsonProperty("status")
	private TicketStatus status;

	@JsonProperty("ticketCreatedTime")
	private Long ticketCreatedTime;

	@JsonProperty("ticketClosedTime")
	private Long ticketClosedTime;

	@JsonProperty("hasOpenTicket")
	private Boolean hasOpenTicket;

	@JsonProperty("additionalDetail")
	private String additionalDetail;

	@JsonProperty("unAttended")
	private Boolean unAttended;

	@JsonProperty("isSatisfied")
	private Boolean isSatisfied;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("surveyTicketComments")
	private List<SurveyTicketComment> surveyTicketComments;
	
	@JsonProperty("sdaName")
	private String sdaName;
	
	@JsonProperty("sdaMobileNo")
	private String sdaMobileNo;
	
	@JsonProperty("escalatedId")
	private String escalatedId;
	
	@JsonProperty("escalatedTime")
	private Long escalatedTime;

	public SurveyTicket addTicketComment(SurveyTicketComment surveyTicketComment) {
		if (this.surveyTicketComments == null) {
			this.surveyTicketComments = new ArrayList<>();
		}

		if (null != surveyTicketComments)
			this.surveyTicketComments.add(surveyTicketComment);
		return this;
	}

}
