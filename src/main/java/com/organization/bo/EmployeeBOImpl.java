package com.organization.bo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.organization.adapter.EmployeeAdapter;
import com.organization.domain.Employee;
import com.organization.exception.AddressException;
import com.organization.helper.EmployeeHelper;
import com.organization.repository.EmployeeRepository;
import com.organization.service.request.EmployeeRequest;
import com.organization.service.response.EmployeeResponse;
import com.organization.service.response.EmployeeResponses;

@Component("employeeBO")
@Scope("prototype")
public class EmployeeBOImpl implements EmployeeBO {
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeAdapter employeeAdapter;
	
	@Autowired
	private EmployeeHelper employeeHelper;
	
	private static final String default_radius = "20";
	private static final Logger log = LoggerFactory.getLogger(EmployeeBOImpl.class);
	
	public EmployeeResponses getAllEmployees(String lname, String address, String radius) throws AddressException, Exception {
		Double[] latlng = null;
		// convert address to lat and long
		if (address != null) {
			latlng = employeeHelper.geoCode(address);
			radius = (radius != null) ? radius : default_radius;
		}
		
		// get all employees
		List<Employee> empDomainList = employeeRepository.findAll(lname, latlng, radius);
		
		// convert employee list to employee response
		EmployeeResponses empResponseList = null;
		if (empDomainList != null && empDomainList.size() > 0) {
			empResponseList = employeeAdapter.convertToEmployeeResponse(empDomainList);
		} else {
			// throw unknown input exception
		}
		log.info("employeeResponseList={}", empResponseList);
		return empResponseList;
	}

	public EmployeeResponse getEmployeeById(String empId) throws Exception {
		EmployeeResponse employeeResponse = null;
		Employee employee = employeeRepository.findById(empId);
		if (employee != null) {
			employeeResponse = employeeAdapter.convertToEmployeeResponse(employee);
		}
		log.info("employeeResponse={}", employeeResponse);
		return employeeResponse;
	}

	public EmployeeResponse updateEmployee(Long empId, EmployeeRequest employeeRequest) throws AddressException, Exception {
		EmployeeResponse employeeResponse = null;
		Employee employee = null;
		Update updateObject = null;
		Double[] latlng = null;

		if (employeeRequest != null) {
			latlng = employeeHelper.getGeoResult(employeeRequest.getAddress());
			updateObject = employeeAdapter.buildDocument(employeeRequest, latlng);
			employee = employeeRepository.updateEmployee(empId, updateObject);
			if (employee != null && employee.getEmpId() != null) {
				employeeResponse = getEmployeeById(employee.getEmpId().toString());
			}
		}
		log.info("employeeResponse={}", employeeResponse);
		return employeeResponse;
	}
	
	public EmployeeResponse createEmployee(EmployeeRequest employeeRequest) throws AddressException, Exception {
		EmployeeResponse employeeResponse = null;
		Employee employee = null;
		Double[] latlng = null;
		
		if (employeeRequest != null) {
			Long empId = employeeRepository.getNextId();
			log.info("new employeeId={}", empId);

			latlng = employeeHelper.getGeoResult(employeeRequest.getAddress());
			employee = employeeAdapter.buildDocument(empId, employeeRequest, latlng);
			employeeRepository.createEmployee(employee);
			if (employee.get_id() != null) {
				employeeResponse = employeeAdapter.convertToEmployeeResponse(employee);
			}
		}
		
		log.info("employeeResponse={}", employeeResponse);
		return employeeResponse;
	}

	public EmployeeResponse deleteEmployee(Long empId) throws Exception {
		EmployeeResponse employeeResponse = null;
		Employee employee = employeeRepository.deleteEmployee(empId);
		
		if (employee != null) {
			employeeResponse = employeeAdapter.convertToEmployeeResponse(employee);
		}
		log.info("employeeResponse={}", employeeResponse);
		return employeeResponse;
	}

}
