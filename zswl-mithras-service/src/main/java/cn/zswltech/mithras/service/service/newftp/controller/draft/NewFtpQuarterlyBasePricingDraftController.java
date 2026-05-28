package cn.zswltech.mithras.service.service.newftp.controller.draft;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.draft.NewFtpQuarterlyBasePricingDraftApi;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingDetailRsp;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.service.newftp.service.drift.NewFtpQuarterlyBasePricingDraftService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yangxiong
 * @date 2024/3/29/16:15
 * @description
 */
@RestController
public class NewFtpQuarterlyBasePricingDraftController implements NewFtpQuarterlyBasePricingDraftApi {

    @Resource
    private NewFtpQuarterlyBasePricingDraftService newFtpQuarterlyBasePricingService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private NewFtpBaseInfoService newFtpBaseInfoService;

    @Override
    public R<Void> modify(NewFtpQuarterlyBasePricingModifyREQ req) {
        newFtpQuarterlyBasePricingService.modify(req);
        return R.ok();
    }

    @Override
    public R<NewFtpQuarterlyBasePricingDetailRsp> detail(NewFtpDetailReq req) {
        return R.ok(newFtpQuarterlyBasePricingService.detail(req.getMainId()));
    }

    @SneakyThrows
    @Override
    public R<Void> downLoad(@Valid NewFtpDetailReq req) {
        NewFtpBaseInfo byId = newFtpBaseInfoService.getById(req.getMainId());
        if(ObjectUtil.isEmpty(byId)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(byId.getMonth().format(DatePattern.NORM_MONTH_FORMATTER) + "FTP/业务定价指导.xlsx",
                        StandardCharsets.UTF_8.name()));
        newFtpQuarterlyBasePricingService.exportExcel(req.getMainId(), response.getOutputStream());
        return R.ok();
    }
}
