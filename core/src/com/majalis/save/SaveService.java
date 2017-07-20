package com.majalis.save;

import com.badlogic.gdx.utils.Array;

/*
 * Service interface that provides an interface for delivering save messages to the SaveManager
 */
public interface SaveService {
    public Array<MutationResult> saveDataValue(SaveEnum key, Object object);    
    public Array<MutationResult> saveDataValue(SaveEnum key, Object object, boolean saveToJson);    
    public void saveDataValue(ProfileEnum key, Object object);  
    public void saveDataValue(ProfileEnum key, Object object, boolean saveToJson);  
    public void newSave();
    public void newSave(String path);
    public void manualSave(String path);
}
