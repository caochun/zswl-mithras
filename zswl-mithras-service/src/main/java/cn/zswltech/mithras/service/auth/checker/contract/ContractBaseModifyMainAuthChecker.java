package cn.zswltech.mithras.service.auth.checker.contract;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author jackerhe
 * @date 2022/8/31
 * @description
 */
@Component
public class ContractBaseModifyMainAuthChecker implements IDataAuthChecker {

    @Resource
    private CommonModifyMainAuthCheckerNew commonModifyMainAuthCheckerNew;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        ContractBaseInfo contractBaseInfo;
        if (mainData instanceof ContractBaseInfo) {
            contractBaseInfo = (ContractBaseInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        // 判断合同是否已经走过结清流程
        if (Objects.equals(ContractProcessStatusEnum.SETTLE_PASS.name(), contractBaseInfo.getContractProcessStatus())) {
            throw new MithrasException("合同已走完结清流程，无法操作");
        }
        // 判断合同是否结清
        Assert.isTrue(!Objects.equals(ContractStatus.SETTLE.name(), contractBaseInfo.getContractStatus()), () -> MithrasException.newException("合同已结清，无法操作"));
        commonModifyMainAuthCheckerNew.check(businessModule, helperMapperClass, keyId, args);
        return true;
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
