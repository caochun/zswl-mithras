package cn.zswltech.mithras.kpi.application.ecl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.EclBusinessConfigDetailREQ;
import cn.zswltech.mithras.dto.kpi.EclBusinessConfigListREQ;
import cn.zswltech.mithras.kpi.mapper.EclBusinessConfigLibMapper;
import cn.zswltech.mithras.kpi.mapper.model.EclBusinessConfigLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @description ecl_业务配置表版本表
* @author vico
* @date 2025-09-25
*/
@Service
public class EclBusinessConfigLibService extends ServiceImpl<EclBusinessConfigLibMapper, EclBusinessConfigLib> {


    public Page<EclBusinessConfigLib> versionList(EclBusinessConfigListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclBusinessConfigLib>lambdaQuery()
        .eq(ObjectUtil.isNotEmpty(req.getConfigCode()), EclBusinessConfigLib::getConfigCode, req.getConfigCode())
        .orderByDesc(EclBusinessConfigLib::getVersionTime));
    }

    public EclBusinessConfigLib versionDetail(EclBusinessConfigDetailREQ req) {
        return this.getById(req.getId());
    }

}