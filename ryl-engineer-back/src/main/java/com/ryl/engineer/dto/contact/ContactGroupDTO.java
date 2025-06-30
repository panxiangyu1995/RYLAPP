package com.ryl.engineer.dto.contact;

import lombok.Data;

import java.util.List;

/**
 * 联系人分组数据传输对象
 */
@Data
public class ContactGroupDTO {
    
    /**
     * 分组ID
     */
    private Long id;
    
    /**
     * 分组名称
     */
    private String name;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 分组中的联系人列表
     */
    private List<ContactDTO> contacts;
} 