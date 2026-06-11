package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardClientOverviewSettleModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "所属部门", headerOrder = 20)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "所属主办", headerOrder = 30)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 40)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 50)
    private String contractCode;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 60)
    private String bizType;

    @SimpleExcelHeader(headerName = "所属部门", headerOrder = 70)
    private String contractBizDeptName;

    @SimpleExcelHeader(headerName = "所属主办", headerOrder = 80)
    private String contractSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 90)
    private String projCosponsorUserNames;

}
