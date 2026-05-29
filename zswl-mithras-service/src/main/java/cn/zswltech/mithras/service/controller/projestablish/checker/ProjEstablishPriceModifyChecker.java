/*
package cn.zswltech.mithras.service.controller.projestablish.checker;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.checker.impl.CommonModifySubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

*/
/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/5 11:36
 *//*

@Component
public class ProjEstablishPriceModifyChecker implements IDataAuthChecker {
    @Resource
    private ProjEstablishBaseInfoService baseInfoService;
    @Resource
    private CommonModifySubAuthChecker commonModifySubAuthChecker;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        commonModifySubAuthChecker.check(businessModule,helperMapperClass,keyId,args);
        if(ObjectUtil.isNotEmpty(keyId)){
            ProjEstablishBaseInfo baseInfo = baseInfoService.getById(keyId);
            if(RecordStatus.CLOSED.name().equals(baseInfo.getProjEstablishStatus())){
                throw new MithrasException("该立项已关闭，不允许再修改有关信息");
            }
        }
        return true;
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return commonModifySubAuthChecker.checkBatch(businessModule,helperMapperClass,keyIds,args);
    }
}
*/
