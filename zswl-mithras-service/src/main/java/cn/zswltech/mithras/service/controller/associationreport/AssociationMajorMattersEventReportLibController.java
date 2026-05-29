package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationMajorMattersEventReportLibApi;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportLibAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportLibModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportLibListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportLibListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportLibRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationMajorMattersEventReportLibService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersEventReportLib;


/**
* @description 重大事项报告表-重大事项报告情况(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@RestController
public class AssociationMajorMattersEventReportLibController implements AssociationMajorMattersEventReportLibApi {

    @Resource
    private AssociationMajorMattersEventReportLibService associationMajorMattersEventReportLibService;

    @Override
    public R<Void> add(AssociationMajorMattersEventReportLibAddREQ req) {
        associationMajorMattersEventReportLibService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationMajorMattersEventReportLibModifyREQ req){
        associationMajorMattersEventReportLibService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationMajorMattersEventReportLibListRSP>> list(AssociationMajorMattersEventReportLibListREQ req){
        Page<AssociationMajorMattersEventReportLib> data = associationMajorMattersEventReportLibService.list(req);
        List<AssociationMajorMattersEventReportLibListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationMajorMattersEventReportLibListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationMajorMattersEventReportLibRemoveREQ req){
        associationMajorMattersEventReportLibService.remove(req);
        return R.ok();
    }

}