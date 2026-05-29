package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationBusinessSituationLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationBusinessSituationLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBusinessSituationLib;


/**
* @description 业务情况表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationBusinessSituationLibController implements AssociationBusinessSituationLibApi {

    @Resource
    private AssociationBusinessSituationLibService associationBusinessSituationLibService;

    @Override
    public R<Void> add(AssociationBusinessSituationLibAddREQ req) {
        associationBusinessSituationLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationBusinessSituationLibModifyREQ req){
        associationBusinessSituationLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationBusinessSituationLibListRSP>> list(AssociationBusinessSituationLibListREQ req){
        Page<AssociationBusinessSituationLib> data = associationBusinessSituationLibService.list(req);
        List<AssociationBusinessSituationLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationBusinessSituationLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationBusinessSituationLibRemoveREQ req){
        associationBusinessSituationLibService.remove(req);
        return R.ok();
    }

}