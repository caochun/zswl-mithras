package cn.zswltech.mithras.service.auth.checker;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.annotation.MainIdExtract;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 校验编辑请求体里的主表id 与 原表里的主表id是否一样
 *
 * @author wangchuanhao
 * @date 2022/11/17 10:51 AM
 */
@Component
public class DefaultMainIdInspector {
    
    @Resource
    private AuthHelper authHelper;

    public void inspect(BusinessModuleEnum businessModuleEnum, Object param, Long originMainId) {
        if (Objects.isNull(param)) {
            return;
        }
        Object paramMainId = null;
        MainIdExtract annotation = param.getClass().getAnnotation(MainIdExtract.class);
        if (Objects.nonNull(annotation)) {
            paramMainId = authHelper.extractObjectByExpression(param, annotation.expression());
        } else {
            // 默认情况下只取一级
            paramMainId = ReflectUtil.getFieldValue(param, businessModuleEnum.getSubTableMainIdFieldName());
        }
        if (Objects.isNull(paramMainId)) {
            // 前端没传值 不处理
            return;
        }
        if (!Objects.equals(paramMainId, originMainId)) {
            throw new AuthCheckException("子表数据修改时不允许修改主表id");
        }
    }

}
