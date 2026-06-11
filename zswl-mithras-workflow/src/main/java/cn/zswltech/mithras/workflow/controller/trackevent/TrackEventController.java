package cn.zswltech.mithras.workflow.controller.trackevent;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.trackevent.TrackEventApi;
import cn.zswltech.mithras.dto.UserRSP;
import cn.zswltech.mithras.dto.trackevent.TrackEventAddREQ;
import cn.zswltech.mithras.dto.trackevent.TrackEventContractInfoREQ;
import cn.zswltech.mithras.dto.trackevent.TrackEventContractInfoRSP;
import cn.zswltech.mithras.dto.trackevent.TrackEventDetailRSP;
import cn.zswltech.mithras.dto.trackevent.TrackEventListREQ;
import cn.zswltech.mithras.dto.trackevent.TrackEventListRSP;
import cn.zswltech.mithras.dto.trackevent.TrackEventMainREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.trackevent.TrackEventApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Slf4j
public class TrackEventController implements TrackEventApi {

    @Resource
    private TrackEventApplicationService trackEventApplicationService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<TrackEventListRSP>> list(TrackEventListREQ req) {
        return trackEventApplicationService.list(req);
    }

    @Override
    public R<TrackEventDetailRSP> detail(Long id) {
        return trackEventApplicationService.detail(id);
    }

    @Override
    public R<TrackEventContractInfoRSP> contractInfo(TrackEventContractInfoREQ req) {
        return trackEventApplicationService.contractInfo(req);
    }

    @Override
    public R<Boolean> add(TrackEventAddREQ rsp) {
        return trackEventApplicationService.add(rsp);
    }

    @Override
    public R<Boolean> close(Long id) {
        return trackEventApplicationService.close(id);
    }

    @Override
    public R<List<UserRSP>> queryProcess() {
        return trackEventApplicationService.queryProcess();
    }

    @Override
    public R<List<String>> contractCodeList() {
        return trackEventApplicationService.contractCodeList();
    }

    @Override
    public R<List<String>> projNameList() {
        return trackEventApplicationService.projNameList();
    }

    @Override
    public void download(@RequestBody TrackEventMainREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("跟踪事项" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            trackEventApplicationService.download(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出跟踪事项发生未知异常", e);
            throw new MithrasException("导出跟踪事项发生未知异常");
        }
    }

    @Override
    public R<Void> testEffect() {
        return trackEventApplicationService.testEffect();
    }
}
