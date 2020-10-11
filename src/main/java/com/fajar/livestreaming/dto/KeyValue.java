package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeyValue<K, V> implements Entry<K, V>, Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -1668484384625090190L;

	private K key;
	private V value;
	@Builder.Default
	private boolean valid = true;

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}

	public static <T> KeyValue<T, T> identical(T value2) {

		KeyValue<T, T> keyValue = new KeyValue<T, T>();
		keyValue.setKey(value2);
		keyValue.setValue(value2);
		return keyValue;
	}

}
