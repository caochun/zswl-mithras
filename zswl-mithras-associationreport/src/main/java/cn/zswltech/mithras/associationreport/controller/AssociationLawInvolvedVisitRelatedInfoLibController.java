package cn.zswltech.mithras.associationreport.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationLawInvolvedVisitRelatedInfoLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationLawInvolvedVisitRelatedInfoLibService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationLawInvolvedVisitRelatedInfoLib;

import java.util.List;

/**
* @description 涉法涉讼涉访信息表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationLawInvolvedVisitRelatedInfoLibController implements AssociationLawInvolvedVisitRelatedInfoLibApi {

    @Resource
    private AssociationLawInvolvedVisitRelatedInfoLibService associationLawInvolvedVisitRelatedInfoLibService;

    @Override
    public R<Void> add(AssociationLawInvolvedVisitRelatedInfoLibAddREQ req) {
        associationLawInvolvedVisitRelatedInfoLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationLawInvolvedVisitRelatedInfoLibModifyREQ req){
        associationLawInvolvedVisitRelatedInfoLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationLawInvolvedVisitRelatedInfoLibListRSP>> list(AssociationLawInvolvedVisitRelatedInfoLibListREQ req){
        Page<AssociationLawInvolvedVisitRelatedInfoLib> data = associationLawInvolvedVisitRelatedInfoLibService.list(req);
        List<AssociationLawInvolvedVisitRelatedInfoLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationLawInvolvedVisitRelatedInfoLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationLawInvolvedVisitRelatedInfoLibRemoveREQ req){
        associationLawInvolvedVisitRelatedInfoLibService.remove(req);
        return R.ok();
    }

}