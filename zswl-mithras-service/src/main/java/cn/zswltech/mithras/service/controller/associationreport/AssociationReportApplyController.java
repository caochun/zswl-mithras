package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationReportApplyApi;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationReportApplyService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportApply;

import java.util.List;

/**
* @description 金融局报表申请表
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationReportApplyController implements AssociationReportApplyApi {

    @Resource
    private AssociationReportApplyService associationReportApplyService;

    @Override
    public R<Void> add(AssociationReportApplyAddREQ req) {
        associationReportApplyService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationReportApplyModifyREQ req){
        associationReportApplyService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationReportApplyListRSP>> list(AssociationReportApplyListREQ req){
        Page<AssociationReportApply> data = associationReportApplyService.list(req);
        List<AssociationReportApplyListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationReportApplyListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationReportApplyRemoveREQ req){
        associationReportApplyService.remove(req);
        return R.ok();
    }

}