package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.share.DataShareREQ;
import cn.zswltech.mithras.dto.client.share.DataShareRSP;
import cn.zswltech.mithras.dto.client.share.DataShareUserREQ;
import cn.zswltech.mithras.dto.client.share.DataShareUserRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据共享接口
 * @author: jackerhe
 * @date: 2022/8/1 11:36 上午
 **/
@Api(tags = "集团数据共享")
public interface DataShareApi {

    /**
     * 获取客商信息
     * @author: jackerhe
     * @date: 2022/8/1 3:27 下午
     **/
    @GetMapping("/data/share/merchants")
    public R<DataShareRSP> getMerchants(@Valid DataShareREQ req);

    /**
     *同步客商信息
     * @author: jackerhe
     * @date: 2022/8/1 3:27 下午
     **/
    @GetMapping("/data/share/merchants/sync")
    public R<Boolean> syncMerchants();

    @PostMapping("/sync/mainCode")
    @ApiOperation("同步用户主数据编码")
    public R<DataShareUserRSP> syscMainCode(@RequestBody @Valid DataShareUserREQ req);

    @GetMapping("/sync/mainOrg")
    @ApiOperation("同步集团机构编号")
    R<List<SelectRSP>> syscMainOrg();

}
