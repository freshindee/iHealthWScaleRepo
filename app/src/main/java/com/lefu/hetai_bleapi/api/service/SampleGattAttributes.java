package com.lefu.hetai_bleapi.api.service;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class SampleGattAttributes {
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static HashMap<String, String> attributes = new HashMap();
	public static String HEART_RATE_MEASUREMENT = "0000C004-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	static {
		attributes.put("0000181b-0000-1000-8000-00805f9b34fb", "Measurement Service");
		attributes.put("0000fff00000-1000-8000-00805f9b34fb", "Heart Rate Service");
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
		attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
