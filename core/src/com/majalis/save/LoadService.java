package com.majalis.save;
/*
 * Service interface that provides an interface for delivering save messages to the SaveManager
 */
public interface LoadService {

	public <T> T loadDataValue(String key, Class<?> type);

}
