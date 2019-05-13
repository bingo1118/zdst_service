package com.cloudfire.dao;

import com.cloudfire.entity.PlaceTypeEntity;
import com.cloudfire.entity.SafetyItemEntity;

public interface SafetyStudyDao {
	public SafetyItemEntity getAllSafetyItem();
	public SafetyItemEntity getAllSafetyRuleItem();
}
