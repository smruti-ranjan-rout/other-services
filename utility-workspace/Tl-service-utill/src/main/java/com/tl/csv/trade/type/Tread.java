package com.tl.csv.trade.type;

import java.util.ArrayList;
import java.util.List;

public class Tread {
	
	private String code;
    private String name;
    private String uom;
    private String uomDescription;
    private List<ApplicationDocument> applicationDocument;
    private List<Object> verificationDocument=new ArrayList<>();
    private boolean active=true;
    private String type="TL";
    private Object validityPeriod=null;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getUomDescription() {
		return uomDescription;
	}
	public void setUomDescription(String uomDescription) {
		this.uomDescription = uomDescription;
	}
	public List<ApplicationDocument> getApplicationDocument() {
		return applicationDocument;
	}
	public void setApplicationDocument(List<ApplicationDocument> applicationDocument) {
		this.applicationDocument = applicationDocument;
	}
	public List<Object> getVerificationDocument() {
		return verificationDocument;
	}
	public void setVerificationDocument(List<Object> verificationDocument) {
		this.verificationDocument = verificationDocument;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getValidityPeriod() {
		return validityPeriod;
	}
	public void setValidityPeriod(Object validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tread other = (Tread) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


    
    

}
