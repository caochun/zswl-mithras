package cn.zswltech.mithras.service.controller.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.budget.EclExecutePredictBaseInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.budget.EclExecutePredictBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.budget.EclExecutePredictBaseInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
* @description 资产减值预测表
* @author vico
* @date 2025-10-14
*/
@RestController
public class EclExecutePredictBaseInfoController implements EclExecutePredictBaseInfoApi {

    @Resource
    private EclExecutePredictBaseInfoService eclExecutePredictBaseInfoService;

    @Override
    public R<Long> add(EclExecutePredictBaseInfoAddREQ req) {
        req.setSource(YesOrNoNumberEnum.YES.getCode());
        return R.ok(eclExecutePredictBaseInfoService.create(req));
    }

    @Override
    public R<PageR<EclExecutePredictBaseInfoListRSP>> list(EclExecutePredictBaseInfoListREQ req){
        Page<EclExecutePredictBaseInfo> data = eclExecutePredictBaseInfoService.list(req);
        List<EclExecutePredictBaseInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), EclExecutePredictBaseInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(EclExecutePredictBaseInfoRemoveREQ req){
        eclExecutePredictBaseInfoService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> calculation(@Valid EclExecutePredictBaseInfoDetailREQ req) {
        //异步执行
        CompletableFuture.supplyAsync(() -> {
            try {
                eclExecutePredictBaseInfoService.calculation(req.getId(), null);
            } catch (Exception e) {
                throw new MithrasException("测算失败");
            }
            return null;
        });
        return R.ok();
    }

}