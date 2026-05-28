package cn.zswltech.mithras.dto.flow.form;

import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/5/18
 * @description
 */
@Data
public class ChooseApproveAuthDTO {
    private Boolean canChoose;
    private String approveAuth;
    private List<String> approvalUserIdList;
}
