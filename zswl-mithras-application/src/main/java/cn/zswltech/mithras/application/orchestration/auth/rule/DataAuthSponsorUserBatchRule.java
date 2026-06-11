package cn.zswltech.mithras.application.orchestration.auth.rule;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/7/29
 * @description
 */
@Component
public class DataAuthSponsorUserBatchRule {

    @Resource
    private AuthHelper authHelper;

    public void check(DataAuthBusinessModule businessModule, Collection<Long> mainIds) {
        BaseMapper mainTableMapper = SpringContextHolder.getBean(businessModule.getMainMapperClass());
        List<Object> dataList = mainTableMapper.selectBatchIds(mainIds);
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        // 该模块主表自己没有主办字段 要从别的模块取 获取实际要取主办值的模块对象
        if (StringUtils.isNotBlank(businessModule.getSponsorModule())) {
            dataList = authHelper.getAuthObjList(businessModule, dataList);
        }

        for (Object mainObject : dataList) {
            String field = "createBy";
            SponsorField annotation = mainObject.getClass().getAnnotation(SponsorField.class);
            if (null != annotation) {
                field = annotation.value();
            }
            Long maintainerId = (Long) ReflectUtil.getFieldValue(mainObject, field);
            if (null == maintainerId) {
                return;
            }
            if (!Objects.equals(maintainerId, AccountUtil.getLoginInfo().getId())) {
                throw new AuthCheckException("包含非主办数据，无法执行批量操作，请确认");
            }
        }
    }
}
