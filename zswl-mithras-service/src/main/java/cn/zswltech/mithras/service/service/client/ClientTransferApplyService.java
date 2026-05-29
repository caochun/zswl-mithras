package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.util.IdUtil;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.service.mapper.client.ClientTransferApplyMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientTransferApply;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@Slf4j
@Service
public class ClientTransferApplyService extends ServiceImpl<ClientTransferApplyMapper, ClientTransferApply> {
    public ClientTransferApply create() {
        ClientTransferApply clientTransferApply = new ClientTransferApply();
        clientTransferApply.setBatchNo(IdUtil.getSnowflakeNextIdStr());
        clientTransferApply.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
        this.save(clientTransferApply);
        return clientTransferApply;
    }

    public ClientTransferApply findByBatchNo(String batchNo) {
        LambdaQueryWrapper<ClientTransferApply> query = Wrappers.lambdaQuery();
        query.eq(ClientTransferApply::getBatchNo, batchNo);
        return this.getOne(query);
    }
}
