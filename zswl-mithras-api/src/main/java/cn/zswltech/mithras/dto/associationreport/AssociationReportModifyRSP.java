package cn.zswltech.mithras.dto.associationreport;

import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/24
 * @description
 */
@Data
public class AssociationReportModifyRSP {
    private Boolean modifySuccess;
    private List<String> errorMessageList;
}
