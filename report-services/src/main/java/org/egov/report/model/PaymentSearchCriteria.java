package org.egov.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentSearchCriteria {


    private Set<String> ids;

    private Set<String> billIds;

    private String tenantId;

    private Set<String> tenantIds;

    private Set<String> receiptNumbers;

    private Set<String> status;

    private Set<String> instrumentStatus;

    private Set<String> paymentModes;

    private List<String> payerIds;

    private Set<String> consumerCodes;
    
    private Set<String> businessServices;
    
    private String transactionNumber;

    private String mobileNumber;

    private Long fromDate;

    private Long toDate;

    private Integer offset;

    private Integer limit;

}
