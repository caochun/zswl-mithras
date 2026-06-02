package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationExternalFinancingLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationExternalFinancingLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationExternalFinancingLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationExternalFinancingLib;

import java.util.List;

/**
* @description 金融局报送-对外融资信息清单表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationExternalFinancingLibController implements AssociationExternalFinancingLibApi {

    @Resource
    private AssociationExternalFinancingLibService associationExternalFinancingLibService;

    @Override
    public R<Void> add(AssociationExternalFinancingLibAddREQ req) {
        associationExternalFinancingLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationExternalFinancingLibModifyREQ req){
        associationExternalFinancingLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationExternalFinancingLibListRSP>> list(AssociationExternalFinancingLibListREQ req){
        Page<AssociationExternalFinancingLib> data = associationExternalFinancingLibService.list(req);
        List<AssociationExternalFinancingLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationExternalFinancingLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationExternalFinancingLibRemoveREQ req){
        associationExternalFinancingLibService.remove(req);
        return R.ok();
    }

}