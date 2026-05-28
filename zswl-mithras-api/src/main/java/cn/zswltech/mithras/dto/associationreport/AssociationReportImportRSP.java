package cn.zswltech.mithras.dto.associationreport;

import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/24
 * @description
 */
@Data
public class AssociationReportImportRSP {
    private Boolean importSuccess;
    private List<String> errorMessageList;
}
