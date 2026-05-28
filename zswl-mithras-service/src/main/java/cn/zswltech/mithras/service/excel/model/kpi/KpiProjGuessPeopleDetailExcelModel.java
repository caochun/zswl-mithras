package cn.zswltech.mithras.service.excel.model.kpi;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流入明细下载
 * @author: jackerhe
 * @date: 2023/5/17 3:44 下午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class KpiProjGuessPeopleDetailExcelModel extends ExcelModel {

    //人员/部门
    @SimpleExcelHeader(headerName = "人员/部门", headerOrder = 10)
    private String divideTypeName;

    @SimpleExcelHeader(headerName = "人员/部门名称", headerOrder = 20)
    private String divideTargetName;

    //核算月份
    @SimpleExcelHeader(headerName = "核算月份", headerOrder = 30)
    private String calculateDate;

    //奖金-当期值
    @SimpleExcelHeader(headerName = "项目奖金-当期值", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusCurrent;

    //奖金-累计值
    @SimpleExcelHeader(headerName = "项目奖金-累计值", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusTotal;


}
