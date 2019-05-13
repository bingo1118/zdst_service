package com.cloudfire.entity;

import java.util.Map;

public class EasyIOTCmdEntity {
	
        private Map<String,String> resultParams;//传感器名称及值
        private String devSerial; 
        private String commandId;
        
		public Map<String, String> getResultParams() {
			return resultParams;
		}
		public void setResultParams(Map<String, String> resultParams) {
			this.resultParams = resultParams;
		}
		public String getDevSerial() {
			return devSerial;
		}
		public void setDevSerial(String devSerial) {
			this.devSerial = devSerial;
		}
		public String getCommandId() {
			return commandId;
		}
		public void setCommandId(String commandId) {
			this.commandId = commandId;
		}
	
}
