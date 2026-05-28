package cn.zswltech.mithras.service.convert.creditsearch;

import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.service.enums.creditreport.CreditApplyStatusEnum;
import cn.zswltech.mithras.service.excel.model.CreditSearchExcelModel;

import java.util.Optional;

/**
 * 转换
 */
public class CreditSearchConvert {

    public static CreditSearchExcelModel creditSearchListRSPExcelModel(CreditReportListDTO dto) {
        CreditSearchExcelModel searchExcelModel = new CreditSearchExcelModel();
        searchExcelModel.setCreditCode(dto.getCreditCode());
        searchExcelModel.setClientName(dto.getClientName());
        searchExcelModel.setCscCode(dto.getCscCode());
        searchExcelModel.setProjectName(dto.getProjName());
        searchExcelModel.setApplyUserName(dto.getApplyUserName());
        searchExcelModel.setApplyOrgName(dto.getApplyOrgName());
        CreditApplyStatusEnum creditApplyStatusEnum = CreditApplyStatusEnum.finaByName(dto.getApplyStatus());
        searchExcelModel.setApplyStatus(Optional.ofNullable(creditApplyStatusEnum).map(CreditApplyStatusEnum::display).orElse("未知"));
        searchExcelModel.setApplyTime(dto.getApplyTime());
        searchExcelModel.setSearchStatus(dto.getSelectStatus());
        searchExcelModel.setSearchTime(dto.getSelectTime());
        return searchExcelModel;

    }
}
