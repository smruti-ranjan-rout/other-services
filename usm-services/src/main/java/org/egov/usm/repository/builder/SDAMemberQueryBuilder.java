package org.egov.usm.repository.builder;

import java.util.List;

import org.egov.usm.web.model.MemberSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SDAMemberQueryBuilder {
	
	/**
	 * @param searchCriteria
	 * @param preparedStmtList
	 * @return final query String
	 */
	public String getMemberSearchQuery(MemberSearchCriteria searchCriteria, List<Object> preparedStmtList) {
		StringBuilder query = new StringBuilder("SELECT sda.id, sda.userid, sda.tenantid, sda.ward, sda.slumcode, sda.active, sda.createdtime, sda.createdby, sda.lastmodifiedtime, sda.lastmodifiedby FROM eg_usm_sda_mapping sda");
		
		if(!ObjectUtils.isEmpty(searchCriteria.getTicketId())){
            query.append(" LEFT OUTER JOIN eg_usm_survey_submitted surveysubmitted ON sda.tenantid = surveysubmitted.tenantid AND sda.ward = surveysubmitted.ward AND sda.slumcode = surveysubmitted.slumcode ");
            query.append(" LEFT OUTER JOIN eg_usm_survey_submitted_answer answer ON surveysubmitted.id = answer.surveysubmittedid ");
            query.append(" LEFT OUTER JOIN eg_usm_survey_ticket ticket ON answer.id  = ticket.surveyanswerid ");
        }
		
        if(!ObjectUtils.isEmpty(searchCriteria.getId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sda.id = ?" );
            preparedStmtList.add(searchCriteria.getId());
        }
        
        if(!ObjectUtils.isEmpty(searchCriteria.getUserId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sda.userid = ?");
            preparedStmtList.add(searchCriteria.getUserId());
        }

        if(!ObjectUtils.isEmpty(searchCriteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sda.tenantid = ?");
            preparedStmtList.add(searchCriteria.getTenantId());
        }
        
        if (!ObjectUtils.isEmpty(searchCriteria.getWard())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sda.ward = ?");
			preparedStmtList.add(searchCriteria.getWard());
		}
        
        if (!ObjectUtils.isEmpty(searchCriteria.getSlumCode())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sda.slumcode = ?");
			preparedStmtList.add(searchCriteria.getSlumCode());
		}
		
        if(!ObjectUtils.isEmpty(searchCriteria.getIsActive())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" sda.active = ?");
            preparedStmtList.add(searchCriteria.getIsActive());
        }
        
        if(!ObjectUtils.isEmpty(searchCriteria.getTicketId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ticket.id = ?");
            preparedStmtList.add(searchCriteria.getTicketId());
        }
        
        query.append(" ORDER BY sda.createdtime DESC ");
        return query.toString();
	}
	
	
	private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
		if (preparedStmtList.isEmpty()) {
			query.append(" WHERE ");
		} else {
			query.append(" AND ");
		}
	}
	

}
