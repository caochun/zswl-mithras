package cn.zswltech.mithras.api.common;

/**
 * @author junke
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PageR<T> implements Serializable {

    private static final long serialVersionUID = 4747104746707551711L;
    /**
     * 查询集合
     */
    private List<T> list;
    /**
     * 数据总记录数。
     */
    private long total;
    /**
     * 总页数
     */
    private long pages;
    /**
     * 页大小
     */
    private long pageSize;
    /**
     * 当前页
     */
    private long currentPage;
    /**
     * 其他携带参数
     */
    private Map<String, Object> others = new HashMap<>();

    public static <T> PageR<T> empty(long currentPage, long pageSize) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(Collections.emptyList());
        pageResult.setTotal(0);
        pageResult.setPages(0);
        pageResult.setCurrentPage(currentPage);
        pageResult.setPageSize(pageSize);
        return pageResult;
    }

    public static <T> PageR<T> of(List<T> list, long total, long currentPage, long pageSize) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(list);
        pageResult.setTotal(total);
        if (total % pageSize == 0) {
            pageResult.setPages(total / pageSize);
        } else {
            pageResult.setPages((total / pageSize) + 1);
        }
        pageResult.setCurrentPage(currentPage);
        pageResult.setPageSize(pageSize);
        return pageResult;
    }

    public static <T> PageR<T> of(List<T> list, long total, long pages, long currentPage, long pageSize) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(list);
        pageResult.setTotal(total);
        pageResult.setPages(pages);
        pageResult.setCurrentPage(currentPage);
        pageResult.setPageSize(pageSize);
        return pageResult;
    }

    public static <T> PageR<T> of(Page<T> page) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(page.getRecords());
        pageResult.setTotal(page.getTotal());
        pageResult.setCurrentPage(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setPages(page.getPages());
        return pageResult;
    }

    public static <T> PageR<T> of(List<T> list, long total) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(list);
        pageResult.setTotal(total);
        return pageResult;
    }

    public static <S, T> PageR<T> of(Page<S> data, List<T> dto) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(dto);
        pageResult.setTotal(data.getTotal());
        pageResult.setPageSize(data.getSize());
        pageResult.setPages(data.getPages());
        pageResult.setCurrentPage(data.getCurrent());
        return pageResult;
    }

    public static <S, T> PageR<T> of(PageR<S> data, List<T> dto) {
        PageR<T> pageResult = new PageR<>();
        pageResult.setList(dto);
        pageResult.setTotal(data.getTotal());
        pageResult.setPageSize(data.getPageSize());
        pageResult.setPages(data.getPages());
        pageResult.setCurrentPage(data.getCurrentPage());
        return pageResult;
    }
}
