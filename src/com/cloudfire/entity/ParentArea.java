/**
 * ÏÂÎç4:00:34
 */
package com.cloudfire.entity;

/**
 * @author cheng
 *2017-6-8
 *ÏÂÎç4:00:34
 */
public class ParentArea {
	 private Integer id;

	    private String code="";

	    private String name="";

	    private String town="";
	    
	    private String parentAreaName="";
	    
	    private String currentId="";
	    
	    private String province;
	    
	    private String city="";
	    
	    
	    
	    
	    

	    public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getCurrentId() {
			return currentId;
		}

		public void setCurrentId(String currentId) {
			this.currentId = currentId;
		}

		

		public String getParentAreaName() {
			return parentAreaName;
		}

		public void setParentAreaName(String parentAreaName) {
			this.parentAreaName = parentAreaName;
		}

		public Integer getId() {
	        return id;
	    }

	    public void setId(Integer id) {
	        this.id = id;
	    }

	    public String getCode() {
	        return code;
	    }

	    public void setCode(String code) {
	        this.code = code == null ? null : code.trim();
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name == null ? null : name.trim();
	    }

		public String getTown() {
			return town;
		}

		public void setTown(String town) {
			this.town = town;
		}

	    
}
