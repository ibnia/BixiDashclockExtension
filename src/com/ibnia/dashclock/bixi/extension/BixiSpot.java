package com.ibnia.dashclock.bixi.extension;

public class BixiSpot {

	int id;
	String name;
	double latitude;
	double longitude;
	int nbBikes;
	int nbEmptyDocks;
	boolean installed;
	boolean locked;
	boolean temporary;

	public BixiSpot() {
		// TODO Auto-generated constructor stub
		id = 0;
		name = "";
		latitude = 0;
		longitude = 0;
		nbBikes = 0;
		nbEmptyDocks = 0;
		installed = false;
		locked = false;
		temporary = false;
	}

	public int getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getNbBikes() {
		return nbBikes;
	}

	public int getNbEmptyDocks() {
		return nbEmptyDocks;
	}

	public boolean isInstalled() {
		return installed;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean  isTemporary() {
		return temporary;
	}

	public void setId(int value) {
		this.id = value;
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setLatitude(double value) {
		this.latitude = value;
	}

	public void setLongitude(double value) {
		this.longitude = value;
	}

	public void setNbBikes(int value) {
		this.nbBikes = value;
	}

	public void setNbEmptyDocks(int value) {
		this.nbEmptyDocks = value;
	}

	public void setInstalled(boolean value) {
		this.installed = value;
	}

	public void setLocked(boolean value) {
		this.locked = value;
	}

	public void setTemporary(boolean value) {
		this.temporary = value;
	}
}
