package com.cloudfire.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rain
 *
 */
public class OneKindDevRecord{
	
	private String devTypeId;
	private String devTypeName;
	private List<NFCInfoEntity> list;
	private List<NFCCheakItem> items;
	
	public OneKindDevRecord(String devTypeId, String devTypeName,List<NFCCheakItem> items) {
		super();
		this.devTypeId = devTypeId;
		this.devTypeName = devTypeName;
		this.list = new ArrayList<NFCInfoEntity>();
		this.items=items;
	}
	
	public String getDevTypeId() {
		return devTypeId;
	}
	/**
	 * @param devTypeId
	 */
	public void setDevTypeId(String devTypeId) {
		this.devTypeId = devTypeId;
	}
	public String getDevTypeName() {
		return devTypeName;
	}
	public void setDevTypeName(String devTypeName) {
		this.devTypeName = devTypeName;
	}
	public List<NFCInfoEntity> getList() {
		return list;
	}
	public void setList(List<NFCInfoEntity> list) {
		this.list = list;
	}

	public List<NFCCheakItem> getItems() {
		return items;
	}

	public void setItems(List<NFCCheakItem> items) {
		this.items = items;
	}
}
