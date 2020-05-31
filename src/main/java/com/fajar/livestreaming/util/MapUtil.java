package com.fajar.livestreaming.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapUtil {

	public static boolean objectEquals(Object object, Object... objects) {

		for (Object object2 : objects) {
			if (object.equals(object2)) {
				return true;
			}
		}

		return false;
	}
	
	public static List mapToList(Map map) {
		
		List result = new ArrayList<>();
		if(null == map) {
			return result;
		}
		Set mapKeys = map.keySet();
		for (Object key : mapKeys) {
			result.add(map.get(key));
		}
		
		return result;
	}
}
