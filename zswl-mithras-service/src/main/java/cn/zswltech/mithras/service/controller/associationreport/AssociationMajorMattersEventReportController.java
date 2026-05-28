package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationMajorMattersEventReportApi;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersEventReportRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationMajorMattersEventReportService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersEventReport;

import java.util.List;

/**
* @description 重大事项报告表-重大事项报告情况
* @author hspcadmin
* @date 2025-08-27
*/
@RestController
public class AssociationMajorMattersEventReportController implements AssociationMajorMattersEventReportApi {

    @Resource
    private AssociationMajorMattersEventReportService associationMajorMattersEventReportService;

    @Override
    public R<Void> add(AssociationMajorMattersEventReportAddREQ req) {
        associationMajorMattersEventReportService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationMajorMattersEventReportModifyREQ req){
        associationMajorMattersEventReportService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationMajorMattersEventReportListRSP>> list(AssociationMajorMattersEventReportListREQ req){
        Page<AssociationMajorMattersEventReport> data = associationMajorMattersEventReportService.list(req);
        List<AssociationMajorMattersEventReportListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationMajorMattersEventReportListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationMajorMattersEventReportRemoveREQ req){
        associationMajorMattersEventReportService.remove(req);
        return R.ok();
    }

}