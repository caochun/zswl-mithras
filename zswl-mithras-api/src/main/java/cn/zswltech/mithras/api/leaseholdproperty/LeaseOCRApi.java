package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFileNameComparisonREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/15 11:50
 */
@Api(tags = "租赁物-OCR接口")
@RequestMapping
public interface LeaseOCRApi {

    @ApiOperation("判断是否存在相同文件名")
    @PostMapping("lease/ocr/fileNameComparison")
    R<List<String>> fileNameComparison(@Valid @RequestBody LeaseFileNameComparisonREQ req);
}
