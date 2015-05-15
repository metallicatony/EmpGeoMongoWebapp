package com.organization.service.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.organization.domain.Address;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "employeeResponse", namespace="")
@XmlType(name = "", propOrder = {"employeeId", "firstName","lastName", "salary", "deptName", "address"})
@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
public class EmployeeResponse extends Error {
	
	@XmlElement(name = "employeeId", required = true)
	private Long employeeId;
	
	@XmlElement(name = "firstName", required = true)
	private String firstName;
	
	@XmlElement(name = "lastName", required = true)
	private String lastName;
	
	@XmlElement(name = "salary", required = true)
	private Double salary;
	
	@XmlElement(name = "deptName", required = true)
	private String deptName;
	
	@XmlElement(name = "address", required = true)
	private Address address;
	
	public EmployeeResponse(Error error) {
		super(error.getErrorCode(), error.getErrorName(), error.getErrorDesc());
	}
	
	public EmployeeResponse() {
		
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "EmployeeResponse [employeeId=" + employeeId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", salary=" + salary
				+ ", deptName=" + deptName + ", address=" + address + "]";
	}

}
