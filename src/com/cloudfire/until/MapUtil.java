package com.cloudfire.until;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.cloudfire.entity.LngLat;

public class MapUtil {

	public static LngLat coordinateTrans(String src, String dst, String longitude, String latitude) {
		LngLat  lnglat = new LngLat();
		String url = "https://restapi.amap.com/v3/assistant/coordinate/convert";
		Map<String,String> map = new HashMap<String,String>();
		map.put("locations", longitude+","+latitude);
		map.put("coordsys",src);
		map.put("output","json");
		map.put("key","22d9821215cee7633138eff827414263");
		String map2 = OneNetHttpMethod.getMap(url, map);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(map2);
			String[] locations =jsonObject.getString("locations").split(",");
			lnglat.setLantitude(Double.parseDouble(locations[1]));
			lnglat.setLongitude(Double.parseDouble(locations[0]));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return lnglat;
		
	}
	
	public static void main(String[] args) {
		System.out.println(coordinateTrans("baidu", "gaode", "23.109441", "113.442055"));
	}
	
}
