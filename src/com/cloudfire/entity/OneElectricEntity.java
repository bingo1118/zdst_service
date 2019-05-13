package com.cloudfire.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OneElectricEntity {
	 /**
     * Electric : [{"electricTime":"2017-01-09 17:24:51","electricType":6,"electricValue":[{"ElectricThreshold":"242","id":1,"value":"0.00"},{"ElectricThreshold":"","id":2,"value":"0.00"},{"ElectricThreshold":"","id":3,"value":"0.00"}]},{"electricTime":"2017-01-09 17:24:51","electricType":7,"electricValue":[{"ElectricThreshold":"6","id":1,"value":"0.000"},{"ElectricThreshold":"","id":2,"value":"0.000"},{"ElectricThreshold":"","id":3,"value":"0.000"},{"ElectricThreshold":"","id":4,"value":"0.000"}]},{"electricTime":"2017-01-09 17:24:51","electricType":8,"electricValue":[{"ElectricThreshold":"500","id":1,"value":"0.000"}]},{"electricTime":"2017-01-09 17:24:51","electricType":9,"electricValue":[{"ElectricThreshold":"60.00","id":1,"value":"27.96"},{"ElectricThreshold":"60.00","id":2,"value":"28.73"},{"ElectricThreshold":"60.00","id":3,"value":"27.90"},{"ElectricThreshold":"60.00","id":4,"value":"26.93"}]}]
     * error : 获取单个电气设备成功
     * errorCode : 0
     */

    private String error="";
    private int errorCode;
    /**
     * electricTime : 2017-01-09 17:24:51
     * electricType : 6
     * electricValue : [{"ElectricThreshold":"242","id":1,"value":"0.00"},{"ElectricThreshold":"","id":2,"value":"0.00"},{"ElectricThreshold":"","id":3,"value":"0.00"}]
     */
    @JsonProperty("Electric")
    private List<ElectricBean> electric;
//    private ElectricBean[] electric;

//    public ElectricBean[] getElectric() {
//		return electric;
//	}
//
//	public void setElectric(ElectricBean[] electric) {
//		this.electric = electric;
//	}

	public String getError() {
        return error;
    }

    public List<ElectricBean> getElectric() {
		return electric;
	}

	public void setElectric(List<ElectricBean> electric) {
		this.electric = electric;
	}

	public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    

//    public List<ElectricBean> getElectric() {
//		return electric;
//	}

//	public void setElectric(List<ElectricBean> electric) {
//		this.electric = electric;
//	}



	public static class ElectricBean {
        private String electricTime="";
        private int electricType;
        private String electricThreshold="";
        /**
         * ElectricThreshold : 242
         * id : 1
         * value : 0.00
         */

        private List<ElectricValueBean> electricValue;

        public String getElectricTime() {
            return electricTime;
        }

        public void setElectricTime(String electricTime) {
            this.electricTime = electricTime;
        }

        public int getElectricType() {
            return electricType;
        }

        public void setElectricType(int electricType) {
            this.electricType = electricType;
        }

        public String getElectricThreshold() {
			return electricThreshold;
		}

		public void setElectricThreshold(String electricThreshold) {
			this.electricThreshold = electricThreshold;
		}
        
        public List<ElectricValueBean> getElectricValue() {
            return electricValue;
        }

        public void setElectricValue(List<ElectricValueBean> electricValue) {
            this.electricValue = electricValue;
        }

        public static class ElectricValueBean {
        	@JsonProperty("ElectricThreshold")
            private String electricThreshold="";
            private int id;
            private String value="";

			public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
            public String getElectricThreshold() {
    			return electricThreshold;
    		}

    		public void setElectricThreshold(String electricThreshold) {
    			this.electricThreshold = electricThreshold;
    		}
        }
    }
}
