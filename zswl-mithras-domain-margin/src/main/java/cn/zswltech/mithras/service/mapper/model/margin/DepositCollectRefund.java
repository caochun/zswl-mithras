package cn.zswltech.mithras.service.mapper.model.margin;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/24 14:19
 */
@Data
public class DepositCollectRefund {

    private Long contractId;

    private String recordType;

    private Long amount;
}
