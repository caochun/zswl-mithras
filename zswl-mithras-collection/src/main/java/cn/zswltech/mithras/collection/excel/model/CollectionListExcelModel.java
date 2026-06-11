package cn.zswltech.mithras.collection.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 收款核销列表
 *
 * @author wangchuanhao
 * @date 2022/8/23 3:31 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CollectionListExcelModel extends ExcelModel {

    private Long id;

    @SimpleExcelHeader(headerName = "收款核销编号", headerOrder = 10)
    private String code;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 30)
    private String clientName;

    @SimpleExcelHeader(headerName = "核销状态", headerOrder = 40)
    private String writeOffStatus;

    @SimpleExcelHeader(headerName = "期项", headerOrder = 50)
    private Integer phase;

    @SimpleExcelHeader(headerName = "现金流项目", headerOrder = 60)
    private String cashFlowItem;

    @SimpleExcelHeader(headerName = "计划收款日期", headerOrder = 70)
    private String planCollectionDate;

    @SimpleExcelHeader(headerName = "计划收款金额", headerOrder = 80)
    private String planCollectionAmount;

    @SimpleExcelHeader(headerName = "实收日期", headerOrder = 90)
    private String collectionDate;

    @SimpleExcelHeader(headerName = "实收金额", headerOrder = 100)
    private String collectionAmount;
}
