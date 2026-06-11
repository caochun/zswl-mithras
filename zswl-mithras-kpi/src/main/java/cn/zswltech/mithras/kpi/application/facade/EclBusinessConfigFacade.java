package cn.zswltech.mithras.kpi.application.facade;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.kpi.application.EclBusinessConfigApplicationService;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.kpi.model.EclBusinessConfig;
import cn.zswltech.mithras.kpi.model.EclBusinessConfigLib;
import cn.zswltech.mithras.kpi.application.ecl.EclBusinessConfigLibService;
import cn.zswltech.mithras.kpi.application.ecl.EclBusinessConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description ecl_业务配置表
* @author vico
* @date 2025-09-24
*/
@Service
public class EclBusinessConfigFacade implements EclBusinessConfigApplicationService {

    @Resource
    private EclBusinessConfigService eclBusinessConfigService;
    @Resource
    private EclBusinessConfigLibService eclBusinessConfigLibService;


    @Override
    public R<Void> modify(EclBusinessConfigModifyREQ req){
        eclBusinessConfigService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<EclBusinessConfigListRSP>> list(EclBusinessConfigListREQ req){
        Page<EclBusinessConfig> data = eclBusinessConfigService.list(req);
        List<EclBusinessConfigListRSP> list = BeanUtil.copyToList(data.getRecords(), EclBusinessConfigListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<EclBusinessConfigDetailRSP> detail(@Valid EclBusinessConfigDetailREQ req) {
        return R.ok(eclBusinessConfigService.detail(req));
    }

    @Override
    public R<PageR<EclBusinessConfigListRSP>> versionList(@Valid EclBusinessConfigListREQ req) {
        Page<EclBusinessConfigLib> data = eclBusinessConfigLibService.versionList(req);
        List<EclBusinessConfigListRSP> list = BeanUtil.copyToList(data.getRecords(), EclBusinessConfigListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<EclBusinessConfigDetailRSP> versionDetail(@Valid EclBusinessConfigDetailREQ req) {
        return R.ok(BeanUtil.copyProperties(eclBusinessConfigLibService.versionDetail(req), EclBusinessConfigDetailRSP.class));
    }

}
