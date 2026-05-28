package cn.zswltech.mithras.api.basedata;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.basedata.BaseDataSpecialDateInitREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataSpecialDateSaveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2022/11/4
 * @description
 */
@Api(tags = "基础数据-特殊日期相关接口")
public interface BaseDataSpecialDateApi {
    @ApiOperation("保存特殊日期")
    @PostMapping("/basedata/specialdate/save")
    R<Void> save(@RequestBody @Valid BaseDataSpecialDateSaveREQ req);


    /**
     * 线下执行；sql同步生产
     */
    @PostMapping("/basedata/specialdate/init")
    R<Void> init(@RequestBody @Valid BaseDataSpecialDateInitREQ req);
}
