package com.fajar.livestreaming.runtimerepo;

import java.util.List;

public interface BaseRuntimeRepo<T> {
	
	public  List<T> getAll();
	public boolean deleteByKey(String key);
	public boolean clearAll();
	public  T get(String key);

}
