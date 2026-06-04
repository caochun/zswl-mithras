package cn.zswltech.mithras.creditreport.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditSearchProjectApi;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportProjectReviewAddDTO;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectCmd;
import cn.zswltech.mithras.dto.creditreport.CreditSearchProjectQuery;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.creditreport.service.CreditSearchProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class CreditSearchProjectController implements CreditSearchProjectApi {

    @Resource
    private CreditSearchProjectService creditSearchProjectService;
    @Resource
    private CreditReportBaseInfoService creditReportBaseInfoService;

    @Override
    public R<CreditReportProjectReviewAddDTO> showCreditReportByProjId(CreditSearchProjectCmd cmd) {
        return R.ok(creditSearchProjectService.showCreditReportByProjId(cmd));
    }

    @Override
    public R<PageR<CreditReportListDTO>> list(CreditSearchProjectQuery query) {
        return R.ok(creditSearchProjectService.list(query));
    }

    @Override
    public R<Void> delete(Long id) {
        //creditSearchProjectService.delete(id);
        creditReportBaseInfoService.remove(id);
        return R.ok();
    }
}
