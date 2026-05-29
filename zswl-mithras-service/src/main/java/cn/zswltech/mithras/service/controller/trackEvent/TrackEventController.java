package cn.zswltech.mithras.service.controller.trackEvent;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.trackEvent.TrackEventApi;
import cn.zswltech.mithras.dto.OrgUserRSP;
import cn.zswltech.mithras.dto.UserRSP;
import cn.zswltech.mithras.dto.trackEvent.*;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.trackEvent.TrackEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class TrackEventController implements TrackEventApi {

    @Resource
    private TrackEventService trackEventService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Override
    public R<PageR<TrackEventListRSP>> list(TrackEventListREQ req) {
        return R.ok(trackEventService.queryList(req));
    }

    @Override
    public R<TrackEventDetailRSP> detail(Long id) {
        return R.ok(trackEventService.detail(id));
    }

    @Override
    public R<TrackEventContractInfoRSP> contractInfo(TrackEventContractInfoREQ req) {
        return R.ok(trackEventService.contractInfo(req));
    }

    @Override
    public R<Boolean> add(TrackEventAddREQ rsp) {
        return R.ok(trackEventService.add(rsp));
    }

//    @Override
//    public R<Boolean> update(TrackEventUpdateREQ rsp) {
//        return R.ok(trackEventService.edit(rsp));
//    }

    @Override
    public R<Boolean> close(Long id) {
        return R.ok(trackEventService.close(id));
    }

    @Override
    public R<List<UserRSP>> queryProcess() {
        return R.ok(trackEventService.queryProcess());
    }

    @Override
    public R<List<String>> contractCodeList() {
        return R.ok(trackEventService.contractCodeList());
    }

    @Override
    public R<List<String>> projNameList() {
        return null;
    }

    @Override
    public void download(@RequestBody TrackEventMainREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("跟踪事项" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            trackEventService.download(httpServletResponse.getOutputStream(),req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出跟踪事项发生未知异常", e);
            throw new MithrasException("导出跟踪事项发生未知异常");
        }
    }

    @Override
    public R<Void> testEffect() {
        trackEventService.startEffect();
        return R.ok();
    }


}
