package cn.zswltech.mithras.service.auth.checker.implnew;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthDeptRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 主表新增数据权限校验
 * 只有业务部门的人才能新增数据
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:23 PM
 */
@Component
@Slf4j
public class CommonAddMainAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthDeptRule dataAuthDeptRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long mainId, Object[] args) {
        // 此时mapper是没用的 只校验当前用户部门
        dataAuthDeptRule.check();
        return true;
    }

}
