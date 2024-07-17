package com.springbootemployeedata.springbootemployeedata.controller;

import org.springframework.web.bind.annotation.RestController;

import com.springbootemployeedata.springbootemployeedata.service.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.AbstractMap.SimpleEntry;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String, String> rt;

    @Autowired
    private RedisService redisService;

    /**
     * Sets a key-value pair in Redis.
     *
     * @param keyValue the key-value pair to set in Redis
     * @return the key-value pair that was set
     */
    @PostMapping("/redis")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, String> setRedisData(@RequestBody Map.Entry<String, String> keyValue) {
        rt.opsForValue().set(keyValue.getKey(), keyValue.getValue());
        return keyValue;
    }

    /**
     * Retrieves a value from Redis based on the key.
     *
     * @param key the key to retrieve the value for
     * @return the key-value pair retrieved from Redis
     */
    @GetMapping("/redis/{key}")
    public Map.Entry<String, String> getRedisData(@PathVariable("key") String key) {
        String value = rt.opsForValue().get(key);
        return new SimpleEntry<>(key, value);
    }

    /**
     * Increments the value of a key in Redis.
     *
     * @param key the key to increment the value for
     * @return the key and the new incremented value
     */
    @PostMapping("/redis/increment/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, Long> incrementKey(@PathVariable("key") String key) {
        Long newValue = rt.opsForValue().increment(key);
        return new SimpleEntry<>(key, newValue);
    }

    /**
     * Decrements the value of a key in Redis.
     *
     * @param key the key to decrement the value for
     * @return the key and the new decremented value
     */
    @PostMapping("/redis/decrement/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, Long> decrementKey(@PathVariable("key") String key) {
        Long newValue = rt.opsForValue().decrement(key);
        return new SimpleEntry<>(key, newValue);
    }

    /**
     * Sets the Time-To-Live (TTL) for a key in Redis.
     *
     * @param key the key to set the TTL for
     * @param ttl the TTL value in days
     * @return the key and a boolean indicating if the TTL was set successfully
     */
    @PostMapping("/redis/{key}/{ttl}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, Boolean> setTTL(@PathVariable("key") String key, @PathVariable("ttl") long ttl) {
        Boolean result = rt.expire(key, ttl, TimeUnit.DAYS);
        return new SimpleEntry<>(key, result);
    }

    /**
     * Sets the Time-To-Live (TTL) for a key in Redis using a request body.
     *
     * @param requestBody the request body containing the key and TTL in seconds
     * @return a boolean indicating if the TTL was set successfully
     */
    @PostMapping("/redis/ttl")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean setTTLForKey(@RequestBody Map<String, Object> requestBody) {
        String key = (String) requestBody.get("key");
        long ttlInSeconds = Long.parseLong(requestBody.get("ttlInSeconds").toString());

        // Check if the key exists
        if (rt.hasKey(key)) {
            // Set the TTL for the key
            return rt.expire(key, ttlInSeconds, TimeUnit.SECONDS);
        }
        // Key doesn't exist, return false
        return false;
    }

    /**
     * Updates the contribution count for an employee in Redis.
     *
     * @param requestBody the request body containing department name, employee ID, and count
     * @return a response entity with the key and updated count
     */
    @PostMapping("/redisemp")
    public ResponseEntity<Map<String, Object>> updateEmployeeContribution(
            @RequestBody Map<String, Object> requestBody) {

        String departmentName = (String) requestBody.get("departmentName");
        String employeeId = (String) requestBody.get("employeeId");
        int count = requestBody.containsKey("count") ? (int) requestBody.get("count") : 1;

        String key = "user." + departmentName + "." + employeeId;
        long updatedCount = redisService.updateEmployeeContribution(key, count);

        // Prepare response
        Map<String, Object> responseBody = Map.of(
                "key", key,
                "updatedCount", updatedCount);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    /**
     * Retrieves the contribution count for an employee from Redis.
     *
     * @param departmentName the department name
     * @param employeeId     the employee ID
     * @return a response entity with the contribution count
     */
    @GetMapping("/redisemp/{departmentName}/{employeeId}")
    public ResponseEntity<Long> getEmployeeContribution(
            @PathVariable String departmentName,
            @PathVariable String employeeId) {

        // Get the contribution count for the employee
        Long contribution = redisService.getEmployeeContribution(departmentName, employeeId);

        // Determine the HTTP status based on whether the contribution is null or not
        HttpStatus status = (contribution != null) ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        // Return the ResponseEntity with the determined status and contribution
        return ResponseEntity.status(status).body(contribution);
    }
}

