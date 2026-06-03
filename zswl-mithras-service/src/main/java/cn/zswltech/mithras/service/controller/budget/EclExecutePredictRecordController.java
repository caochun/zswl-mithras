package cn.zswltech.mithras.service.controller.budget;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.budget.EclExecutePredictRecordApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.EclExecutePredictRecord;
import cn.zswltech.mithras.service.service.budget.EclExecutePredictRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
* @description 资产减值预测详情记录表
* @author vico
* @date 2025-10-14
*/
@RestController
public class EclExecutePredictRecordController implements EclExecutePredictRecordApi {

    @Resource
    private EclExecutePredictRecordService eclExecutePredictRecordService;

    @Override
    public R<Void> add(EclExecutePredictRecordAddREQ req) {
        eclExecutePredictRecordService.add(req);
        return R.ok();
    }

    @Override
    public R<String> addCheck(@Valid EclExecutePredictRecordAddREQ req) {
        return R.ok(eclExecutePredictRecordService.addCheck(req));
    }

    @Override
    public R<Void> importFile(@Valid EclExecutePredictRecordImportREQ req) {
        eclExecutePredictRecordService.importFile(req);
        return R.ok();
    }

    @Override
    public R<Set<String>> importFileCheck(@Valid EclExecutePredictRecordImportREQ req) {
        return R.ok(eclExecutePredictRecordService.importFileCheck(req));
    }

    @Override
    public R<Void> modify(EclExecutePredictRecordModifyREQ req){
        eclExecutePredictRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<EclExecutePredictRecordListRSP>> list(EclExecutePredictRecordListREQ req){
        Page<EclExecutePredictRecord> data = eclExecutePredictRecordService.list(req);
        List<EclExecutePredictRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), EclExecutePredictRecordListRSP.class);
        /*if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(e -> {
                if (ObjectUtil.isNotEmpty(e.getPromotionResultHandle())) {
                    e.setPromotionResult(e.getPromotionResultHandle());
                }
            });
        }*/
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(EclExecutePredictRecordRemoveREQ req){
        eclExecutePredictRecordService.remove(req);
        return R.ok();
    }

}