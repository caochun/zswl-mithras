package cn.zswltech.mithras.associationreport.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationBasicSituationApi;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationBasicSituationService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBasicSituation;

import java.util.List;

/**
* @description 基本情况统计表
* @author hspcadmin
* @date 2025-08-22
*/
@RestController
public class AssociationBasicSituationController implements AssociationBasicSituationApi {

    @Resource
    private AssociationBasicSituationService associationBasicSituationService;

    @Override
    public R<Void> add(AssociationBasicSituationAddREQ req) {
        associationBasicSituationService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationBasicSituationModifyREQ req){
        associationBasicSituationService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationBasicSituationListRSP>> list(AssociationBasicSituationListREQ req){
        Page<AssociationBasicSituation> data = associationBasicSituationService.list(req);
        List<AssociationBasicSituationListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationBasicSituationListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationBasicSituationRemoveREQ req){
        associationBasicSituationService.remove(req);
        return R.ok();
    }

}