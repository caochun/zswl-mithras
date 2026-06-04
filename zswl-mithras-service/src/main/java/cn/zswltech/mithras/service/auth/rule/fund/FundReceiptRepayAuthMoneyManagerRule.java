package cn.zswltech.mithras.service.auth.rule.fund;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.SponsorField;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 判断当前操作数据 是不是 资金经理
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:52 PM
 */
@Component
@Slf4j
public class FundReceiptRepayAuthMoneyManagerRule {

    @Resource
    private AuthHelper authHelper;
    @Resource
    private SysUserService sysUserService;

    public void check(BusinessModuleEnum businessModule, Long mainId) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能操作数据!");
        }
    }

}
