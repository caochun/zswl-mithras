package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractOperationPrepareREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


/**
* 合同管理-首页，基本信息表
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同基本信息表-接口")
public interface ContractBaseInfoApi {
    @ApiOperation("准备进行合同操作")
    @PostMapping("/contract/operation/prepare")
    R<Void> prepareContractOperation(@RequestBody @Valid ContractOperationPrepareREQ req);

    @ApiOperation("保存调整说明")
    @PostMapping("/contract/base/info/adjustremark/save")
    R<Void> saveAdjustRemark(@RequestBody @Valid ContractAdjustRemarkREQ req);

    @ApiOperation("保存变更说明")
    @PostMapping("/contract/base/info/changeremark/save")
    R<Void> saveChangeRemark(@RequestBody @Valid ContractChangeRemarkREQ req);

    /**
     *合同首页模糊查询
     **/
    @ApiOperation("合同模糊查询")
    @PostMapping("/contract/review/query")
    R<List<ProjEstablishVagueListRSP>> vague(@RequestBody @Valid ProjEstablishVagueListREQ req);

    /**
     *新增合同基本信息表
     **/
    @ApiOperation("新增合同基本信息表")
    @PostMapping("/contract/base/info/add")
    R<ContractBaseInfoAddRSP> add(@RequestBody @Valid ContractBaseInfoAddREQ req);

    /**
     *修改合同基本信息表
     **/
    @ApiOperation("修改合同基本信息表")
    @PostMapping("/contract/base/info/modify")
    R<Void> modify(@RequestBody @Valid ContractBaseInfoModifyREQ req);

    /**
     *修改合同基本信息表-租赁物类型
     **/
    @ApiOperation("修改合同基本信息表-租赁物类型")
    @PostMapping("/contract/base/info/modify/leaseItem")
    R<Void> modifyLeaseItem(@RequestBody @Valid ContractBaseInfoModifyREQ req);

    /**
     * 首页合同基本信息表列表
     **/
    @ApiOperation("合同基本信息表列表")
    @PostMapping("/contract/base/info/list")
    R<PageR<ContractBaseInfoListRSP>> list(@RequestBody @Valid ContractBaseInfoListREQ req);

    @ApiOperation("删除合同基本信息表")
    @PostMapping("/contract/base/info/remove")
    R<Void> remove(@RequestBody @Valid ContractBaseInfoRemoveREQ req);

    @ApiOperation("作废合同")
    @PostMapping("/contract/cancel")
    R<Void> cancel(@RequestBody @Valid ContractBaseInfoRemoveREQ req);

    /**
     *合同基本信息详情
     **/
    @ApiOperation("合同基本信息详情")
    @GetMapping("/contract/base/info/detail")
    R<ContractBaseInfoDetailRSP> detail(@Valid ContractBaseInfoDetailREQ req);

    @ApiOperation("更新合同实际起租日期")
    @PostMapping("/contract/base/actualleasedate/update")
    R<Void> updateActualLeaseDate(@RequestBody @Valid ContractActualLeaseDateREQ req);

    @ApiOperation("导出合同相关信息")
    @GetMapping("/contract/base/info/export")
    void exportContract();

    @ApiOperation("合同比对承租人及担保人工商信息")
    @PostMapping("/contract/client/compare/business")
    R<List<ContractCompareBusinessRSP>> compareBusiness(@RequestBody @Valid ContractCompareBusinessREQ req);

    @ApiOperation("校验irr")
    @PostMapping("/contract/operation/checkIrr")
    R<Boolean> checkCombinedIrr(@RequestBody @Valid ContractOperationPrepareREQ req);
}