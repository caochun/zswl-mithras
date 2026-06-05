package cn.zswltech.mithras.kpi.excel.model;

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
public class KpiProjGuessContractListExcelModel extends ExcelModel {

    //合同编号
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 10)
    private String contractCode;

    //合同编号
    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "项目来源", headerOrder = 30)
    private String projSource;

    @SimpleExcelHeader(headerName = "项目类型", headerOrder = 40)
    private String projClassify;

    @SimpleExcelHeader(headerName = "投放日", headerOrder = 50)
    private String contractStartDate;

    @SimpleExcelHeader(headerName = "所属部门", headerOrder = 60)
    private String belongDeptName;

    //利润-当期值
    @SimpleExcelHeader(headerName = "项目利润-当期值", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String profitCurrent;

    //利润-累计值
    @SimpleExcelHeader(headerName = "项目利润-累计值", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String profitTotal;

    //奖金-当期值
    @SimpleExcelHeader(headerName = "项目奖金-当期值", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusCurrent;

    //奖金-累计值
    @SimpleExcelHeader(headerName = "项目奖金-累计值", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusTotal;

    //核算月份
    @SimpleExcelHeader(headerName = "最新核算月份", headerOrder = 110)
    private String calculateDate;

}
