package org.example.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);
        pageResult.setList(list);
        
        // 计算总页数
        Integer pages = (int) Math.ceil((double) total / pageSize);
        pageResult.setPages(pages);
        
        // 计算是否有上一页和下一页
        pageResult.setHasPrevious(pageNum > 1);
        pageResult.setHasNext(pageNum < pages);
        
        return pageResult;
    }
    
    /**
     * 创建空的分页结果
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize, 0L, List.of());
    }
}
