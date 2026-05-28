package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.file.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author junke
 */
@Api(value = "客户接口", tags = "客户接口")
public interface ClientApi {
    @ApiOperation("获取可申请权限的客户")
    @PostMapping("/client/noauthority/list")
    R<PageR<ClientListRSP>> listNoAuthrotiy(@RequestBody @Valid ClientListREQ req);

    @ApiOperation("客户列表")
    @PostMapping("/client/list")
    R<PageR<ClientListRSP>> list(@RequestBody @Valid ClientListREQ req);

//    @ApiOperation("新的客户列表")
//    @PostMapping("/client/new/list")
//    R<List<ClientListRSP>> newList(@RequestBody @Valid ClientListREQ req);

    @ApiOperation("新的客户列表")
    @PostMapping("/client/new/list")
    R<PageR<ClientListRSP>> newList(@RequestBody @Valid ClientListREQ req);

    @ApiOperation(value = "新增法人客户", notes = "" +
            "1、新增成功返回天眼查查询到的基本信息\n" +
            "2、返回-511时，代表获取天眼查信息失败\n" +
            "3、返回-512，代表选择的客户分类不对")
    @PostMapping("/client/corp/add")
    R<CorpCommerceInfoAddREQ> addCorporation(@RequestBody @Valid ClientCorpAddREQ req);

    @ApiOperation("新增自然人客户")
    @PostMapping("/client/normal/add")
    R<Long> addNormal(@RequestBody @Valid ClientNormalAddREQ req);

    @ApiOperation("判断客户名称使用已经存在")
    @PostMapping("/client/name/exist")
    R<Boolean> userNameExist(@RequestBody @Valid ClientNameExistREQ req);

    @ApiOperation("客户删除")
    @PostMapping("/client/remove")
    R<Boolean> removeClient(@RequestBody @Valid ClientRemoveREQ req);

    @ApiOperation("基本信息同步")
    @PostMapping("/client/sync")
    R<ClientSyncRSP> sync(@RequestBody @Valid ClientSyncREQ req);

    @ApiOperation("从天眼查获取工商信息")
    @PostMapping("/client/tyc/commerceInfo")
    R<CorpCommerceInfoDetailRSP> tycCommerceInfo(@RequestBody @Valid ClientSyncREQ req);

    @ApiOperation("客户生效（或提交审批）")
    @PostMapping("/client/effect")
    R<Void> effect(@RequestBody @Valid ClientEffectREQ req);

    @ApiOperation("获取客户按钮状态")
    @PostMapping("/client/button/status")
    R<ClientButtonStatusRSP> buttonStatus(@RequestBody @Valid ClientButtonStatusREQ req);

    @ApiOperation("创建客户移交申请记录")
    @PostMapping("/client/transfer/apply/create")
    R<ClientTransferApplyRSP> createTransferApply();

    @ApiOperation("通过批次编号查询移交申请信息")
    @PostMapping("/client/transfer/apply/queryByBatchNo")
    R<ClientTransferApplyRSP> findTransferApplyByBatchNo(@RequestBody @Valid ClientTransferApplyQueryREQ req);

    @ApiOperation("获取指定用户负责的客户列表")
    @PostMapping("/client/list/bySponsors")
    R<List<SponsorClientListRSP>> sponsorClientList(@RequestBody @Valid SponsorClientListREQ req);

    @ApiOperation("新的获取指定用户负责的客户列表")
    @PostMapping("/client/newList/BySponsors")
    R<List<SponsorClientListNewRSP>> sponsorClientNewList(@RequestBody @Valid SponsorClientListNewREQ req);

    @ApiOperation("提交转移指定用户负责的客户")
    @PostMapping("/client/transfer/submit")
    R<Void> sponsorClientSubmit(@RequestBody @Valid SponsorClientSubmitREQ req);

    @ApiOperation("新的提交转移指定用户负责的客户")
    @PostMapping("/client/newTransfer/submit")
    R<Void> sponsorClientNewSubmit(@RequestBody @Valid SponsorClientSubmitNewREQ req);


    @ApiOperation("新的提交转移编辑用户负责的客户")
    @PostMapping("/client/newTransfer/modify")
    R<Void> sponsorClientNewModify(@RequestBody @Valid SponsorClientModifyNewREQ req);

    @ApiOperation("新的提交批量转移编辑用户负责的客户")
    @PostMapping("/client/newTransfer/modifyBatch")
    R<Void> sponsorClientNewModifyBatch(@RequestBody @Valid SponsorClientModifyNewBatchREQ req);

    @ApiOperation("取消操作新的提交转移编辑用户负责的客户")
    @PostMapping("/client/newTransfer/remove")
    R<Void> sponsorClientNewRemove(@RequestBody @Valid SponsorClientRemoveNewREQ req);


    @ApiOperation("客户移交审批详情")
    @PostMapping("/client/transfer/detail")
    R<SponsorClientDetailRSP> transferDetail(@RequestBody @Valid SponsorClientDetailREQ req);

    @ApiOperation("新的客户移交审批详情")
    @PostMapping("/client/newTransfer/detail")
    R<SponsorClientDetailNewRSP> newTransferDetail(@RequestBody @Valid SponsorClientDetailNewREQ req);


    @ApiOperation("新的客户移交审批详情编辑信息")
    @PostMapping("/client/newTransfer/detail/modify")
    R<Void> newTransferDetailModify(@RequestBody @Valid SponsorClientDetailNewModifyREQ req);


    @ApiOperation("获取当前用户按钮状态")
    @PostMapping("/currentUser/button/status")
    R<UserButtonStatusRSP> buttonStatus();

    @ApiOperation("客户权限生效（或提交审批）")
    @PostMapping("/client/authority/effect")
    R<Void> authorityEffect(@RequestBody @Valid ClientAuthorityEffectREQ req);

    @ApiOperation("客户申办权限申请（或提交审批）")
    @PostMapping("/client/apply/effect")
    R<Void> applyEffect(@RequestBody @Valid ClientApplyEffectREQ req);

    @ApiOperation("客户申办权限信息保存")
    @PostMapping("/client/apply/modify")
    R<Void> applyModify(@RequestBody @Valid ClientApplyModifyREQ req);


    @ApiOperation("客户申办权限申请客户信息")
    @PostMapping("/client/apply/detail")
    R<ClientApplyDetailRSP> applyDetail(@RequestBody @Valid ClientApplyDetailREQ req);

    @ApiOperation("校验客户申办权限申请客户信息")
    @PostMapping("/client/apply/validate")
    R<Void> applyValidate(@RequestBody @Valid ClientApplyDetailREQ req);

    @ApiOperation("导出客户移交信息模版")
    @PostMapping("/client/transfer/export")
    R<Void> exportClientTransfer(@RequestBody @Valid ClientTransferExportREQ clientTransferExportREQ);


    @ApiOperation("客户详情页面是否有编辑查看权限")
    @PostMapping("/client/apply/own")
    R<ClientOwnApplyDetailRSP> applyOwn(@RequestBody @Valid ClientOwnApplyDetailREQ req);


    @ApiOperation("得到客户状态和管控权限")
    @PostMapping("/client/apply/status")
    R<ClientApplyStatusRSP> status(@RequestBody @Valid ClientApplyStatusREQ req);

    @ApiOperation("检查客户是否被占有")
    @PostMapping("/client/apply/occupy")
    R<ClientApplyOccupyRSP> checkOccupy(@RequestBody @Valid ClientApplyOccupyREQ req);

    @ApiOperation("检查客户是否被释放(临时方法)")
    @PostMapping("/client/release")
    R<Void> release(@RequestBody @Valid ClientReleaseREQ req);

    @ApiOperation("上传文件")
    @PostMapping("/client/file/upload")
    R<FileUploadRSP> upload(@Valid FileUploadREQ fileUploadREQ);

    @ApiOperation("获取文件信息")
    @PostMapping("/client/file/list")
    R<PageR<FileListRSP>> list(@RequestBody @Valid ClientAuthorityFileListREQ req);

    @ApiOperation("下载报告")
    @GetMapping("/client/file/download")
    R<FileDownLoadRSP> download(@Valid FileDownLoadREQ req);

    @ApiOperation("批量删除文件")
    @PostMapping("/client/file/batch/remove")
    R<Void> batchRemove(@RequestBody @Valid FileBatchRemoveREQ req);

    @ApiOperation("批量下载报告")
    @GetMapping("/client/file/batch/download")
    void batchDownload(@Valid FileBatchDownLoadREQ req);

    @ApiOperation("客户当前批次版本")
    @PostMapping("/client/batch/number")
    R<String> getBatchNumber();


    @ApiOperation("所有集团公司客户")
    @PostMapping("/client/group/list")
    R<PageR<ClientListRSP>> groupList(@RequestBody @Valid ClientListREQ req);

    @PostMapping(path = "/message/test")
    void test(String timePoint);

    @ApiOperation("APP拜访记录模糊搜索境内客户名称")
    @PostMapping("/app/visit/queryCompany")
    R<List<ClientAppQueryRSP>> queryCompany(@RequestBody @Valid ClientAppQueryREQ req);

}
