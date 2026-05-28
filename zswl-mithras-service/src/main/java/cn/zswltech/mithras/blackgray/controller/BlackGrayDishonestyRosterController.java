package cn.zswltech.mithras.blackgray.controller;


import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayDishonestyRosterREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayDishonestyRosterRSP;
import cn.zswltech.mithras.blackgray.service.BlackGrayDishonestyRosterService;
import cn.zswltech.mithras.blackgray.service.BlackGrayLibraryService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "黑灰名单临时数据查询-接口")
public class BlackGrayDishonestyRosterController {

    @Autowired
    private BlackGrayDishonestyRosterService blackGrayDishonestyRosterService;

    @Autowired
    private BlackGrayLibraryService blackGrayLibraryService;

    @ApiOperation(value = "分页查询")
    @PostMapping("/black/gray/dishonesty/roster/query")
    public R<PageInfo<BlackGrayDishonestyRosterRSP>> query(@RequestBody BlackGrayDishonestyRosterREQ req) {
        return blackGrayDishonestyRosterService.query(req);
    }

}
