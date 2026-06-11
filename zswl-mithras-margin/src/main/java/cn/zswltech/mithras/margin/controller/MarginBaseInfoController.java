package cn.zswltech.mithras.margin.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.margin.MarginBaseInfoApi;
import cn.zswltech.mithras.dto.margin.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.margin.application.MarginBaseInfoApplicationService;
import cn.zswltech.mithras.margin.application.MarginRecordApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @create: 2022-08-17
 **/
@RestController
@Slf4j
public class MarginBaseInfoController implements MarginBaseInfoApi {
    @Resource
    private MarginBaseInfoApplicationService marginBaseInfoService;
    @Resource
    private MarginRecordApplicationService marginRecordService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<MarginBaseInfoListRSP>> list(@Valid MarginBaseInfoListREQ req) {
        return R.ok(marginBaseInfoService.list(req));
    }

    @Override
    public R<MarginBaseInfoRSP> detail(@Valid MarginBaseInfoDetailREQ req) {
        return R.ok(marginBaseInfoService.detail(req));
    }

    @Override
    public R<List<MarginRecordListRSP>> collectionList(@Valid MarginRecordListREQ req) {
        return R.ok(marginRecordService.list(req));
    }

    @Override
    public R<MarginRecordDetailRSP> collectionDetail(@Valid MarginRecordDetailREQ req) {
        return R.ok(marginRecordService.collectionDetail(req));
    }


    @Override
    public R<MarginRecordDetailRSP> recordDetail(@Valid MarginRecordDetailREQ req) {
        return R.ok(marginRecordService.detail(req));
    }

    @Override
    public R<MarginDeductDetailRSP> deductDetail(@Valid MarginRecordDetailREQ req) {
        return R.ok(marginRecordService.deductDetail(req));
    }

    @Override
    public R<Void> exportMarginList(@Valid MarginBaseInfoListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("保证金列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            marginBaseInfoService.exportList(req, httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出保证金列表发生未知异常", e);
            return R.fail("导出保证金列表发生未知异常");
        }
    }
}
