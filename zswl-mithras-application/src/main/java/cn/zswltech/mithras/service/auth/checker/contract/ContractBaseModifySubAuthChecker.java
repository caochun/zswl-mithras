package cn.zswltech.mithras.service.auth.checker.contract;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.checker.DefaultMainIdInspector;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
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
public class ContractBaseModifySubAuthChecker implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private AuthHelper authHelper;
    @Resource
    private DefaultMainIdInspector defaultMainIdInspector;


    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {

        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        // 从数据库子表中获取主表id
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        //合同
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(mainId);
        ContractBaseInfo contractBaseInfo;
        if (mainData instanceof ContractBaseInfo) {
            contractBaseInfo = (ContractBaseInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        // 判断合同是否结清
        Assert.isTrue(!Objects.equals(ContractStatus.SETTLE.name(), contractBaseInfo.getContractStatus()), () -> MithrasException.newException("合同已结清，无法操作"));

        //统一
        if (args.length > 0) {
            // 此处做一个额外的校验 禁止更新子表中的主表id 兼容之前各处都没处理参数中主表id的问题
            defaultMainIdInspector.inspect(businessModule, args[0], mainId);
        }
        // 权限校验
        dataAuthSponsorUserRule.check(businessModule, mainId);
        dataAuthProcessRule.check(businessModule, mainId);
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
