package cn.zswltech.mithras.service.auth.checker.fund.financing;

import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Component
public class FundFinancingSubModifyAuthChecker implements IDataAuthChecker {
    @Resource
    private AuthHelper authHelper;
    @Resource
    private FundFinancingMainModifyAuthChecker mainChecker;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        // 从数据库子表中获取主表id
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        // 委托给主表修改校验器校验
        return mainChecker.check(businessModule, helperMapperClass, mainId, args);
    }
}
