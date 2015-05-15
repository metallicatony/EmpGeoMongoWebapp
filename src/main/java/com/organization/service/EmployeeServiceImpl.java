package com.organization.service;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.organization.adapter.EmployeeAdapter;
import com.organization.bo.EmployeeBO;
import com.organization.exception.AddressException;
import com.organization.service.request.EmployeeRequest;
import com.organization.service.response.EmployeeResponse;
import com.organization.service.response.EmployeeResponses;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    private static Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    
    @Autowired
    private EmployeeBO employeeBO;
    
/*    @Autowired
    private ApplicationContext ctx;*/
    
    @Autowired
    private EmployeeAdapter employeeAdapter;
    
    
	public Response createEmployee(EmployeeRequest employeeRequest) {
		EmployeeResponse employeeResponse = null;
		log.info("received create employee request employeeRequest={}", employeeRequest);
		try {
//			EmployeeBO employeeBO = (EmployeeBO) ctx.getBean("employeeBO");
			employeeResponse = employeeBO.createEmployee(employeeRequest);
		} catch (AddressException e) {
			log.error("address exception received");
			employeeResponse = new EmployeeResponse(
					new com.organization.service.response.Error(400,
							HttpStatus.BAD_REQUEST.name(),
							"given address is not valid"));
			return Response.status(Status.BAD_REQUEST).entity(employeeResponse).build();
		} catch (Exception e) {
			log.error("received exception for employeeRequest={}", employeeRequest, e);
			employeeResponse = new EmployeeResponse(
					new com.organization.service.response.Error(500,
							HttpStatus.INTERNAL_SERVER_ERROR.name(),
							"unexpected internal server error"));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(employeeResponse).build();
		}
		
		log.info("returning getAllEmployees response employeeResponse={}", employeeResponse);
		return Response.ok(employeeResponse).build();
	}
	
	
	public Response getAllEmployees(String lname, String address, String radius) {
		EmployeeResponses employeeResponses = null;
		log.info("received getAllEmployees request");
		try {
//			EmployeeBO employeeBO = (EmployeeBO) ctx.getBean("employeeBO");
			employeeResponses = employeeBO.getAllEmployees(lname, address, radius);
			if (employeeResponses == null) {
				return Response.status(Status.NOT_FOUND).entity(new EmployeeResponses(
					new com.organization.service.response.Error(400,
							HttpStatus.NOT_FOUND.name(),
							"no employees found"))).build();
			}
		}  catch (Exception e) {
			log.error("received exception for employeeRequest with lname={}", lname, e);
			employeeResponses = new EmployeeResponses(
					new com.organization.service.response.Error(500,
							HttpStatus.INTERNAL_SERVER_ERROR.name(),
							"unexpected internal server error"));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(employeeResponses).build();
		}
		
		log.info("returning getAllEmployees response employeeResponses={}", employeeResponses);
		return Response.ok(employeeResponses).build();
	}

	
	public Response getEmployee(String empId) {
		EmployeeResponse employeeResponse = null;
		log.info("received getEmployee request empId={}", empId);
		try {
//			EmployeeBO employeeBO = (EmployeeBO) ctx.getBean("employeeBO");
			employeeResponse = employeeBO.getEmployeeById(empId);
			if (employeeResponse == null) {
				return Response.status(Status.NOT_FOUND).entity(new EmployeeResponses(
					new com.organization.service.response.Error(400,
							HttpStatus.NOT_FOUND.name(),
							"employee with id=" + empId + " not found"))).build();
			}
		} catch (Exception e) {
			log.error("received exception for employeeRequest with empId={}", empId, e);
			employeeResponse = new EmployeeResponse(
					new com.organization.service.response.Error(500,
							HttpStatus.INTERNAL_SERVER_ERROR.name(),
							"unexpected internal server error"));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(employeeResponse).build();
		}
		log.info("returning getEmployee response employeeResponse={}", employeeResponse);
		return Response.ok(employeeResponse).build();
	}
	
	
	public Response updateEmployee(String empId, EmployeeRequest employeeRequest) {
		EmployeeResponse employeeResponse = null;
		log.info("received post Employee request empId={} employeeRequest={}", empId, employeeRequest);
		try {
//			EmployeeBO employeeBO = (EmployeeBO) ctx.getBean("employeeBO");
			employeeResponse = employeeBO.updateEmployee(new Long(empId), employeeRequest);
			if (employeeResponse == null) {
				return Response.status(Status.NOT_FOUND).entity(new EmployeeResponses(
					new com.organization.service.response.Error(400,
							HttpStatus.NOT_FOUND.name(),
							"employee with id=" + empId + " not found"))).build();
			}
		} catch (AddressException e) {
			log.error("address exception received");
			employeeResponse = new EmployeeResponse(
					new com.organization.service.response.Error(400,
							HttpStatus.BAD_REQUEST.name(),
							"given address is not valid"));
			return Response.status(Status.BAD_REQUEST).entity(employeeResponse).build();
		} catch (Exception e) {
			log.error("received exception for employeeRequest={}",
					employeeRequest, e);
			employeeResponse = new EmployeeResponse(
					new com.organization.service.response.Error(500,
							HttpStatus.INTERNAL_SERVER_ERROR.name(),
							"unexpected internal server error"));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(employeeResponse).build();
		}
		log.info("returning getEmployee response employeeResponse={}", employeeResponse);
		return Response.ok(employeeResponse).build();
	}

	
	public Response deleteEmployee(@PathParam("empid") String empId) {
		EmployeeResponse employeeResponse = null;
		log.info("received delete employee empId={}", empId);
		try {
//			EmployeeBO employeeBO = (EmployeeBO) ctx.getBean("employeeBO");
			employeeResponse = employeeBO.deleteEmployee(new Long(empId));
			if (employeeResponse == null) {
				return Response.status(Status.NOT_FOUND).entity(new EmployeeResponses(
					new com.organization.service.response.Error(400,
							HttpStatus.NOT_FOUND.name(),
							"employee with id=" + empId + " not found"))).build();
			}
		} catch (Exception e) {
			log.error("received exception", e);
		}
		log.info("returning deleteEmployee response employeeResponse={}", employeeResponse);
		return Response.ok(employeeResponse).build();
	}
	
}
