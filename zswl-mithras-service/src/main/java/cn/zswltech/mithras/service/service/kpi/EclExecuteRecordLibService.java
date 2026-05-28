package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.EclExecuteRecordRemoveREQ;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.kpi.EclExecuteRecordLibMapper;
import cn.zswltech.mithras.service.mapper.model.kpi.EclExecuteRecordLib;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @description 资产减值记录表
* @author vico
* @date 2025-09-28
*/
@Service
public class EclExecuteRecordLibService extends ServiceImpl<EclExecuteRecordLibMapper, EclExecuteRecordLib> {


    @Transactional(rollbackFor = Throwable.class)
    public void remove(EclExecuteRecordRemoveREQ req) {
        LambdaUpdateWrapper<EclExecuteRecordLib> libLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        libLambdaUpdateWrapper.set(EclExecuteRecordLib::getDeleted, YesOrNoNumberEnum.YES.getCode());
        if (ObjectUtil.isNotEmpty(req.getId())) {
            libLambdaUpdateWrapper.eq(EclExecuteRecordLib::getId, req.getId());
        }
        if (ObjectUtil.isNotEmpty(req.getIds())) {
            libLambdaUpdateWrapper.in(EclExecuteRecordLib::getId, req.getIds());
        }
        this.update(null, libLambdaUpdateWrapper);
    }

}