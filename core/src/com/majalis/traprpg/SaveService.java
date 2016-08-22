package com.majalis.traprpg;
/*
 * Service interface that provides an interface for delivering save messages to the SaveManager
 */
public interface SaveService {
    public void saveDataValue(String key, Object object);
}
