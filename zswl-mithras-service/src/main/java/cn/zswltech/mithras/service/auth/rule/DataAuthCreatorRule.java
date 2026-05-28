package cn.zswltech.mithras.service.auth.rule;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Component
public class DataAuthCreatorRule {
    public void check(BusinessModuleEnum businessModule, Long mainId) {
        BaseMapper mainTableMapper = SpringContextHolder.getBean(businessModule.getMainMapperClass());
        Object mainObject = mainTableMapper.selectById(mainId);
        if (Objects.isNull(mainObject)) {
            throw new AuthCheckException("主表数据不存在");
        }
        Long maintainerId = (Long) ReflectUtil.getFieldValue(mainObject, "createBy");
        if (null == maintainerId) {
            return;
        }
        if (!Objects.equals(maintainerId, AccountUtil.getLoginInfo().getId())) {
            throw new AuthCheckException("非数据创建人，不支持该种操作");
        }
    }
}
