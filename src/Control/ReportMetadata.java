package Control;

public class ReportMetadata {
	
	String name,licPackage, date;
	
	

	public ReportMetadata(String name, String licPackage, String date) {
		super();
		this.name = name;
		this.licPackage = licPackage;
		this.date = date;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the licPackage
	 */
	public String getLicPackage() {
		return licPackage;
	}

	/**
	 * @param licPackage the licPackage to set
	 */
	public void setLicPackage(String licPackage) {
		this.licPackage = licPackage;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReportMetadata [name=" + name + ", licPackage=" + licPackage
				+ ", date=" + date + "]";
	}
	
	public String toCSVString() {
		return  name + "," + licPackage
				+ "," + date + ",";
	}
	
	public String toStringNameDate() {
		return  name +  ", " + date;
	}
	
	
	
	

}
