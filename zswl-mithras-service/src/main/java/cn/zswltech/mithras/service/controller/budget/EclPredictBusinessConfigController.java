package cn.zswltech.mithras.service.controller.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.budget.EclPredictBusinessConfigApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.service.mapper.model.budget.EclPredictBusinessConfig;
import cn.zswltech.mithras.service.service.budget.EclPredictBusinessConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description ecl_预测业务配置表
* @author vico
* @date 2025-10-14
*/
@RestController
public class EclPredictBusinessConfigController implements EclPredictBusinessConfigApi {

    @Resource
    private EclPredictBusinessConfigService eclPredictBusinessConfigService;

    @Override
    public R<Void> modify(EclPredictBusinessConfigModifyREQ req){
        eclPredictBusinessConfigService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<EclPredictBusinessConfigListRSP>> list(@Valid EclPredictBusinessConfigListREQ req) {
        Page<EclPredictBusinessConfig> data = eclPredictBusinessConfigService.list(req);
        List<EclPredictBusinessConfigListRSP> list = BeanUtil.copyToList(data.getRecords(), EclPredictBusinessConfigListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<EclPredictBusinessConfigDetailRSP> detail(@Valid EclPredictBusinessConfigDetailREQ req) {
        return R.ok(BeanUtil.copyProperties(eclPredictBusinessConfigService.detail(req), EclPredictBusinessConfigDetailRSP.class));
    }



}