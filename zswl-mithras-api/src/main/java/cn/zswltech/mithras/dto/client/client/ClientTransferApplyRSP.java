package cn.zswltech.mithras.dto.client.client;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@Data
public class ClientTransferApplyRSP {
    private Long id;
    private String batchNo;
    private String approvalStatus;
}
