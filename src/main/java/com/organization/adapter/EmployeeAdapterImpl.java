package com.organization.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.organization.domain.Address;
import com.organization.domain.Employee;
import com.organization.domain.Location;
import com.organization.domain.PostalAddress;
import com.organization.service.request.EmployeeRequest;
import com.organization.service.response.EmployeeResponse;
import com.organization.service.response.EmployeeResponses;

@Component("employeeAdapter")
public class EmployeeAdapterImpl implements EmployeeAdapter {
    private static Logger log = LoggerFactory.getLogger(EmployeeAdapterImpl.class);

	public EmployeeResponses convertToEmployeeResponse(List<Employee> employeeList) {
		List<EmployeeResponse> employeeResponseList = null;
		EmployeeResponses employeeResponses = null;
		if (employeeList != null && employeeList.size() > 0) {
			employeeResponseList = new ArrayList<EmployeeResponse>();
			employeeResponses = new EmployeeResponses();
			for (Employee employee: employeeList) {
				EmployeeResponse employeeResponse = convertToEmployeeResponse(employee);
				employeeResponseList.add(employeeResponse);
			}
			employeeResponses.setEmployeeResponses(employeeResponseList);
		}
		return employeeResponses;
	}

	public EmployeeResponse convertToEmployeeResponse(Employee employee) {
		EmployeeResponse employeeResponse = new EmployeeResponse();
		log.info("employee={}", employee);
		employeeResponse.setEmployeeId(employee.getEmpId());
		employeeResponse.setFirstName(employee.getFname());
		employeeResponse.setLastName(employee.getLname());
		employeeResponse.setDeptName(employee.getDeptName());
		employeeResponse.setSalary(employee.getSalary());
		employeeResponse.setAddress(employee.getAddress());
		return employeeResponse;
	}


	public Update buildDocument(EmployeeRequest employeeRequest, Double[] latlng) {
		Update updateObject = new Update();

		if (employeeRequest.getFirstName() != null) {
			updateObject.set("fname", employeeRequest.getFirstName());
		}
		if (employeeRequest.getLastName() != null) {
			updateObject.set("lname", employeeRequest.getLastName());
		}
		if (employeeRequest.getSalary() != null) {
			updateObject.set("salary", employeeRequest.getSalary());
		}
		if (employeeRequest.getDeptName() != null) {
			updateObject.set("deptName", employeeRequest.getDeptName());
		}
		Address address = buildAddress(employeeRequest.getAddress(), latlng);
		if (address != null) {
			updateObject.set("address", address);
		}
		
		return updateObject;
	}
	
	public Employee buildDocument(Long empId, EmployeeRequest employeeRequest, Double[] latlng) {
		Employee employee = new Employee();
		
		if (empId != null) {
			employee.setEmpId(empId);
		}
		
		if (employeeRequest.getFirstName() != null) {
			employee.setFname(employeeRequest.getFirstName());
		}
		if (employeeRequest.getLastName() != null) {
			employee.setLname(employeeRequest.getLastName());
		}
		if (employeeRequest.getSalary() != null) {
			employee.setSalary(employeeRequest.getSalary());
		}
		if (employeeRequest.getDeptName() != null) {
			employee.setDeptName(employeeRequest.getDeptName());
		}
		
		Address address = buildAddress(employeeRequest.getAddress(), latlng);
		if (address != null) {
			employee.setAddress(address);
		}
		
		return employee;
	}
	
	/**
	 * Converts postal address to formatted address text
	 * @param pa the postal address
	 * @return address the formatted address text
	 */
	public String convertPostalAddressToTextAddress(PostalAddress pa) {
		if (pa != null) {
			StringBuilder address = new StringBuilder();
			if (pa.getStreet() != null) {
				address = address.append(pa.getStreet()).append(",");
			}
			
			if (pa.getAptNo() != null) {
				address = address.append(pa.getAptNo()).append(",");
			}
			
			if (pa.getCity() != null) {
				address = address.append(pa.getCity()).append(",");
			}
			
			if (pa.getState() != null) {
				address = address.append(pa.getState()).append(" ");
			}
			
			if (pa.getZip() != null) {
				address = address.append(pa.getZip());
			}
			
			if (pa.getCountry() != null) {
				address = address.append(", ").append(pa.getCountry());
			}
			return address.toString();
		}
		return null;
	}
	
	/**
	 * Builds address of an employee that includes the latitude & longitude
	 * information along with postal address
	 * @param pa
	 *            postal address of employee
	 * @param latlng
	 *            latitude longitude for the address of employee
	 * @return the address object
	 */
	private Address buildAddress(PostalAddress pa, Double[] latlng) {
		Address address = null;
		if (latlng != null && latlng.length > 0) {
			address = new Address();
			Location loc = new Location();
			loc.setType("Point");
			loc.setCoordinates(Arrays.asList(latlng));
			address.setLoc(loc);
			address.setPostalAddress(pa);
		}
		return address;
	}


}
