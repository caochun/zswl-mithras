package cn.zswltech.mithras.ep.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ep.EpChangeInfoApi;
import cn.zswltech.mithras.dto.ep.EpChangeInfoRSP;
import cn.zswltech.mithras.ep.service.EpChangeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 张欣
 */
@Slf4j
@RestController
public class EpChangeInfoController implements EpChangeInfoApi {

    @Resource
    private EpChangeInfoService epChangeInfoService;
    @Override
    public R<EpChangeInfoRSP> getEpChangeInfo(@RequestParam Long id) {
        log.debug("getEpChangeInfo id:{}", id);
        return epChangeInfoService.getEpChangeInfo(id);
    }
}
