package cn.zswltech.mithras.riskcontrol.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 关联方信息导入模板
 * @author: zhaozhengkang
 * @date: 2023/3/8 15:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RelatedClientExcelModel extends ExcelModel{

    /**
     * 关联方名称
     */
    @SimpleExcelHeader(headerName = "关联方名称")
    private String clientName;

    /**
     * 关联方类型
     */
    @SimpleExcelHeader(headerName = "关联方类型")
    private String relatedPartyType;

    /**
     * 统一社会信用代码
     */
    @SimpleExcelHeader(headerName = "统一社会信用代码")
    private String uscd;

    /**
     * 关联关系说明
     */
    @SimpleExcelHeader(headerName = "关联关系说明")
    private String description;

    @SimpleExcelHeader(headerName = "关联关系父类型")
    private String parentRelationType;

    @SimpleExcelHeader(headerName = "关联关系子类型")
    private String subRelationType;

    @SimpleExcelHeader(headerName = "企业类型")
    private String c3;

    @SimpleExcelHeader(headerName = "注册资本(万)")
    private String c4;

    @SimpleExcelHeader(headerName = "法定代表人")
    private String c5;

    @SimpleExcelHeader(headerName = "所属行业")
    private String c6;

    @SimpleExcelHeader(headerName = "注册地址")
    private String c7;

    @SimpleExcelHeader(headerName = "经营范围")
    private String c8;

    @SimpleExcelHeader(headerName = "状态")
    private String c9;
}
