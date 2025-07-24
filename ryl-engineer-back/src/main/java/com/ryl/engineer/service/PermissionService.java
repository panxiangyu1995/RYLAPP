package com.ryl.engineer.service;
 
public interface PermissionService {
    void checkWarehouseDeletePermission();
    void checkCustomerDeletePermission(Long customerId);
} 