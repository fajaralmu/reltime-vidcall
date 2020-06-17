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
	
	public static <K, V> List<V> mapToList(Map<K, V> map) {
		
		List<V> result = new ArrayList<V>();
		if(null == map) {
			return result;
		}
		Set<K> mapKeys = map.keySet();
		for (K key : mapKeys) {
			result.add(map.get(key));
		}
		
		return result;
	}
}
