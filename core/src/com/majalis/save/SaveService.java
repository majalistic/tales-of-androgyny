package com.majalis.save;
/*
 * Service interface that provides an interface for delivering save messages to the SaveManager
 */
public interface SaveService {
    public void saveDataValue(SaveEnum key, Object object);    
    public void newSave();
}
