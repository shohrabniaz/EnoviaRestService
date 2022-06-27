package com.bjit.common.rest.app.service.utilities;

import org.modelmapper.ModelMapper;


/**
 *
 * @author Omour Faruq
 * @param <TSourceObject>
 * @param <KDestinationObject>
 */
public class ObjectMapper<TSourceObject, KDestinationObject> {
    //private @Inject ModelMapper modelMapper;
    private ModelMapper modelMapper = new ModelMapper();
    private TSourceObject source;
    private KDestinationObject destination;

    private void mapObjects(Class<KDestinationObject> entityClass) throws Exception{
        try{
            this.destination = modelMapper.map(this.source, entityClass);
        }
        catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }
    }

    public ObjectMapper setObjects(TSourceObject sourceObject, Class<KDestinationObject> destinationClass) throws Exception{
        this.source = sourceObject;
        mapObjects(destinationClass);
        return this;
    }
    
    public KDestinationObject getObject(){
        return this.destination;
    }
}
