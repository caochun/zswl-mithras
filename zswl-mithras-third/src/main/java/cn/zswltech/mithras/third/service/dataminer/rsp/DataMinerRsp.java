package cn.zswltech.mithras.third.service.dataminer.rsp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
@Data
public class DataMinerRsp<T> implements Serializable {
    private Boolean success;
    private Integer code;
    private String message;
    // 非分页接口返回结果
    private List<T> dataList;
    // 分页接口返回结果
    private PageListResult<T> pageResult;

    @Data
    public static class PageListResult<T> {
        private List<T> list;
        private int total;
        private int pages;
        private int pageSize;
        private int currentPage;
    }
}
