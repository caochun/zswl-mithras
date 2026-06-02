package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationBasicSituationLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBasicSituationLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationBasicSituationLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBasicSituationLib;

import java.util.List;

/**
* @description 基本情况统计((流程节点记录版本表))
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationBasicSituationLibController implements AssociationBasicSituationLibApi {

    @Resource
    private AssociationBasicSituationLibService associationBasicSituationLibService;

    @Override
    public R<Void> add(AssociationBasicSituationLibAddREQ req) {
        associationBasicSituationLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationBasicSituationLibModifyREQ req){
        associationBasicSituationLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationBasicSituationLibListRSP>> list(AssociationBasicSituationLibListREQ req){
        Page<AssociationBasicSituationLib> data = associationBasicSituationLibService.list(req);
        List<AssociationBasicSituationLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationBasicSituationLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationBasicSituationLibRemoveREQ req){
        associationBasicSituationLibService.remove(req);
        return R.ok();
    }

}