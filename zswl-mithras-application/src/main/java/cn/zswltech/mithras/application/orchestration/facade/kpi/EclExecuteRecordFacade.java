package cn.zswltech.mithras.application.orchestration.facade.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.kpi.application.EclExecuteRecordApplicationService;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.kpi.mapper.model.EclExecuteRecord;
import cn.zswltech.mithras.kpi.mapper.model.EclExecuteRecordLib;
import cn.zswltech.mithras.kpi.service.EclExecuteRecordLibService;
import cn.zswltech.mithras.application.orchestration.kpi.EclExecuteRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* @description 资产减值记录表
* @author vico
* @date 2025-09-28
*/
@Service
public class EclExecuteRecordFacade implements EclExecuteRecordApplicationService {

    @Resource
    private EclExecuteRecordService eclExecuteRecordService;
    @Resource
    private EclExecuteRecordLibService eclExecuteRecordLibService;

    @Override
    public R<Void> add(EclExecuteRecordAddREQ req) {
        eclExecuteRecordService.add(req);
        return R.ok();
    }

    @Override
    public R<String> addCheck(@Valid EclExecuteRecordAddREQ req) {
        return R.ok(eclExecuteRecordService.addCheck(req));
    }

    @Override
    public R<Void> importFile(@Valid EclExecuteRecordImportREQ req) {
        eclExecuteRecordService.importFile(req);
        return R.ok();
    }

    @Override
    public R<Set<String>> importFileCheck(@Valid EclExecuteRecordImportREQ req) {
        return R.ok(eclExecuteRecordService.importFileCheck(req));
    }

    @Override
    public R<Void> modify(EclExecuteRecordModifyREQ req){
        eclExecuteRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<EclExecuteRecordListRSP>> list(EclExecuteRecordListREQ req) {
        if (req.isFastFlag()) {
            LocalDateTime localDateTime = null;
            if (ObjectUtil.isNotEmpty(req.getCreateTimeTo())){
                localDateTime = req.getCreateTimeTo().atTime(23, 59, 59);
            }
            Page<EclExecuteRecord> data = eclExecuteRecordService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclExecuteRecord>lambdaQuery()
                    .like(ObjectUtil.isNotEmpty(req.getClientName()), EclExecuteRecord::getClientName, req.getClientName())
                    .like(ObjectUtil.isNotEmpty(req.getContractCode()), EclExecuteRecord::getContractCode, req.getContractCode())
                    .eq(ObjectUtil.isNotEmpty(req.getInnerMdLevel()), EclExecuteRecord::getInnerMdLevel, req.getInnerMdLevel())
                    .eq(ObjectUtil.isNotEmpty(req.getGroup()), EclExecuteRecord::getGroup, req.getGroup())
                    .eq(ObjectUtil.isNotEmpty(req.getClassify()), EclExecuteRecord::getClassify, req.getClassify())
                    .eq(ObjectUtil.isNotEmpty(req.getEclStep()), EclExecuteRecord::getEclStep, req.getEclStep())
                    .eq(ObjectUtil.isNotEmpty(req.getLeaseType()), EclExecuteRecord::getLeaseType, req.getLeaseType())
                    .ge(ObjectUtil.isNotEmpty(req.getCreateTimeFrom()), EclExecuteRecord::getUpdateTime, req.getCreateTimeFrom())
                    .le(ObjectUtil.isNotEmpty(req.getCreateTimeTo()), EclExecuteRecord::getUpdateTime, localDateTime)
                    .orderByDesc(EclExecuteRecord::getUpdateTime));
            List<EclExecuteRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), EclExecuteRecordListRSP.class);
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            Page<EclExecuteRecordLib> data = eclExecuteRecordService.listLib(req);
            List<EclExecuteRecordListRSP> list = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(data.getRecords())) {
                data.getRecords().forEach(e -> {
                    EclExecuteRecordListRSP eclExecuteRecordListRSP = BeanUtil.copyProperties(e, EclExecuteRecordListRSP.class);
                    eclExecuteRecordListRSP.setCreateTime(e.getCreateTime());
                    eclExecuteRecordListRSP.setCreateBy(e.getCreateBy());
                    eclExecuteRecordListRSP.setUpdateBy(e.getUpdateBy());
                    eclExecuteRecordListRSP.setUpdateTime(e.getUpdateTime());
                    list.add(eclExecuteRecordListRSP);
                });
            }
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        }
    }

    @Override
    public R<List<EclExecuteRecordListRSP>> listCompare(@Valid EclExecuteRecordListREQ req) {
        List<EclExecuteRecord> records = eclExecuteRecordService.listCompare(req);
        return R.ok(BeanUtil.copyToList(records, EclExecuteRecordListRSP.class));
    }

    @Override
    public R<Void> remove(EclExecuteRecordRemoveREQ req){
        eclExecuteRecordService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> allRecordRemove(@Valid EclExecuteRecordRemoveREQ req) {
        eclExecuteRecordLibService.remove(req);
        return R.ok();
    }

}