package cn.zswltech.mithras.contract.mapper.query;

import lombok.Data;

import java.util.Collection;

/**
 * @author dingqi
 * @date 2023/7/24
 * @description
 */
@Data
public class ContractPrincipalQuery {
    private Collection<Long> contractIds;
}
