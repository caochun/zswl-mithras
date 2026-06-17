package cn.zswltech.mithras.application.orchestration.auth.rule;


import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthViewGuard;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.SysUserService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.util.ObjectUtil.equal;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.gruul.common.util.AccountUtil.getLoginInfo;

/**
 * 数据查看权限
 *
 * @author wangchuanhao
 * @date 2022/11/17 9:57 AM
 */
@Component
public class DataAuthViewRule implements DataAuthViewGuard {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private AuthHelper authHelper;

    @Override
    public void check(DataAuthBusinessModule businessModule, Long mainId) {
        if(BusinessModuleEnum.CONTARCT_DEPOSIT.name().equals(businessModule.name())){ // 退抵流程不控权限
            return;
        }
        List<Long> deptIds = sysUserService.canViewDeptIds();
        if (isNull(deptIds)) {
            // 当前用户非业务部门用户，可查看全部
            return;
        }
        if (Objects.isNull(businessModule.getMainMapperClass())) {
            return;
        }

        // 判断数据创建用户是否在登陆用户所属业务部门的用户id集合内
        BaseMapper mainTableMapper = SpringContextHolder.getBean(businessModule.getMainMapperClass());
        Object mainObject = mainTableMapper.selectById(mainId);
        if (StringUtils.isNotBlank(businessModule.getSponsorModule())) {
            mainObject = authHelper.getAuthObj(businessModule, mainObject);
        }

        if (Objects.isNull(mainObject)) {
            throw new AuthCheckException("主表数据不存在");
        }
        String sponsorFieldName = "createBy";
        String cosponsorFieldName = "";
        Long belongDept = null;
        SponsorField sponsorField = mainObject.getClass().getAnnotation(SponsorField.class);
        if (null != sponsorField) {
            sponsorFieldName = sponsorField.value();
            belongDept = (Long) ReflectUtil.getFieldValue(mainObject, sponsorField.belongDeptField());
            cosponsorFieldName = sponsorField.cosponsorField();
        }
        Long sponsorId = (Long) ReflectUtil.getFieldValue(mainObject, sponsorFieldName);
        List<Long> cosponsorList = StringUtils.isBlank(cosponsorFieldName)
                ? new ArrayList<>() : Optional.ofNullable(ReflectUtil.getFieldValue(mainObject, cosponsorFieldName)).map(s -> JSONArray.parseArray((String) s, Long.class)).orElse(new ArrayList<>());

        if (equal(sponsorId, getLoginInfo().getId())
                || cosponsorList.contains(getLoginInfo().getId())
                || deptIds.contains(belongDept)) {
            return;
        } else {
            throw new AuthCheckException("当前登陆用户无权限查看");
        }

    }

}
