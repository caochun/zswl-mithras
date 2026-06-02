package cn.zswltech.mithras.contract.core.application;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractEvaluationAgencyEffect;
import cn.zswltech.mithras.contract.mapper.contract.ContractEvaluationAgencyEffectMapper;
import org.springframework.stereotype.Service;

/**
 * @author bigbear
 * @description 针对表【contract_evaluation_agency_lib(合同租赁物评估机构关联版本表)】的数据库操作Service实现
 * @createDate 2025-03-21 14:38:07
 */
@Service
public class ContractEvaluationAgencyEffectService extends ServiceImpl<ContractEvaluationAgencyEffectMapper, ContractEvaluationAgencyEffect>
        implements IService<ContractEvaluationAgencyEffect> {

}




