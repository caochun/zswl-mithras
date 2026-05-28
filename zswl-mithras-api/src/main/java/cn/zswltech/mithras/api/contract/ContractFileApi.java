package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.file.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Api(tags = "合同文本相关接口")
public interface ContractFileApi {
    @ApiOperation("获取变更材料信息")
    @PostMapping("/contract/file/exchange/list")
    R<List<ContractFileGroupRSP>> listExchangeMaterialGroup(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("获取合同文件分组信息")
    @PostMapping("/contract/file/list")
    R<List<ContractFileGroupRSP>> listContractGroup(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("上传合同文件")
    @PostMapping("/contract/file/upload")
    R<Long> upload(@Valid ContractFileUploadREQ contractFileUploadREQ);

    @ApiOperation("生成合同文件")
    @PostMapping("/contract/file/generate")
    R<Void> generate(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("删除合同文件")
    @PostMapping("/contract/file/remove")
    R<Void> remove(@RequestBody @Valid ContractFileRemoveREQ contractFileRemoveREQ);

    @ApiOperation("保存合同文本类型")
    @PostMapping("/contract/text/info/save")
    R<Void> saveTextInfo(@RequestBody @Valid ContractTextInfoREQ req);

    @ApiOperation("获取合同文本类型")
    @PostMapping("/contract/text/info/get")
    R<ContractTextInfoRSP> getTextInfo(@RequestBody @Valid ContractSingleIdREQ req);
}
