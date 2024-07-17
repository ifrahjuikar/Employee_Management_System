package com.springbootemployeedata.springbootemployeedata.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public long updateEmployeeContribution(String key, int count) {
        // If key doesn't exist, set it to the default value (0)
        redisTemplate.opsForValue().setIfAbsent(key, "0");

        // Increment the value of the key by the count
        return redisTemplate.opsForValue().increment(key, count);
    }

   
    public Long getEmployeeContribution(String departmentName, String employeeId) {
        // Construct the key for the employee
        String key = "user." + departmentName + "." + employeeId;

        // Retrieve the contribution count for the employee from Redis
        String contributionStr = redisTemplate.opsForValue().get(key);

        // Convert the contribution count to Long
        if (contributionStr != null) {
            return Long.parseLong(contributionStr);
        } else {
            return null; // Return null if key does not exist
        }
    }
}
