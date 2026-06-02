package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationSeniorExecutiveInfoLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationSeniorExecutiveInfoLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationSeniorExecutiveInfoLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationSeniorExecutiveInfoLib;

import java.util.List;

/**
* @description 高管信息一览表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationSeniorExecutiveInfoLibController implements AssociationSeniorExecutiveInfoLibApi {

    @Resource
    private AssociationSeniorExecutiveInfoLibService associationSeniorExecutiveInfoLibService;

    @Override
    public R<Void> add(AssociationSeniorExecutiveInfoLibAddREQ req) {
        associationSeniorExecutiveInfoLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationSeniorExecutiveInfoLibModifyREQ req){
        associationSeniorExecutiveInfoLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationSeniorExecutiveInfoLibListRSP>> list(AssociationSeniorExecutiveInfoLibListREQ req){
        Page<AssociationSeniorExecutiveInfoLib> data = associationSeniorExecutiveInfoLibService.list(req);
        List<AssociationSeniorExecutiveInfoLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationSeniorExecutiveInfoLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationSeniorExecutiveInfoLibRemoveREQ req){
        associationSeniorExecutiveInfoLibService.remove(req);
        return R.ok();
    }

}