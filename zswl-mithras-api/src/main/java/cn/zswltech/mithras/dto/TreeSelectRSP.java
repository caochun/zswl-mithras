package cn.zswltech.mithras.dto;

import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/1
 * @description
 */
@Data
public class TreeSelectRSP {
    private Long id;
    private Long parentId;
    private String label;
    private String value;
    private List<TreeSelectRSP> children;
}
