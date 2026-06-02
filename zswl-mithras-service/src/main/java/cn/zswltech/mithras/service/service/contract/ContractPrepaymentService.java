package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentAddREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentDetailRSP;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentModifyREQ;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPrepayment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ContractPrepaymentService extends IService<ContractPrepayment> {

    ContractPrepayment getList(ContractIdListREQ req);

    ContractPrepaymentDetailRSP add(ContractPrepaymentAddREQ req);

    void update(ContractPrepaymentModifyREQ req);

    <T extends ContractPrepaymentAddREQ> void calculation(T contractPrepayment);

}
