package org.egov.dx.web.models;

import javax.validation.constraints.Size;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("DocDetails")

public class DocDetailsRequest {
	
	@XStreamAlias("DocType")
    private String docType;
	
	@XStreamAlias("FullName")
    private String fullName;
	
	@XStreamAlias("DOB")
    private String dob;
	
	@XStreamAlias("GENDER")
    private String gender;
	
	@XStreamAlias("DigiLockerId")
    private String digiLockerId;
	
	@XStreamAlias("Mobile")
    private String mobile;
	
    @Size(max=64)
    @XStreamAlias("PropertyID")
    private String propertyId;
    
    @Size(max=64)
    @XStreamAlias("MrNumber")
    private String mrNumber;
    
    @Size(max=64)
    @XStreamAlias("LicenseNumber")
    private String licenseNumber;
    
    @Size(max=64)
    @XStreamAlias("ApprovalNumber")
    private String approvalNumber;

	@XStreamAlias("City")
    private String city;

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDigiLockerId() {
		return digiLockerId;
	}

	public void setDigiLockerId(String digiLockerId) {
		this.digiLockerId = digiLockerId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	
	public String getMrNumber() {
		return mrNumber;
	}

	public void setMrNumber(String mrNumber) {
		this.mrNumber = mrNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	
	public String getApprovalNumber() {
		return approvalNumber;
	}

	public void setApprovalNumber(String approvalNumber) {
		this.approvalNumber = approvalNumber;
	}
     
}
