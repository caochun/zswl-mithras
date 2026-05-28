package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrBatchApi;
import cn.zswltech.mithras.dto.report.batch.BatchHeadREQ;
import cn.zswltech.mithras.dto.report.batch.BatchHeadRSP;
import cn.zswltech.mithras.dto.report.batch.BatchListREQ;
import cn.zswltech.mithras.dto.report.batch.BatchListRSP;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.BatchRecordService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 征信报送-批次接口
 *
 * @author wangchuanhao
 * @date 2023/1/13 10:29 AM
 */
@RestController
public class CrBatchController implements CrBatchApi {

    @Resource
    private BatchRecordService batchRecordService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<BatchListRSP>> increList(BatchListREQ req) {
        return R.ok(batchRecordService.increList(req));
    }

    @Override
    public R<BatchHeadRSP> batchReason(BatchHeadREQ req) {
        return R.ok(batchRecordService.batchReason(req.getProcessInstanceId()));
    }

}
