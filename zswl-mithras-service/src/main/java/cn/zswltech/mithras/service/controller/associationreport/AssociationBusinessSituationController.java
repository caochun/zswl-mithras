package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationBusinessSituationApi;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationBusinessSituationRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationBusinessSituationService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBusinessSituation;

/**
* @description 业务情况表
* @author vico
* @date 2025-04-18
*/
@RestController
public class AssociationBusinessSituationController implements AssociationBusinessSituationApi {

    @Resource
    private AssociationBusinessSituationService associationBusinessSituationService;

    @Override
    public R<Void> add(AssociationBusinessSituationAddREQ req) {
        associationBusinessSituationService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationBusinessSituationModifyREQ req){
        associationBusinessSituationService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationBusinessSituationListRSP>> list(AssociationBusinessSituationListREQ req){
        Page<AssociationBusinessSituation> data = associationBusinessSituationService.list(req);
        List<AssociationBusinessSituationListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationBusinessSituationListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationBusinessSituationRemoveREQ req){
        associationBusinessSituationService.remove(req);
        return R.ok();
    }

}