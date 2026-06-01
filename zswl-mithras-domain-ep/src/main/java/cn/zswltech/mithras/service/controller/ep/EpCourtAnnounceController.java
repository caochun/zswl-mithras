package cn.zswltech.mithras.service.controller.ep;


import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ep.EpCourtAnnounceApi;
import cn.zswltech.mithras.dto.ep.EpCourtAnnounceRSP;
import cn.zswltech.mithras.service.service.ep.EpCourtAnnounceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ZHANGXIN
 */
@Slf4j
@RestController
public class EpCourtAnnounceController implements EpCourtAnnounceApi {

    @Resource
    private EpCourtAnnounceService epCourtAnnounceService;

    @Override
    public R<EpCourtAnnounceRSP> getEpCourtAnnounce(@RequestParam Long id) {
        log.debug("getEpCourtAnnounce id:{}", id);
        return epCourtAnnounceService.getEpCourtAnnounce(id);
    }
}
