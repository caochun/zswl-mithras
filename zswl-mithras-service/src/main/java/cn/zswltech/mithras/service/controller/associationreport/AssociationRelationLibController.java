package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationRelationLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationRelationLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationRelationLib;


/**
* @description 金融协会报送-关联方信息汇总表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationRelationLibController implements AssociationRelationLibApi {

    @Resource
    private AssociationRelationLibService associationRelationLibService;

    @Override
    public R<Void> add(AssociationRelationLibAddREQ req) {
        associationRelationLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationRelationLibModifyREQ req){
        associationRelationLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationRelationLibListRSP>> list(AssociationRelationLibListREQ req){
        Page<AssociationRelationLib> data = associationRelationLibService.list(req);
        List<AssociationRelationLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationRelationLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationRelationLibRemoveREQ req){
        associationRelationLibService.remove(req);
        return R.ok();
    }

}