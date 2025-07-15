package com.ryl.miniprogram.converter;

import com.ryl.miniprogram.dto.CustomerDTO;
import com.ryl.miniprogram.entity.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 客户数据转换器
 */
@Component
public class CustomerConverter {
    
    /**
     * 将 DTO 转换为实体
     *
     * @param customerDTO 客户 DTO
     * @return 客户实体
     */
    public Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        
        return customer;
    }
    
    /**
     * 将实体转换为 DTO
     *
     * @param customer 客户实体
     * @return 客户 DTO
     */
    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        
        return customerDTO;
    }
    
    /**
     * 更新实体
     *
     * @param customer    要更新的客户实体
     * @param customerDTO 包含更新数据的 DTO
     * @return 更新后的客户实体
     */
    public Customer updateEntity(Customer customer, CustomerDTO customerDTO) {
        if (customer == null || customerDTO == null) {
            return customer;
        }
        
        // 只更新非空字段
        if (customerDTO.getName() != null) {
            customer.setName(customerDTO.getName());
        }
        
        if (customerDTO.getContact() != null) {
            customer.setContact(customerDTO.getContact());
        }
        
        if (customerDTO.getPhone() != null) {
            customer.setPhone(customerDTO.getPhone());
        }
        
        if (customerDTO.getEmail() != null) {
            customer.setEmail(customerDTO.getEmail());
        }
        
        if (customerDTO.getAddress() != null) {
            customer.setAddress(customerDTO.getAddress());
        }
        
        if (customerDTO.getOpenid() != null) {
            customer.setOpenid(customerDTO.getOpenid());
        }
        
        if (customerDTO.getNickname() != null) {
            customer.setNickname(customerDTO.getNickname());
        }
        
        if (customerDTO.getAvatarUrl() != null) {
            customer.setAvatarUrl(customerDTO.getAvatarUrl());
        }
        
        return customer;
    }
}