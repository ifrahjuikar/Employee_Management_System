package com.springbootemployeedata.springbootemployeedata.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus()
public class HttpResponseHandler {
    public static ResponseEntity<Object> generateResponse(String statusMessage,int statusCodeValue, HttpStatus statusCode, Object responseObject){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("Status Message", statusMessage);
        map.put("Status Code Value", statusCodeValue);
        map.put("Status Code", statusCode);
        map.put("Object", responseObject);
        return new ResponseEntity<Object>(map,statusCode);
    }
}
