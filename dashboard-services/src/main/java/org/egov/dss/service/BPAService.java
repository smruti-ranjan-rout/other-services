package org.egov.dss.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.egov.dss.config.ConfigurationLoader;
import org.egov.dss.constants.DashboardConstants;
import org.egov.dss.model.BpaSearchCriteria;
import org.egov.dss.model.Chart;
import org.egov.dss.model.PayloadDetails;
import org.egov.dss.model.PgrSearchCriteria;
import org.egov.dss.model.PropertySerarchCriteria;
import org.egov.dss.repository.BPARepository;
import org.egov.dss.util.DashboardUtility;
import org.egov.dss.util.DashboardUtils;
import org.egov.dss.web.model.Data;
import org.egov.dss.web.model.Plot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Sets;
import com.jayway.jsonpath.Criteria;

@Service
public class BPAService {

	@Autowired
	private BPARepository bpaRepository;

	@Autowired
	private ConfigurationLoader config;
	
	@Autowired
	private DashboardUtils dashboardUtils;

	public List<Data> totalPermitIssued(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		Integer totalApplication = (Integer) bpaRepository.getTotalPermitsIssued(criteria);
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> slaAchieved(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		Integer totalApplication = (Integer) bpaRepository.getTotalPermitsIssued(criteria);
		Integer slaAchievedAppCount = (Integer) bpaRepository.getSlaAchievedAppCount(criteria);
		return Arrays.asList(Data.builder()
				.headerValue((slaAchievedAppCount.doubleValue() / totalApplication.doubleValue()) * 100).build());
	}
	
	public Integer slaAchievedCount(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		Object slaAchievedAppCountObject = bpaRepository.getSlaAchievedAppCount(criteria);
		if(slaAchievedAppCountObject == null) {
			return 0;
		}
		Integer slaAchievedAppCount = (Integer) slaAchievedAppCountObject;
		return slaAchievedAppCount;
	}

	public BpaSearchCriteria getBpaSearchCriteria(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = new BpaSearchCriteria();

		if (StringUtils.hasText(payloadDetails.getModulelevel())) {
			if (payloadDetails.getModulelevel().equalsIgnoreCase(DashboardConstants.MODULE_LEVEL_OBPS))
				criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		}

		if (StringUtils.hasText(payloadDetails.getTenantid())) {
			criteria.setTenantIds(Sets.newHashSet(payloadDetails.getTenantid()));
		}

		if (payloadDetails.getStartdate() != null && payloadDetails.getStartdate() != 0) {
			criteria.setFromDate(payloadDetails.getStartdate());
		}

		if (payloadDetails.getEnddate() != null && payloadDetails.getEnddate() != 0) {
			criteria.setToDate(payloadDetails.getEnddate());
		}

		return criteria;
	}

	public List<Data> totalApplicationsReceived(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		Integer totalApplication = (Integer) bpaRepository.totalApplicationsReceived(criteria);
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> totalApplicationsRejected(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_REJECTED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		Integer totalApplication = (Integer) bpaRepository.totalApplicationsRejected(criteria);
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> totalApplicationsPending(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUS_TOTAL_APPLICATIONS_PENDING));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		criteria.setFromDate(null);
		criteria.setDeleteStatus(DashboardConstants.STATUS_DELETED);
		Integer totalApplication = (Integer) bpaRepository.totalApplicationsPending(criteria);
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> avgDaysToIssuePermit(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		BigDecimal totalApplication = (BigDecimal) bpaRepository.getAvgDaysToIssuePermit(criteria);// change it
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> minDaysToIssuePermit(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		Integer totalApplication = (Integer) bpaRepository.getMinDaysToIssuePermit(criteria);
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> maxDaysToIssuePermit(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		Integer totalApplication = (Integer) bpaRepository.getMaxDaysToIssuePermit(criteria);
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> slaCompliancePermit(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		criteria.setSlaThreshold(config.getSlaBpaPermitsThreshold());
		criteria.setRiskType(DashboardConstants.BPA_RISK_TYPE_LOW);
		List<String> sparitUlbs = DashboardUtility.getSystemProperties().getSparitulbs();
		Integer totalApplication = 0;
		if (!sparitUlbs.contains(payloadDetails.getTenantid())) {
			totalApplication = (Integer) bpaRepository.getSlaCompliancePermit(criteria);
		}
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> slaComplianceOtherThanLowRisk(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(
				Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		criteria.setSlaThreshold(config.getSlaBpaOtherThanLowRiskThreshold());
		criteria.setRiskType(DashboardConstants.BPA_RISK_TYPE_HIGH);
		List<String> sparitUlbs = DashboardUtility.getSystemProperties().getSparitulbs();
		Integer totalApplication = 0;
		if (!sparitUlbs.contains(payloadDetails.getTenantid())) {
			totalApplication = (Integer) bpaRepository.getSlaComplianceOtherThanLowRisk(criteria);
		}
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> slaCompliancePreApprovedPlan(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.BUSINESS_SERICE_BPA6));
		criteria.setSlaThreshold(config.getSlaBpaPreApprovedPlanThreshold());
		List<String> sparitUlbs = DashboardUtility.getSystemProperties().getSparitulbs();
		Integer totalApplication = 0;
		if (!sparitUlbs.contains(payloadDetails.getTenantid())) {
			totalApplication = (Integer) bpaRepository.getSlaCompliancePreApprovedPlan(criteria);
		}
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	public List<Data> slaComplianceBuildingPermit(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_SLA_COMPLIANCE_BUILDING_PERMIT_STATUS));
		criteria.setSlaThreshold(config.getSlaBpaBuildingPermitThreshold());
		List<String> sparitUlbs = DashboardUtility.getSystemProperties().getSparitulbs();
		Integer totalApplication = 0;
		if (sparitUlbs.contains(payloadDetails.getTenantid())) {
			totalApplication = bpaRepository.getSlaComplianceBuildingPermit(criteria);
		}
		return Arrays.asList(Data.builder().headerValue(totalApplication).build());
	}

	private List<Chart> mapTenantsForPerformanceRate(HashMap<String, Long> numeratorMap,
			HashMap<String, Long> denominatorMap) {
		List<Chart> percentList = new ArrayList();
		numeratorMap.entrySet().stream().forEach(item -> {
			Long numerator = item.getValue();
			Long denominator = denominatorMap.get(item.getKey());
			BigDecimal percent = new BigDecimal(numerator * 100).divide(new BigDecimal(denominator), 2,
					RoundingMode.HALF_EVEN);
			percentList.add(Chart.builder().name(item.getKey()).value(percent).build());
		});
		return percentList;
	}

	public List<Data> topUlbByPerformance(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		List<Data> response = new ArrayList();
		HashMap<String, BigDecimal> tenantWiseBpaAvgDaysPermitIssue = getTenantWiseAvgDaysToIssuePermit(payloadDetails);
		// Sort the HashMap in ascending order
		if (!CollectionUtils.isEmpty(tenantWiseBpaAvgDaysPermitIssue)) {
			Map<String, BigDecimal> tenantWiseSorted = tenantWiseBpaAvgDaysPermitIssue.entrySet().parallelStream()
					.sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
							Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			
			int Rank = 0;
			for (Entry<String, BigDecimal> obj : tenantWiseSorted.entrySet()) {
				Rank++;
				response.add(
						Data.builder().headerName("Rank").headerValue(Rank)
								.plots(Arrays.asList(Plot.builder().label("AVERAGE_DAYS").name(obj.getKey())
										.value(obj.getValue()).symbol("number").build()))
								.headerSymbol("number").build());
			}
			;
		} else {
			response.add(
					Data.builder().headerName("Rank").headerValue(BigDecimal.ZERO)
							.plots(Arrays.asList(Plot.builder().label("AVERAGE_DAYS")
									.name(String.valueOf(payloadDetails.getTenantid())).value(BigDecimal.ZERO)
									.symbol("number").build()))
							.headerSymbol("number").build());
		}

		return response;
	}

	public List<Data> bottomUlbByPerformance(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		List<Data> response = new ArrayList();
		HashMap<String, BigDecimal> tenantWiseBpaAvgDaysPermitIssue = getTenantWiseAvgDaysToIssuePermit(payloadDetails);
		if (!CollectionUtils.isEmpty(tenantWiseBpaAvgDaysPermitIssue)) {
			Map<String, BigDecimal> tenantWiseSorted = tenantWiseBpaAvgDaysPermitIssue.entrySet().parallelStream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
							Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			int Rank = tenantWiseSorted.size();
			for (Entry<String, BigDecimal> obj : tenantWiseSorted.entrySet()) {
				response.add(
						Data.builder().headerName("Rank").headerValue(Rank)
								.plots(Arrays.asList(Plot.builder().label("AVERAGE_DAYS").name(obj.getKey())
										.value(obj.getValue()).symbol("number").build()))
								.headerSymbol("number").build());
				Rank--;
			}
			;
		} else {
			response.add(
					Data.builder().headerName("Rank").headerValue(BigDecimal.ZERO)
							.plots(Arrays.asList(Plot.builder().label("AVERAGE_DAYS")
									.name(String.valueOf(payloadDetails.getTenantid())).value(BigDecimal.ZERO)
									.symbol("number").build()))
							.headerSymbol("number").build());
		}

		return response;
	}

	public List<Data> permitsAndOcIssuedAndOcSubmitted(PayloadDetails payloadDetails) {
		List<Data> response = new ArrayList<>();
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_SERVICES));
		if (!Sets.newHashSet(DashboardConstants.TIME_INTERVAL).contains(payloadDetails.getTimeinterval())) {
			criteria.setFromDate(dashboardUtils.getStartDateGmt(String.valueOf(payloadDetails.getTimeinterval())));
		}
		LinkedHashMap<String,Long> monthYearPermit= bpaRepository.getMonthYearData(criteria);
		LinkedHashMap<String,Long> monthYearOCIssued= bpaRepository.getMonthYearData(criteria);
		LinkedHashMap<String,Long> monthYearOCSubmitted= bpaRepository.getMonthYearData(criteria);


		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		List<Chart> totalPermitsIssuedMonthWise = bpaRepository.getTotalPermitsIssuedVsTotalOcIssuedVsTotalOcSubmitted(criteria);
		
		List<Plot> plotsForTotalPermitsIssuedMonthWise = extractedMonthYearData(monthYearPermit,
				totalPermitsIssuedMonthWise);
		
		Long totalPermitsIssued = monthYearPermit.values().stream().mapToLong(Long::longValue).sum();
		response.add(Data.builder().headerName("TotalPermitIssued").headerValue(totalPermitsIssued).plots(plotsForTotalPermitsIssuedMonthWise).build());
		

		
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_OC_BUSINESS_SERVICES));
		List<Chart> totalOcIssuedMonthWise = bpaRepository.getTotalPermitsIssuedVsTotalOcIssuedVsTotalOcSubmitted(criteria);
		
		List<Plot> plotsForTotalOcIssuedMonthWise = extractedMonthYearData(monthYearOCIssued,
				totalOcIssuedMonthWise);
		
		
		Long totalOcIssued = monthYearOCIssued.values().stream().mapToLong(Long::longValue).sum();
		response.add(Data.builder().headerName("TotalOCissued").headerValue(totalOcIssued).plots(plotsForTotalOcIssuedMonthWise).build());

		

		
		criteria.setStatus(null);
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_OC_BUSINESS_SERVICES));
		List<Chart> totalOcSubmittedMonthWise = bpaRepository.getTotalPermitsIssuedVsTotalOcIssuedVsTotalOcSubmitted(criteria);
		
		List<Plot> plotsForTotalOcSubmittedMonthWise = extractedMonthYearData(monthYearOCSubmitted,
				totalOcSubmittedMonthWise);
		
		
		Long totalOcSubmitted = monthYearOCSubmitted.values().stream().mapToLong(Long::longValue).sum();
		response.add(Data.builder().headerName("TotalOCSubmitted").headerValue(totalOcSubmitted).plots(plotsForTotalOcSubmittedMonthWise).build());


		return response;		

	}

	private List<Plot> extractedMonthYearData(LinkedHashMap<String, Long> monthYearPermit,
			List<Chart> totalPermitsIssuedMonthWise) {
		totalPermitsIssuedMonthWise.forEach(item -> {
			if(monthYearPermit.containsKey(item.getName())) {
				BigDecimal value =  item.getValue();
				monthYearPermit.replace(item.getName(), value.longValue());
			}
		});
		List<Plot> plotsForTotalPermitsIssuedMonthWise = new ArrayList();
		monthYearPermit.forEach((key,value) ->{
			plotsForTotalPermitsIssuedMonthWise.add(Plot.builder().name(key).value(new BigDecimal(value)).symbol("number").build());
		});
		return plotsForTotalPermitsIssuedMonthWise;
	}
	
	private Long extractDataForChart(List<Chart> items, List<Plot> plots, Long total) {
		for (Chart item : items) {
			plots.add(Plot.builder().name(item.getName()).value(item.getValue()).symbol("number").build());
			total = total + Long.valueOf(String.valueOf(item.getValue()));
		}
		return total ;
	}
	
	public List<Data> totalOcApplicationsReceived(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        Integer totalApplication = (Integer) bpaRepository.totalApplicationsReceived(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> totalOcIssued(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        Integer totalApplication = (Integer) bpaRepository.getTotalPermitsIssued(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> totalOcRejected(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_REJECTED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        Integer totalApplication = (Integer) bpaRepository.totalApplicationsRejected(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> totalOcPending(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUS_TOTAL_APPLICATIONS_PENDING));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        Integer totalApplication = (Integer) bpaRepository.totalApplicationsPending(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> avgDaysToIssueOc(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        BigDecimal totalApplication = (BigDecimal) bpaRepository.getAvgDaysToIssuePermit(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> minDaysToIssueOc(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        Integer totalApplication = (Integer) bpaRepository.getMinDaysToIssuePermit(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> maxDaysToIssueOc(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        Integer totalApplication = (Integer) bpaRepository.getMaxDaysToIssuePermit(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> slaComplianceOc(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        criteria.setSlaThreshold(config.getSlaOcPermitThreshold());
        Integer totalApplication = (Integer) bpaRepository.getSlaCompliancePermit(criteria);
        return Arrays.asList(Data.builder().headerValue(totalApplication).build());
    }
    
    public List<Data> serviceReport(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        HashMap<String, BigDecimal> tenantWiseBpaApplicationSubmitted = getTenantWiseBpaApplication(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseBpaPermitIssued = getTenantWisePermitIssued(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseBpaPendingApplication = getTenantWiseApplicationPending(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseBpaAvgDaysPermitIssue = getTenantWiseAvgDaysToIssuePermit(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseBpaSlaCompliance = getTenantWiseSlaCompliancePermit(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseOcApplicationSubmitted = getTenantWiseOcApplication(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseOcPendingApplication = getTenantWiseOcApplicationPending(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseOcIssued = getTenantWiseOcIssue(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseOcAvgDaysPermitIssue = getTenantWiseAvgDaysToOcIssue(payloadDetails);
        HashMap<String, BigDecimal> tenantWiseOcSlaCompliance = getTenantWiseOcSlaCompliance(payloadDetails);
        
        List<Data> response = new ArrayList<>();
        int serialNumber = 1;

        for (HashMap.Entry<String, BigDecimal> tenantWiseBpaApplication : tenantWiseBpaApplicationSubmitted.entrySet()) {
            List<Plot> plots = new ArrayList();
            plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

            plots.add(Plot.builder().name("ULBs").label(tenantWiseBpaApplication.getKey().toString()).symbol("text")
                    .build());

            plots.add(Plot.builder().name("Total Applications Submitted").value(tenantWiseBpaApplication.getValue() == null ? BigDecimal.ZERO : tenantWiseBpaApplication.getValue())
                    .symbol("number").build());

            plots.add(Plot.builder().name("Total Permits Issued")
                    .value(tenantWiseBpaPermitIssued.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseBpaPermitIssued.get(tenantWiseBpaApplication.getKey())).symbol("number").build());

            plots.add(Plot.builder().name("Total BPA Application Pending")
                    .value(tenantWiseBpaPendingApplication.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseBpaPendingApplication.get(tenantWiseBpaApplication.getKey())).symbol("number").build());

            plots.add(Plot.builder().name("Average days to issue Permit")
                    .value(tenantWiseBpaAvgDaysPermitIssue.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseBpaAvgDaysPermitIssue.get(tenantWiseBpaApplication.getKey()) ).symbol("number").build());

            plots.add(Plot.builder().name("SLA Compliance Permit")
                    .value(tenantWiseBpaSlaCompliance.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseBpaSlaCompliance.get(tenantWiseBpaApplication.getKey())).symbol("number").build());
            
            plots.add(Plot.builder().name("Total OC Submitted")
                    .value(tenantWiseOcApplicationSubmitted.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseOcApplicationSubmitted.get(tenantWiseBpaApplication.getKey())).symbol("number").build());
            
            plots.add(Plot.builder().name("Total OC Application Pending")
                    .value(tenantWiseOcPendingApplication.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseOcPendingApplication.get(tenantWiseBpaApplication.getKey())).symbol("number").build());
            
            plots.add(Plot.builder().name("Total OC Issued")
                    .value(tenantWiseOcIssued.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseOcIssued.get(tenantWiseBpaApplication.getKey())).symbol("number").build());
            
            plots.add(Plot.builder().name("Average days to issue OC")
                    .value(tenantWiseOcAvgDaysPermitIssue.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseOcAvgDaysPermitIssue.get(tenantWiseBpaApplication.getKey())).symbol("number").build());
            
            plots.add(Plot.builder().name("SLA Compliance OC")
                    .value(tenantWiseOcSlaCompliance.get(tenantWiseBpaApplication.getKey()) == null ? BigDecimal.ZERO : tenantWiseOcSlaCompliance.get(tenantWiseBpaApplication.getKey())).symbol("number").build());

            response.add(Data.builder().headerName(tenantWiseBpaApplication.getKey()).plots(plots)
                    .headerValue(serialNumber).headerName(tenantWiseBpaApplication.getKey()).build());

            serialNumber++;

        }
        
		if (CollectionUtils.isEmpty(response)) {
			serialNumber++;
			List<Plot> plots = new ArrayList();
			plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

			plots.add(Plot.builder().name("ULBs").label(payloadDetails.getTenantid()).symbol("text").build());

			plots.add(Plot.builder().name("Total Applications Submitted").value(BigDecimal.ZERO).symbol("number")
					.build());

			plots.add(Plot.builder().name("Total Permits Issued").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("Total BPA Application Pending").value(BigDecimal.ZERO).symbol("number")
					.build());

			plots.add(Plot.builder().name("Average days to issue Permit").value(BigDecimal.ZERO).symbol("number")
					.build());

			plots.add(Plot.builder().name("SLA Compliance Permit").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("Total OC Submitted").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("Total OC Application Pending").value(BigDecimal.ZERO).symbol("number")
					.build());

			plots.add(Plot.builder().name("Total OC Issued").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("Average days to issue OC").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("SLA Compliance OC").value(BigDecimal.ZERO).symbol("number").build());

			response.add(Data.builder().headerName(payloadDetails.getTenantid()).plots(plots).headerValue(serialNumber)
					.build());

		}

        return response;
    }
    
    public HashMap<String,BigDecimal> getTenantWiseBpaApplication(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseBpaTotalApplication(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWisePermitIssued(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseBpaPermitIssued(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseApplicationPending(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUS_TOTAL_APPLICATIONS_PENDING));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        criteria.setFromDate(null);
        criteria.setDeleteStatus(DashboardConstants.STATUS_DELETED);
        return bpaRepository.getTenantWiseBpaPendingApplication(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseAvgDaysToIssuePermit(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseAvgDaysPermitIssued(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseSlaCompliancePermit(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        criteria.setSlaThreshold(config.getSlaBpaPermitsThreshold());
        return bpaRepository.getTenantWiseBpaPermitIssued(criteria);
    }
   
    public HashMap<String,BigDecimal> getTenantWiseOcApplication(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseBpaTotalApplication(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseOcIssue(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseBpaTotalApplication(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseOcApplicationPending(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUS_TOTAL_APPLICATIONS_PENDING));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseBpaTotalApplication(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseAvgDaysToOcIssue(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        return bpaRepository.getTenantWiseAvgDaysPermitIssued(criteria);
     }
    
    public HashMap<String,BigDecimal> getTenantWiseOcSlaCompliance(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_OC_BUSINESS_SERVICES));
        criteria.setSlaThreshold(config.getSlaOcPermitThreshold());
        return bpaRepository.getTenantWiseBpaTotalApplication(criteria);
    }

	public HashMap<String, Long> totalApplicationsTenantWise(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		criteria.setStatus(null);
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
		HashMap<String, Long> tenantWiseApplicationsReceivedList = bpaRepository
				.getTenantWiseApplicationsReceivedList(criteria);
		return tenantWiseApplicationsReceivedList;
	}

	public HashMap<String, Long> totalBPAOcApplicationsTenantWise(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_OC_BUSINESS_SERVICES));
		criteria.setStatus(null);
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
		HashMap<String, Long> tenantWiseApplicationsReceivedList = bpaRepository
				.getTenantWiseApplicationsReceivedList(criteria);
		return tenantWiseApplicationsReceivedList;
	}

	public HashMap<String, Long> bpaTotalApplicationsTenantWise(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
		HashMap<String, Long> tenantWiseApplicationsReceivedList = bpaRepository
				.getTenantWiseApplicationsReceivedList(criteria);
		return tenantWiseApplicationsReceivedList;
	}

	public HashMap<String, Long> bpaOcTotalApplicationsTenantWise(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
		criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
		criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
		HashMap<String, Long> tenantWiseApplicationsReceivedList = bpaRepository
				.getTenantWiseApplicationsReceivedList(criteria);
		return tenantWiseApplicationsReceivedList;
	}
	
	public List<Data> obpsServiceSummary(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        HashMap<String, BigDecimal> totalAppicationReceived = getTotalApplicationReceivedByService(payloadDetails);
        HashMap<String, BigDecimal> totalAppicationApproved = getTotalApplicationApprovedByService(payloadDetails);
        HashMap<String, BigDecimal> avgDaysToIssuePermit =    getAvgDaysToIssuePermitByServiceType(payloadDetails);
        
        HashMap<String, BigDecimal> concatedHashMap = new HashMap<>(totalAppicationReceived);
        concatedHashMap.putAll(totalAppicationApproved);
        concatedHashMap.putAll(avgDaysToIssuePermit);
                
        List<Data> response = new ArrayList<>();
        int serialNumber = 1;

        for (HashMap.Entry<String, BigDecimal> totalApplication : concatedHashMap.entrySet()) {
            List<Plot> plots = new ArrayList();
            plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

            plots.add(Plot.builder().name("Service").label(totalApplication.getKey().toString()).symbol("text")
                    .build());

			plots.add(Plot.builder().name("Application Received")
					.value(totalAppicationReceived.get(totalApplication.getKey()) == null ? BigDecimal.ZERO
							: totalAppicationReceived.get(totalApplication.getKey()))
					.symbol("number").build());

			plots.add(Plot.builder().name("Application Approved")
					.value(totalAppicationApproved.get(totalApplication.getKey()) == null ? BigDecimal.ZERO
							: totalAppicationApproved.get(totalApplication.getKey()))
					.symbol("number").build());
			
			plots.add(Plot.builder().name("Avg. Days to Issue Permit")
					.value(avgDaysToIssuePermit.get(totalApplication.getKey()) == null ? BigDecimal.ZERO
							: avgDaysToIssuePermit.get(totalApplication.getKey()))
					.symbol("number").build());

			response.add(Data.builder().headerName(totalApplication.getKey()).plots(plots).headerValue(serialNumber)
					.build());

            serialNumber++;

        }
        
        if (CollectionUtils.isEmpty(response)) {
			serialNumber++;
			List<Plot> plots = new ArrayList();
			plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

			plots.add(Plot.builder().name("Service").label(payloadDetails.getTenantid()).symbol("text").build());

			plots.add(Plot.builder().name("Application Received").value(BigDecimal.ZERO).symbol("number")
					.build());

			plots.add(Plot.builder().name("Application Approved").value(BigDecimal.ZERO).symbol("number").build());
			
			plots.add(Plot.builder().name("Avg. Days to Issue Permit").value(BigDecimal.ZERO).symbol("number").build());

			response.add(Data.builder().headerName(payloadDetails.getTenantid()).plots(plots).headerValue(serialNumber)
					.build());

		}

         return response;
         
	}
	
	public HashMap<String,BigDecimal> getTotalApplicationReceivedByService(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatusNotIn(Sets.newHashSet(DashboardConstants.OBPS_REJECTED_STATUSES));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        return bpaRepository.getTotalApplicationByServiceType(criteria);
     }
	
	public HashMap<String,BigDecimal> getTotalApplicationApprovedByService(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        return bpaRepository.getApprovedApplicationByServiceType(criteria);
     }
	
	public HashMap<String,BigDecimal> getAvgDaysToIssuePermitByServiceType(PayloadDetails payloadDetails) {
        BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
        criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
        criteria.setStatus(Sets.newHashSet(DashboardConstants.STATUS_APPROVED));
        criteria.setBusinessServices(Sets.newHashSet(DashboardConstants.OBPS_ALL_BUSINESS_SERVICES));
        return bpaRepository.getAvgDaysToIssuePermitByServiceType(criteria);
     }
	
	public List<Data> topPerformingUlbsTable(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		List<Data> response = new ArrayList();
		HashMap<String, BigDecimal> tenantWiseBpaAvgDaysPermitIssue = getTenantWiseAvgDaysToIssuePermit(payloadDetails);
		int serialNumber = 1;
		// Sort the HashMap in ascending order
		Map<String, BigDecimal> tenantWiseSorted = tenantWiseBpaAvgDaysPermitIssue.entrySet().parallelStream()
				.sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		for (HashMap.Entry<String, BigDecimal> totalApplication : tenantWiseSorted.entrySet()) {
            List<Plot> plots = new ArrayList();
            plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

            plots.add(Plot.builder().name("Ulb").label(totalApplication.getKey().toString()).symbol("text")
                    .build());

			plots.add(Plot.builder().name("Rank")
					.value(BigDecimal.valueOf(serialNumber))
					.symbol("number").build());

			plots.add(Plot.builder().name("Avg. Days to Issue Permit")
					.value(totalApplication.getValue() == null ? BigDecimal.ZERO
							: totalApplication.getValue().setScale(0, BigDecimal.ROUND_UP))
					.symbol("number").build());

			response.add(Data.builder().headerName(totalApplication.getKey()).plots(plots).headerValue(serialNumber)
					.build());

            serialNumber++;

        }
		
		if (CollectionUtils.isEmpty(response)) {
			serialNumber++;
			List<Plot> plots = new ArrayList();
			plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

			plots.add(Plot.builder().name("Ulb").label(payloadDetails.getTenantid()).symbol("text").build());

			plots.add(Plot.builder().name("Rank").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("Avg. Days to Issue Permit").value(BigDecimal.ZERO).symbol("number").build());

			response.add(Data.builder().headerName(payloadDetails.getTenantid()).plots(plots).headerValue(serialNumber)
					.build());

		}

		return response;
	}
	
	public List<Data> bottomPerformingUlbsTable(PayloadDetails payloadDetails) {
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		List<Data> response = new ArrayList();
		HashMap<String, BigDecimal> tenantWiseBpaAvgDaysPermitIssue = getTenantWiseAvgDaysToIssuePermit(payloadDetails);
		int serialNumber = 1;		
					
		Map<String, BigDecimal> tenantWiseSorted = tenantWiseBpaAvgDaysPermitIssue.entrySet().parallelStream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) // Sort in descending order
		        .collect(Collectors.toMap(
		                Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		for (HashMap.Entry<String, BigDecimal> totalApplication : tenantWiseSorted.entrySet()) {
            List<Plot> plots = new ArrayList();
            plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

            plots.add(Plot.builder().name("Ulb").label(totalApplication.getKey().toString()).symbol("text")
                    .build());

			plots.add(Plot.builder().name("Rank")
					.value(BigDecimal.valueOf(serialNumber))
					.symbol("number").build());

			plots.add(Plot.builder().name("Avg. Days to Issue Permit")
					.value(totalApplication.getValue() == null ? BigDecimal.ZERO
							: totalApplication.getValue().setScale(0, BigDecimal.ROUND_UP))
					.symbol("number").build());

			response.add(Data.builder().headerName(totalApplication.getKey()).plots(plots).headerValue(serialNumber)
					.build());
           
            serialNumber++;

        }
		
		if (CollectionUtils.isEmpty(response)) {
			serialNumber++;
			List<Plot> plots = new ArrayList();
			plots.add(Plot.builder().name("S.N.").label(String.valueOf(serialNumber)).symbol("text").build());

			plots.add(Plot.builder().name("Ulb").label(payloadDetails.getTenantid()).symbol("text").build());

			plots.add(Plot.builder().name("Rank").value(BigDecimal.ZERO).symbol("number").build());

			plots.add(Plot.builder().name("Avg. Days to Issue Permit").value(BigDecimal.ZERO).symbol("number").build());

			response.add(Data.builder().headerName(payloadDetails.getTenantid()).plots(plots).headerValue(serialNumber)
					.build());

			}

		return response;
	}

	public List<Data> obpsApplicationsPendingBreakdown(PayloadDetails payloadDetails) {
		
		BpaSearchCriteria criteria = getBpaSearchCriteria(payloadDetails);
		criteria.setExcludedTenantId(DashboardConstants.TESTING_TENANT);
		criteria.setDeleteStatus(DashboardConstants.STATUS_DELETED);
		List<HashMap<String, Object>> obpsApplicationPending = bpaRepository.getApplicationsBreakdown(criteria);
		List<Data> response = new ArrayList();
		int serailNumber = 0;
		for (HashMap<String, Object> obpsApplication : obpsApplicationPending) {
			serailNumber++;
			String tenantIdStyled = String.valueOf(obpsApplication.get("ulb"));
			tenantIdStyled = tenantIdStyled.substring(0, 1).toUpperCase() + tenantIdStyled.substring(1).toLowerCase();
			List<Plot> row = new ArrayList<>();
			row.add(Plot.builder().label(String.valueOf(serailNumber)).name("S.N.").symbol("text").build());
			row.add(Plot.builder().label(tenantIdStyled).name("ULBs").symbol("text").build());

			row.add(Plot.builder().name("Pending At Doc Verification").value(new BigDecimal(String.valueOf(obpsApplication.get("pendingdocverif"))))
					.symbol("number").build());
			row.add(Plot.builder().name("Pending At Field Inspection").value(new BigDecimal(String.valueOf(obpsApplication.get("pendingfieldinspection"))))
					.symbol("number").build());
			row.add(Plot.builder().name("Pending At Planning Assistance").value(new BigDecimal(String.valueOf(obpsApplication.get("pendingatplanningassistant"))))
					.symbol("number").build());
			row.add(Plot.builder().name("Pending Approval")
					.value(new BigDecimal(String.valueOf(obpsApplication.get("pendingatplanningofficer")))).symbol("number")
					.build());
			row.add(Plot.builder().name("Pending At Planning Member").value(new BigDecimal(String.valueOf(obpsApplication.get("pendingatplanningmember"))))
					.symbol("number").build());
			row.add(Plot.builder().name("Pending At DPBP Committee")
					.value(new BigDecimal(String.valueOf(obpsApplication.get("pendingatdpbp")))).symbol("number").build());
			row.add(Plot.builder().name("Pending For Citizen Action")
					.value(new BigDecimal(String.valueOf(obpsApplication.get("pendingforcitizenaction")))).symbol("number")
					.build());
			row.add(Plot.builder().name("Pending Sanction Fee Payment")
					.value(new BigDecimal(String.valueOf(obpsApplication.get("pendingsancfeepayment")))).symbol("number")
					.build());
			row.add(Plot.builder().name("Pending At Accrediated Person")
					.value(new BigDecimal(String.valueOf(obpsApplication.get("pendingataccrediatedofficer")))).symbol("number")
					.build());
			row.add(Plot.builder().name("Total Applications")
					.value(new BigDecimal(String.valueOf(obpsApplication.get("totalapplicationreciceved")))).symbol("number")
					.build());

			response.add(Data.builder().headerName(tenantIdStyled).headerValue(serailNumber).plots(row).insight(null)
					.build());
		}
		
		if (CollectionUtils.isEmpty(response)) {
			serailNumber++;
			List<Plot> row = new ArrayList<>();
			row.add(Plot.builder().label(String.valueOf(serailNumber)).name("S.N.").symbol("text").build());
			row.add(Plot.builder().label(payloadDetails.getTenantid()).name("ULBs").symbol("text").build());
            row.add(Plot.builder().name("Pending At Doc Verification").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending At Field Inspection").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending At Planning Assistance").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending Approval").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending At Planning Member").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending At DPBP Committee").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending For Citizen Action").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending Sanction Fee Payment").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Pending At Accrediated Person").value(BigDecimal.ZERO).symbol("number").build());
			row.add(Plot.builder().name("Total Applications").value(BigDecimal.ZERO).symbol("number").build());
			response.add(Data.builder().headerName(payloadDetails.getTenantid()).headerValue(serailNumber).plots(row)
					.insight(null).build());
		}

		return response;
	}

}
