package cn.zswltech.mithras.contract.core.application.impl;

import cn.zswltech.mithras.contract.core.application.ContractRemindRecordService;
import cn.zswltech.mithras.contract.core.application.delayqueue.DelayQueueService;
import cn.zswltech.mithras.contract.mapper.contract.ContractRemindRecordMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRemindRecord;
import cn.zswltech.mithras.service.enums.TimeoutTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/9/14
 * @description
 */
@Service("contractRemindRecordServiceImpl")
@Slf4j
public class ContractRemindRecordServiceImpl extends ServiceImpl<ContractRemindRecordMapper, ContractRemindRecord> implements ContractRemindRecordService {

    @Resource
    private DelayQueueService delayQueueService;

    @Override
    public void deleteByContractId(Long contractId) {
        LambdaQueryWrapper<ContractRemindRecord> query = Wrappers.lambdaQuery();
        query.eq(ContractRemindRecord::getContractId, contractId);
        this.remove(query);
        delayQueueService.delTask(TimeoutTypeEnum.CONTRACT_RENT.name(), String.valueOf(contractId));
    }

    @Override
    public List<ContractRemindRecord> listByContractCreator(Long creatorId) {
        LambdaQueryWrapper<ContractRemindRecord> query = Wrappers.lambdaQuery();
        query.eq(ContractRemindRecord::getContractCreatorId, creatorId);
        return this.list(query);
    }
}
