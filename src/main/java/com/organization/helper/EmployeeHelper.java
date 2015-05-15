package com.organization.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LocationType;
import com.organization.adapter.EmployeeAdapter;
import com.organization.domain.PostalAddress;
import com.organization.exception.AddressException;

@Component("employeeHelper")
public class EmployeeHelper {
	
	private static final Logger log = LoggerFactory.getLogger(EmployeeHelper.class);
	private static final GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyB78pL8NOorq8V5SXTGFPsbMKSjffEXUbI");
	
	@Autowired
	private EmployeeAdapter employeeAdapter;
	
	/**
	 * Gets geo information for a given postal address using google geocoding api
	 * @param address the address information
	 * @return GeocodingResult[] an array of GeocodingResult
	 * @throws Exception
	 */
	public Double[] getGeoResult(PostalAddress pa) throws AddressException, Exception {
		log.info("get geoinfo for address={}", pa);
		
		if (pa != null) {
			String address = employeeAdapter.convertPostalAddressToTextAddress(pa);
			return geoCode(address);
		}
		return null;
	}
	
	/**
	 * Gets geo information for a given address using google geocoding api
	 * @param address
	 * @return Double[]
	 * @throws AddressException
	 * @throws Exception
	 */
	public Double[] geoCode(String address) throws AddressException, Exception {
		log.info("geocode address={}", address);
		GeocodingResult[] geoResult = null;
		
		geoResult =  GeocodingApi.geocode(geoContext, address).await();
		// if no matches or more than 1 match throw address exception
		if (geoResult == null
				|| geoResult.length != 1
				//|| geoResult[0].geometry.locationType != LocationType.ROOFTOP
				) {
			throw new AddressException("INVALID_ADDRESS");
		}
		log.info("received geoInfo={}", geoResult[0].geometry.location);
		return new Double[] {geoResult[0].geometry.location.lat, geoResult[0].geometry.location.lng};
	}

}
