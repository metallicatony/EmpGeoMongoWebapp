package com.organization.domain;


public class Address {
	private Location loc;
	private PostalAddress postalAddress;
	
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public PostalAddress getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(PostalAddress postalAddress) {
		this.postalAddress = postalAddress;
	}
	@Override
	public String toString() {
		return "Address [loc=" + loc + ", postalAddress=" + postalAddress + "]";
	}

}
