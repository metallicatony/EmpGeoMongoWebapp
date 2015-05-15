package com.organization.adapter;

import java.util.List;

import org.springframework.data.mongodb.core.query.Update;

import com.organization.domain.Employee;
import com.organization.domain.PostalAddress;
import com.organization.service.request.EmployeeRequest;
import com.organization.service.response.EmployeeResponse;
import com.organization.service.response.EmployeeResponses;

public interface EmployeeAdapter {
	public EmployeeResponses convertToEmployeeResponse(List<Employee> employee);
	public EmployeeResponse convertToEmployeeResponse(Employee employee);
	public String convertPostalAddressToTextAddress(PostalAddress pa);
	public Update buildDocument(EmployeeRequest employeeRequest, Double[] latlng);
	public Employee buildDocument(Long empId, EmployeeRequest employeeRequest, Double[] latlng);
}
