package cn.zswltech.mithras.api;

import cn.zswltech.mithras.dto.IndexDownloadREQ;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yibin
 */
@Api("首页下载")
public interface IndexDownloadApi {

    /**
     * 各模块首页列表下载
     */
    @PostMapping("index/download")
    void indexDownload(@RequestBody @Valid IndexDownloadREQ req);
}
