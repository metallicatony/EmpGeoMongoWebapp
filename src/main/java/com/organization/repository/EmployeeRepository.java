package com.organization.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.organization.domain.Employee;
import com.organization.domain.Sequence;

@Repository("employeeRepository")
public class EmployeeRepository {
	private static final Logger log = LoggerFactory.getLogger(EmployeeRepository.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	/**
	 * Returns all employees 
	 * <br> - gets all employees if nothing was given
	 * <br> - gets all employees with given last name
	 * <br> - gets all employees from within a radius of a given latitude and longitude
	 * @param lname the last name of the employee
	 * @return a list of employees
	 */
	public List<Employee> findAll(String lname, Double[] latlng, String radius) {
		log.info("lname={} latlng={} radius={}", new Object[]{lname, latlng, radius});
		List<Employee> empList = null;
		if (lname == null && latlng == null) {
			// get all employees
			empList = mongoTemplate.findAll(Employee.class);
		} else if (lname != null && latlng == null) {
			// get employees by last name
			empList = mongoTemplate.find(new Query(Criteria.where("lname").is(lname)), Employee.class);
		} else if (latlng != null && lname == null) {
			// get employees that live within a given radius of the given address
			NearQuery nq = NearQuery.near(latlng[1], latlng[0], Metrics.MILES).maxDistance(new Double(radius));
			GeoResults<Employee> empGeoResults = mongoTemplate.geoNear(nq, Employee.class);
			if (empGeoResults != null) {
				empList = new ArrayList<Employee>();
				for (GeoResult<Employee> e : empGeoResults) {
					empList.add(e.getContent());
				}
			}
		} else if (latlng != null && lname != null) {
			// get employees that have a given last name and live within a given radius of the given address
			DBObject geoQuery = buildGeoQuery(latlng, radius);
			DBObject addressLoc = BasicDBObjectBuilder.start().add("address.loc", geoQuery).add("lname", lname).get();
			DBCursor cursor = mongoTemplate.getCollection("employees").find(addressLoc);
			empList = cursor.hasNext() ? new ArrayList<Employee>() : null;
			while (cursor.hasNext()) {
				DBObject empDBObject = (DBObject)cursor.next();
				Employee e = mongoTemplate.getConverter().read(Employee.class, empDBObject);
				empList.add(e);
			}
		}
		return empList;
	}
	
	/**
	 * Builds geo query using latitude, longitude co-ordinates and radius. Given
	 * radius is in miles and needs to be converted into meters.
	 * @param lname
	 * @param latlng
	 * @param radius
	 * @return
	 */
	private DBObject buildGeoQuery(Double[] latlng, String radius) {
		// Mongo uses longitude, latitude combination
		Double[] coordinates = new Double[] {latlng[1], latlng[0]};
		DBObject geometryContent = BasicDBObjectBuilder.start().add("type", "Point")
				.add("coordinates", coordinates).get();
		DBObject nearContent = BasicDBObjectBuilder.start().add("$geometry", geometryContent)
				.add("$maxDistance", (Double.valueOf(radius) * 1609.34)).get();
		DBObject addressLocContent = BasicDBObjectBuilder.start().add("$near", nearContent).get();
		return addressLocContent;
	}
	
	/**
	 * Returns employee with the given employee id
	 * @param empId the id of the employee
	 * @return employee information
	 * @throws Exception
	 */
	public Employee findById(String empId) throws Exception {
		if (empId != null) {
			return mongoTemplate.findOne(new Query(Criteria.where("empId").is(new Long(empId))), Employee.class);
		}
		return null;
	}
	
	/**
	 * Updates the employee with the given information
	 * @param empId the id of the employee
	 * @param updateObject the employee information
	 * @return employee information
	 */
	public Employee updateEmployee(Long empId, Update updateObject) {
		Employee employee = null;
		if (empId != null) {
			employee = mongoTemplate.findAndModify(new Query(Criteria.where("empId").is(empId)), updateObject, Employee.class);
		}
		return employee;
	}
	
	/**
	 * Creates an employee
	 * @param employee information
	 * @return employee information
	 */
	public Employee createEmployee(Employee employee) {
		mongoTemplate.insert(employee);
		log.info("employee={}", employee);
		return employee;
	}
	
	/**
	 * Deletes the employee
	 * @param empId the id of the employee
	 * @return the deleted employee information
	 */
	public Employee deleteEmployee(Long empId) {
		Employee employee = null;
		if (empId != null) {
			employee = mongoTemplate.findAndRemove(new Query(Criteria.where("empId").is(empId)), Employee.class);
		}
		log.info("employee={}", employee);
		return employee;
	}
	
	/**
	 * Gets the next sequence id
	 * @return the sequence id
	 */
	public Long getNextId() {
		// Get object id of the sequence document
		Query queryGet = new Query(Criteria.where("empId").exists(true));
		Sequence sequenceDocGet = mongoTemplate.findOne(queryGet, Sequence.class);
		log.info("objectId={}", sequenceDocGet.get_id());
		
		// Increment emp id of the document fetched above and return the new sequence number
		Sequence sequenceDoc = null;
		Update update = new Update();
		update.inc("empId", 1);
		Query query = new Query(Criteria.where("_id").is(new ObjectId(sequenceDocGet.get_id())));
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);
		sequenceDoc = mongoTemplate.findAndModify(query, update, options, Sequence.class);
		if (sequenceDoc != null) {
			return sequenceDoc.getEmpId();
		}
		return null;
	}

}
