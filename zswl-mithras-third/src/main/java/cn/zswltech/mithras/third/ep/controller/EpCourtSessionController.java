package cn.zswltech.mithras.third.ep.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ep.EpCourtSessionApi;
import cn.zswltech.mithras.dto.ep.EpCourtSessionRSP;
import cn.zswltech.mithras.third.ep.service.EpCourtSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ZHANGXIN
 */
@Slf4j
@RestController
public class EpCourtSessionController implements EpCourtSessionApi {

    @Resource
    private EpCourtSessionService epCourtSessionService;

    @Override
    public R<EpCourtSessionRSP> getEpCourtSession(@RequestParam Long id) {
        log.debug("getEpCourtSession id:{}", id);
        return epCourtSessionService.getEpCourtSession(id);
    }
}
