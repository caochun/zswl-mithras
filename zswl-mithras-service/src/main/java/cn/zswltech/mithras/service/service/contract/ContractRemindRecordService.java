package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractRemindRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/9/14
 * @description
 */
public interface ContractRemindRecordService extends IService<ContractRemindRecord> {
    void deleteByContractId(Long contractId);

    List<ContractRemindRecord> listByContractCreator(Long creatorId);

}
