package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationMainBusinessLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationMainBusinessLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMainBusinessLib;


/**
* @description 金融协会报送-主要业务清单表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationMainBusinessLibController implements AssociationMainBusinessLibApi {

    @Resource
    private AssociationMainBusinessLibService associationMainBusinessLibService;

    @Override
    public R<Void> add(AssociationMainBusinessLibAddREQ req) {
        associationMainBusinessLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationMainBusinessLibModifyREQ req){
        associationMainBusinessLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationMainBusinessLibListRSP>> list(AssociationMainBusinessLibListREQ req){
        Page<AssociationMainBusinessLib> data = associationMainBusinessLibService.list(req);
        List<AssociationMainBusinessLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationMainBusinessLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationMainBusinessLibRemoveREQ req){
        associationMainBusinessLibService.remove(req);
        return R.ok();
    }

}