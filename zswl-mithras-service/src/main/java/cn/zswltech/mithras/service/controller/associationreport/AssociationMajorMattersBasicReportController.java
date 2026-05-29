package cn.zswltech.mithras.service.controller.associationreport;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.associationreport.AssociationMajorMattersBasicReportApi;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportListRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMajorMattersBasicReportRemoveREQ;
import cn.zswltech.mithras.associationreport.service.AssociationMajorMattersBasicReportService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersBasicReport;


/**
* @description 重大事项报告表-基本信息
* @author hspcadmin
* @date 2025-08-27
*/
@RestController
public class AssociationMajorMattersBasicReportController implements AssociationMajorMattersBasicReportApi {

    @Resource
    private AssociationMajorMattersBasicReportService associationMajorMattersBasicReportService;

    @Override
    public R<Void> add(AssociationMajorMattersBasicReportAddREQ req) {
        associationMajorMattersBasicReportService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(AssociationMajorMattersBasicReportModifyREQ req){
        associationMajorMattersBasicReportService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AssociationMajorMattersBasicReportListRSP>> list(AssociationMajorMattersBasicReportListREQ req){
        Page<AssociationMajorMattersBasicReport> data = associationMajorMattersBasicReportService.list(req);
        List<AssociationMajorMattersBasicReportListRSP> list = BeanUtil.copyToList(data.getRecords(), AssociationMajorMattersBasicReportListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(AssociationMajorMattersBasicReportRemoveREQ req){
        associationMajorMattersBasicReportService.remove(req);
        return R.ok();
    }

}