package org.example.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.io.Serializable;

/**
 * 分页请求封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码，从1开始
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向：asc, desc
     */
    private String sortOrder = "desc";
    
    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
    
    /**
     * 检查排序方向是否有效
     */
    public boolean isValidSortOrder() {
        return "asc".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder);
    }
    
    /**
     * 获取有效的排序方向
     */
    public String getValidSortOrder() {
        return isValidSortOrder() ? sortOrder : "desc";
    }
}
