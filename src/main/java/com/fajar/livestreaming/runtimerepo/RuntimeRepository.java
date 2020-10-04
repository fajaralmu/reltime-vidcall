package com.fajar.livestreaming.runtimerepo;

public interface RuntimeRepository<V> {

	abstract  V getData();
	abstract  boolean updateData(V data);
}
