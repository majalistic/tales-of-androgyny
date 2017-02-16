package com.majalis.save;
/*
 * Service interface that provides an interface for delivering save messages to the SaveManager
 */
public interface SaveService {
    public String saveDataValue(SaveEnum key, Object object);    
    public String saveDataValue(SaveEnum key, Object object, boolean saveToJson);    
    public void saveDataValue(ProfileEnum key, Object object);  
    public void saveDataValue(ProfileEnum key, Object object, boolean saveToJson);  
    public void newSave();
    public void newSave(String path);
    public void manualSave(String path);
}
