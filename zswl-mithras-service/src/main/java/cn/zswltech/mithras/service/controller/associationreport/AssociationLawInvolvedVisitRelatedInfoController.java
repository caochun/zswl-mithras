package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationLawInvolvedVisitRelatedInfoApi;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationLawInvolvedVisitRelatedInfoRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationLawInvolvedVisitRelatedInfoService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfo;


/**
* @description 涉法涉讼涉访信息表
* @author hspcadmin
* @date 2025-08-27
*/
@RestController
public class AssociationLawInvolvedVisitRelatedInfoController implements AssociationLawInvolvedVisitRelatedInfoApi {

    @Resource
    private AssociationLawInvolvedVisitRelatedInfoService associationLawInvolvedVisitRelatedInfoService;

    @Override
    public R<Void> add(AssociationLawInvolvedVisitRelatedInfoAddREQ req) {
        associationLawInvolvedVisitRelatedInfoService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationLawInvolvedVisitRelatedInfoModifyREQ req){
        associationLawInvolvedVisitRelatedInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationLawInvolvedVisitRelatedInfoListRSP>> list(AssociationLawInvolvedVisitRelatedInfoListREQ req){
        Page<AssociationLawInvolvedVisitRelatedInfo> data = associationLawInvolvedVisitRelatedInfoService.list(req);
        List<AssociationLawInvolvedVisitRelatedInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationLawInvolvedVisitRelatedInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationLawInvolvedVisitRelatedInfoRemoveREQ req){
        associationLawInvolvedVisitRelatedInfoService.remove(req);
        return R.ok();
    }


}