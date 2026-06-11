package cn.zswltech.mithras.application.orchestration.facade.client;
import cn.zswltech.mithras.customer.application.client.ClientCreateRecordService;
import cn.zswltech.mithras.customer.application.client.ClientTransferApplyService;
import cn.zswltech.mithras.customer.application.client.ClientUserRefService;
import cn.zswltech.mithras.customer.application.client.CorpAddressInfoService;
import cn.zswltech.mithras.customer.application.client.CorpAddressTycInfo;
import cn.zswltech.mithras.customer.application.client.CorpRelatedEnterpriseService;
import cn.zswltech.mithras.customer.application.client.CorpShareHolderInfoService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.customer.application.client.ClientApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.customer.hymx.application.ClientHymxService;
import cn.zswltech.mithras.customer.hymx.model.ClientHymx;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.customer.application.client.ClientTransferWeightService;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.file.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientModifyMainAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientRemoveMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonAddMainAuthCheckerNew;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.*;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.customer.excel.exporter.ClientTransferExcelExporter;
import cn.zswltech.mithras.customer.excel.ClientTransferExcelModel;
import cn.zswltech.mithras.customer.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientTransferMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientTransferWeightMapper;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.*;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.third.tianyancha.application.TycService;
import cn.zswltech.mithras.third.tianyancha.application.dto.*;
import cn.zswltech.mithras.third.tianyancha.client.resp.*;
import cn.zswltech.mithras.third.tianyancha.application.impl.TycExecutionService;
import cn.zswltech.mithras.application.orchestration.client.authority.ClientAuthorityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.bean.BeanUtil.copyToList;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.ID_CARD_ERROR;
import static cn.zswltech.mithras.customer.enums.client.ClientProcessStatus.UNDER_APPROVAL;
import static cn.zswltech.mithras.customer.enums.client.ClientTransferStatus.timed_approved;
import static cn.zswltech.mithras.customer.enums.client.ClientTransferStatus.to_be_approved;
import static cn.zswltech.mithras.customer.enums.client.ClientType.CORPORATION;
import static cn.zswltech.mithras.customer.enums.client.ClientType.NORMAL;
import static cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum.*;
import static cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType.GROUP_CREDIT_REVIEW;
import static cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType.PROJ_ESTABLISH;
import static cn.zswltech.mithras.foundation.util.Const.CERT_ID_CARD_CODE;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.util.Util.checkIDCard;
import static cn.zswltech.mithras.foundation.state.ProjProcessState.CHANGING_UNDER_APPROVAL;
import static cn.zswltech.mithras.foundation.state.ProjProcessState.NEW_UNDER_APPROVAL;
import org.springframework.stereotype.Service;

/**
 * @author luyi
 */
@Slf4j
@Service
public class ClientFacade implements ClientApplicationService {

    @Resource
    private ClientService clientService;
    @Resource
    private ClientHymxService clientHymxService;
    @Resource
    private CorpAddressInfoService corpAddressInfoService;
    @Resource
    private CorpRelatedEnterpriseService relatedEnterpriseService;
    @Resource
    private CorpShareHolderInfoService shareHolderInfoService;
    @Resource
    private TycService tycService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjEstablishBaseInfoService etbBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService rvBaseInfoService;
    @Resource
    private ProjPricingBaseInfoService pricingBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ClientAuthorityApplyService authorityApplyService;
    @Resource
    private ClientTransferWeightMapper transferWeightMapper;
    @Resource
    private ClientTransferWeightService transferWeightService;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private ClientTransferExcelExporter clientTransferExcelExporter;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientAuthorityUtil authorityUtil;
    @Resource
    private ClientTransferMapper transferMapper;
    @Resource
    private ProjEstablishBaseInfoMapper establishBaseInfoMapper;
    @Resource
    private TycExecutionService tycExecutionService;

    @Override
    public R<PageR<ClientListRSP>> listNoAuthrotiy(@Valid ClientListREQ req) {
        return R.ok(clientService.listNoAuthority(req));
    }

    @Override
    public R<PageR<ClientListRSP>> list(ClientListREQ req) {
        Page<Client> data = clientService.list(req);
        List<Client> records = data.getRecords();
        List<ClientListRSP> list = BeanUtil.copyToList(records, ClientListRSP.class);
        clientService.fillOtherInfo(list, req.getShowApprovalFlag());

        /*添加航运模型特有用户信息  如果用户输入名称需要去数据库中模糊查询*/
        if(req.getContainHymx() !=null && req.getContainHymx()) {
            List<ClientHymx> clientHymxes = null;
            if (req.getClientName() != null) {
                LambdaQueryChainWrapper wrapper = clientHymxService.lambdaQuery().like(ClientHymx::getClientName, req.getClientName());
                clientHymxes = wrapper.list();
            } else {
                clientHymxes = clientHymxService.list();
            }
            List<ClientListRSP> listHymx = BeanUtil.copyToList(clientHymxes, ClientListRSP.class);
            list.addAll(listHymx);
        }

        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));

    }

//    @Override
//    public R<List<ClientListRSP>> newList(ClientListREQ req) {
//        List<Client> records = clientService.newList(req);
//        List<ClientListRSP> list = BeanUtil.copyToList(records, ClientListRSP.class);
//        clientService.fillOtherInfo(list, req.getShowApprovalFlag());
//        return R.ok(list);
//    }

    @Override
    public R<PageR<ClientListRSP>> newList(ClientListREQ req) {
        Page<Client> data = clientService.newPageList(req);
        List<Client> records = data.getRecords();
        List<ClientListRSP> list = BeanUtil.copyToList(records, ClientListRSP.class);
        clientService.fillOtherInfo(list, req.getShowApprovalFlag());
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @DataAuthCheck(checkerClass = CommonAddMainAuthCheckerNew.class, paramType = DataAuthCheck.ParamType.NO, businessModule = "CLIENT")
    public R<CorpCommerceInfoAddREQ> addCorporation(ClientCorpAddREQ req) {
        R<CorpCommerceInfoAddREQ> r = null;
        DomesticOrAbroad domesticOrAbroad = DomesticOrAbroad.valueOf(req.getDomesticOrAbroad());
        switch (domesticOrAbroad) {
            case DOMESTIC:
                r = addDomesticCorporation(req);
                break;
            case ABROAD:
                r = addAbroadCorporation(req);
                break;
            default:
                err("未知的国内外标识");
        }
        if (r.isSuccess()) {
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            SpringUtil.getBean(ClientCreateRecordService.class).create(r.getData().getClientId(), currentUserId);
            SpringUtil.getBean(ClientUserRefService.class).create(r.getData().getClientId(), currentUserId);
        }
        return r;
    }

    private R<CorpCommerceInfoAddREQ> addAbroadCorporation(ClientCorpAddREQ req) {
        Client client = copyProperties(req, Client.class);
        clientService.add(client, CORPORATION.name());
        CorpCommerceInfoAddREQ addREQ = new CorpCommerceInfoAddREQ();
        addREQ.setClientId(client.getId());
        return R.ok(addREQ);
    }

    private R<CorpCommerceInfoAddREQ> addDomesticCorporation(ClientCorpAddREQ req) {
        String uscCode = req.getUscCode();
        if (uscCode == null || uscCode.length() != 18) {
            throw new MithrasException("统一社会信用代码长度不合法，请重新输入！");
        }
        Client condition = new Client();
        condition.setUscCode(uscCode);
        List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getUscCode, uscCode));
        if (isNotEmpty(clientList)) {
            Client client = clientList.get(0);
            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                    .eq(ClientAuthority::getClientId, client.getId())
                    .eq(ClientAuthority::getDeleted, 0));
            if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())) {
                if (clientAuthorityList != null && !clientAuthorityList.isEmpty()) {
                    for (ClientAuthority clientAuthority : clientAuthorityList) {
                        if (ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
                            String sponsorUserName = id2NameService.sysUserId2NameSingle(clientAuthority.getUserId());
                            throw new MithrasException(String.format("项目经理%s已经拥有该客户管护权", sponsorUserName));
                        }
                    }
                } else {
                    throw new MithrasException("该客户已生效");
                }
            } else if (authorityUtil.isNewClient(client)
                    || authorityUtil.isReleasedClient(client)) {
                CorpCommerceInfoAddREQ rsp = new CorpCommerceInfoAddREQ();
                rsp.setClientId(client.getId());
                return R.ok(rsp);
            }
//            throw new MithrasException("该客户已存在");
        }
        MithrasBaseInfo baseInfo = Optional.ofNullable(tycService.baseInfo(uscCode)).orElse(new MithrasBaseInfo());
        if (isNull(baseInfo) && notEqual(req.getMuteTycError(), true)) {
            return R.ok(-511, "获取天眼查信息失败");
        }
        if (isNotNull(baseInfo) && isNotNull(baseInfo.getClientType()) && notEqual(baseInfo.getClientType(), CORPORATION.name())) {
            return R.ok(-512, "选择的客户分类与天眼查获取的分类不一致");
        }
        Client client;
        if (CollectionUtil.isEmpty(clientList)) {
            client = copyProperties(req, Client.class);
            client.setTycName(baseInfo.getTycName());
            clientService.add(client, CORPORATION.name());
        } else {
            client = clientList.get(0);
        }
        // 先尝试清空一下当前用户数据副本
        authorityUtil.clearNewData(client.getId(), AccountUtil.getLoginInfo().getId());
        CorpAddressTycInfo addressTycInfo = copyProperties(baseInfo, CorpAddressTycInfo.class);
        corpAddressInfoService.addByCommerceInfo(client.getId(), addressTycInfo);
        List<MithrasShareholderInfo> mithrasShareholderInfos = tycService.shareholderInfo(uscCode);
        shareHolderInfoService.batchAdd(client.getId(), copyToList(mithrasShareholderInfos, NewCorpShareholderInfo.class));
        List<MithrasRelatedEnterpriseInfo> enterpriseInfos = tycService.relatedEnterpriseInfo(uscCode);
        relatedEnterpriseService.batchAdd(client.getId(), copyToList(enterpriseInfos, NewCorpRelatedEnterprise.class));
        CorpCommerceInfoAddREQ rsp = copyProperties(baseInfo, CorpCommerceInfoAddREQ.class);
        if (isNotBlank(rsp.getIndustryType())) {
            IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, rsp.getIndustryType()));
            if (isNotNull(industryType)) {
                rsp.setIndustryTypeName(industryType.getDisplay());
            }
        }
        rsp.setClientId(client.getId());
        return R.ok(rsp);
    }

    @Override
    @DataAuthCheck(checkerClass = CommonAddMainAuthCheckerNew.class, paramType = DataAuthCheck.ParamType.NO, businessModule = "CLIENT")
    public R<Long> addNormal(ClientNormalAddREQ req) {
        String certType = req.getCertType();
        //身份证
        if (certType.equals(CERT_ID_CARD_CODE)) {
            if (!checkIDCard(req.getCertNumber())) {
                throw new MithrasException(ID_CARD_ERROR);
            }
        }
        Client condition = new Client();
        condition.setCertNumber(req.getCertNumber());
        if (isNotEmpty(clientService.get(condition))) {
            throw new MithrasException("该客户已存在");
        }
        Long id = clientService.add(copyProperties(req, Client.class), NORMAL.name());
        return R.ok(id);
    }

    @Override
    public R<Boolean> userNameExist(ClientNameExistREQ req) {
        return R.ok(clientService.getByUserName(req.getClientName()).size() > 0);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Boolean> removeClient(ClientRemoveREQ req) {
        clientService.remove(req.getId());
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientModifyMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<ClientSyncRSP> sync(ClientSyncREQ req) {
        ClientSyncRSP rsp = clientService.sync(req.getClientId());
        if (isNotBlank(rsp.getChangedCommerceInfo().getIndustryType())) {
            IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, rsp.getChangedCommerceInfo().getIndustryType()));
            if (isNotNull(industryType)) {
                rsp.getChangedCommerceInfo().setIndustryTypeName(industryType.getDisplay());
            }
        }
        tycExecutionService.syncExternal(req.getClientId());
        return R.ok(rsp);
    }

    @Override
    public R<CorpCommerceInfoDetailRSP> tycCommerceInfo(ClientSyncREQ req) {
        Client client = clientService.getById(req.getClientId());
        if (ObjectUtil.isEmpty(client.getUscCode())) {
            return R.ok();
        }
        MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo(client.getUscCode());
        CorpCommerceInfoDetailRSP rsp = copyProperties(mithrasBaseInfo, CorpCommerceInfoDetailRSP.class);
        copyProperties(client, rsp, "industryType");
        if (isNotBlank(rsp.getIndustryType())) {

            IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, rsp.getIndustryType()));
            if (isNotNull(industryType)) {
                rsp.setIndustryTypeName(industryType.getDisplay());
            }
        }
        return R.ok(rsp);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifyMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> effect(ClientEffectREQ req) {
        clientService.effect(req.getId());
        return R.ok();
    }

    @Override
    public R<ClientButtonStatusRSP> buttonStatus(ClientButtonStatusREQ req) {
        ClientButtonStatusRSP rsp = clientService.queryClientButtonStatus(req.getId());
        return R.ok(rsp);
    }

    @Override
    public R<ClientTransferApplyRSP> createTransferApply() {
        ClientTransferApply clientTransferApply = SpringUtil.getBean(ClientTransferApplyService.class).create();
        ClientTransferApplyRSP rsp = BeanUtil.copyProperties(clientTransferApply, ClientTransferApplyRSP.class);
        return R.ok(rsp);
    }

    @Override
    public R<ClientTransferApplyRSP> findTransferApplyByBatchNo(@Valid ClientTransferApplyQueryREQ req) {
        ClientTransferApply clientTransferApply = SpringUtil.getBean(ClientTransferApplyService.class).findByBatchNo(req.getBatchNo());
        ClientTransferApplyRSP rsp = BeanUtil.copyProperties(clientTransferApply, ClientTransferApplyRSP.class);
        return R.ok(rsp);
    }

    @Override
    public R<UserButtonStatusRSP> buttonStatus() {
        UserButtonStatusRSP rsp = clientService.queryUserButtonStatus(AccountUtil.getLoginInfo().getId());
        return R.ok(rsp);
    }

    @Override
    public R<Void> authorityEffect(ClientAuthorityEffectREQ req) {
        clientAuthorityService.effect(req.getId(), req.getOpinion());
        return R.ok();
    }

    @Override
    public R<Void> applyEffect(ClientApplyEffectREQ req) {
        authorityApplyService.applyEffect(req);
        return R.ok();
    }

    @Override
    public R<Void> applyModify(ClientApplyModifyREQ req) {
        authorityApplyService.applyModify(req);
        return R.ok();
    }

    @Override
    public R<ClientApplyDetailRSP> applyDetail(ClientApplyDetailREQ req) {
        ClientApplyDetailRSP rsp = authorityApplyService.applyDetail(req);
        return R.ok(rsp);
    }

    @Override
    public R<Void> applyValidate(ClientApplyDetailREQ req) {
        authorityApplyService.applyValidate(req);
        return R.ok();
    }

    @Override
    public R<Void> exportClientTransfer(ClientTransferExportREQ clientTransferExportREQ) {
        exportExcel(clientTransferExportREQ);
        return R.ok();
    }


    @Override
    public R<ClientOwnApplyDetailRSP> applyOwn(ClientOwnApplyDetailREQ req) {
        // 接口层面为了安全且保证逻辑统一，此处调用判断权限接口，这样的方式会存在一定的代码重复
        ClientApplyOccupyREQ occupyReq = new ClientApplyOccupyREQ();
        occupyReq.setClientId(req.getClientId());
        occupyReq.setProcessInstanceId(req.getProcessInstanceId());
        ClientApplyOccupyRSP occupyRsp = clientAuthorityService.checkOccupy(occupyReq);
        if (StrUtil.isNotBlank(occupyRsp.getMessage())) {
            throw new MithrasException(occupyRsp.getMessage());
        }
        // 执行展示逻辑
        ClientOwnApplyDetailRSP rsp = authorityApplyService.applyOwn(req);
        return R.ok(rsp);
    }

    @Override
    public R<ClientApplyStatusRSP> status(ClientApplyStatusREQ req) {
        ClientApplyStatusRSP rsp = clientAuthorityService.status(req);
        return R.ok(rsp);
    }

    @Override
    public R<ClientApplyOccupyRSP> checkOccupy(ClientApplyOccupyREQ req) {
        ClientApplyOccupyRSP rsp = clientAuthorityService.checkOccupy(req);
        return R.ok(rsp);
    }

    @Override
    public R<Void> release(ClientReleaseREQ req) {
        clientService.release(req);
        return R.ok();
    }

    @Override
    public R<FileUploadRSP> upload(FileUploadREQ fileUploadREQ) {
        return R.ok(clientAuthorityService.upload(fileUploadREQ));
    }

    @Override
    public R<PageR<FileListRSP>> list(ClientAuthorityFileListREQ req) {
        return R.ok(clientAuthorityService.list(req));
    }

    @Override
    public R<FileDownLoadRSP> download(FileDownLoadREQ req) {
        return R.ok(clientAuthorityService.download(req));
    }

    @Override
    public R<Void> batchRemove(FileBatchRemoveREQ req) {
        clientAuthorityService.batchRemove(req);
        return R.ok();
    }

    @Override
    public void batchDownload(FileBatchDownLoadREQ req) {
        clientAuthorityService.batchDownload(req);
    }

    @Override
    public R<String> getBatchNumber() {
        String batchNumber = clientService.getBatchNumber();
        return R.ok(batchNumber);
    }

    @Override
    public R<PageR<ClientListRSP>> groupList(ClientListREQ req) {
        Page<Client> data = clientService.groupList(req);
        List<Client> records = data.getRecords();
        List<ClientListRSP> list = BeanUtil.copyToList(records, ClientListRSP.class);
        clientService.fillOtherInfo(list, req.getShowApprovalFlag());
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public void test(String timePoint) {
        List<Long> targetClientIds = this.listHasManagerClientIds();
        if (CollectionUtil.isEmpty(targetClientIds)) {
            return;
        }
        // 遍历判断是否可以释放
        for (Long clientId : targetClientIds) {
            String s = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
            try {
                Client client = clientService.getById(clientId);
                if (Objects.isNull(client)) {
                    log.error("没有找到{}的客户信息", clientId);
                    continue;
                }
                MDC.put(GlobalConstants.LOG_TRACE_ID, String.format("%s-%s", clientId, s));
                clientService.trySendClientNotice(client);
            } catch (Exception e) {
                log.error("释放客户发生异常[clientId:{}]", clientId, e);
            } finally {
                MDC.remove(GlobalConstants.LOG_TRACE_ID);
            }
        }
    }

    private List<Long> listHasManagerClientIds() {
        LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel());
        List<ClientAuthority> clientAuthorityList = clientAuthorityService.list(query);
        if (CollectionUtil.isEmpty(clientAuthorityList)) {
            return Collections.emptyList();
        }
        return clientAuthorityList.stream().map(ClientAuthority::getClientId).collect(Collectors.toList());
    }

    @Override
    public R<List<ClientAppQueryRSP>> queryCompany(ClientAppQueryREQ req) {
        List<ClientAppQueryRSP> clientAppQueryRSPList = queryClients(req);
        return R.ok(clientAppQueryRSPList);
    }

    public List<ClientAppQueryRSP> queryClients(ClientAppQueryREQ req) {
        if (StringUtils.isBlank(req.getCompanyName())) {
            return new ArrayList<>();
        }
        TycQueryCompanyReq tycReq = new TycQueryCompanyReq();
        tycReq.setCompanyName(req.getCompanyName());
        tycReq.setPage(req.getPage());
        tycReq.setPageSize(req.getPageSize());
        List<MithrasCompanyInfo> baseInfoList = tycService.queryByCompanyName(tycReq);
        List<ClientAppQueryRSP> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(baseInfoList)) {
            Set<String> createCodes = baseInfoList.stream().map(MithrasCompanyInfo::getCreditCode).collect(Collectors.toSet());
            List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getUscCode, createCodes));
            Map<String, Long> clientIdMap;
            if (CollectionUtil.isEmpty(clients)) {
                clientIdMap = Collections.emptyMap();
            } else {
                clientIdMap = clients.stream().collect(Collectors.toMap(Client::getUscCode, Client::getId));
            }
            for (MithrasCompanyInfo companyInfo : baseInfoList) {
                if (StringUtils.isEmpty(companyInfo.getCreditCode()) || companyInfo.getCreditCode().length() != 18) {
                    continue;
                }
//                boolean find = false;
                ClientAppQueryRSP clientAppQueryRSP = BeanUtil.copyProperties(companyInfo, ClientAppQueryRSP.class);
                clientAppQueryRSP.setClientId(clientIdMap.get(companyInfo.getCreditCode()));
                res.add(clientAppQueryRSP);
//                for (Client client : clients) {
//                    if (companyInfo.getCreditCode().equalsIgnoreCase(client.getUscCode())) {
//                        find = true;
//                        clientAppQueryRSP.setClientId(client.getId());
//                        res.add(clientAppQueryRSP);
//                        break;
//                    }
//                }
//                if (!find) {
//                    ClientCorpAddREQ addREQ = new ClientCorpAddREQ();
//                    addREQ.setClientName(companyInfo.getCompanyName());
//                    addREQ.setUscCode(companyInfo.getCreditCode());
//                    addREQ.setDomesticOrAbroad(DomesticOrAbroad.DOMESTIC.name());
//                    R<CorpCommerceInfoAddREQ> r = addCorporation(addREQ);
//                    clientAppQueryRSP.setClientId(r.getData().getClientId());
//                    res.add(clientAppQueryRSP);
//                }
            }
        }
        return res;
    }


    private List<SponsorClientListRSP> getSponsorClients(List<Client> clientList) {
        List<Long> clientIdList = clientList.stream().map(Client::getId).collect(Collectors.toList());
        //已经在移交中的d
        Set<Long> inTransferSet = clientTransferService.getBaseMapper().selectList(Wrappers.<ClientTransfer>lambdaQuery().in(ClientTransfer::getTransferStatus, to_be_approved.name(), timed_approved.name()))
                .stream().map(ClientTransfer::getClientId).collect(Collectors.toSet());
        //客户的立项数据
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = etbBaseInfoService.listByClients(clientIdList);
        Map<String, ProjEstablishBaseInfo> etbCodeMap = projEstablishBaseInfos.stream().collect(Collectors.toMap(ProjEstablishBaseInfo::getProjCode, e -> e));
        Map<Long, List<ProjEstablishBaseInfo>> clientEtbMap = projEstablishBaseInfos.stream()
                .collect(Collectors.groupingBy(ProjEstablishBaseInfo::getClientId));

        //客户的评审数据（可能是一般评审和集团授信评审）
        List<ProjReviewBaseInfo> projReviewBaseInfos = rvBaseInfoService.listByClients(clientIdList);
        Map<String, ProjReviewBaseInfo> rvCodeMap = projReviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getProjCode, e -> e));
        Map<Long/*clientId*/, List<ProjReviewBaseInfo>/**/> etbRvMap = projReviewBaseInfos.stream().filter(e -> PROJ_ESTABLISH.name().equals(e.getRelationDataType()))
                .collect(Collectors.groupingBy(ProjReviewBaseInfo::getClientId));
        Map<Long/*clientId*/, List<ProjReviewBaseInfo>/**/> groupCreditRvMap = projReviewBaseInfos.stream().filter(e -> GROUP_CREDIT_REVIEW.name().equals(e.getRelationDataType()))
                .collect(Collectors.groupingBy(ProjReviewBaseInfo::getClientId));

        //评审关联的合同
        Map<Long/*projReviewId*/, List<ContractBaseInfo>/**/> rvContractMap = contractBaseInfoService.listByClients(clientIdList).stream()
                .collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        //拼装结果
        List<SponsorClientListRSP> result = copyToList(clientList, SponsorClientListRSP.class);
        Map<Long, String> nameMap = id2NameService.sysUserId2Name(clientList.stream().map(Client::getBelongSponsorId).collect(Collectors.toList()));
        Map<Long, String> orgNameMap = id2NameService.deptId2Name(clientList.stream().map(Client::getBelongDeptId).collect(Collectors.toList()));
        result.forEach(e -> {
            e.setBelongSponsorName(nameMap.get(e.getBelongSponsorId()));
            e.setBelongDeptName(orgNameMap.get(e.getBelongDeptId()));
            //找出客户下的一般立项
            List<SponsorClientListRSP.ClientProjRSP> clientProjRSPList = new ArrayList<>();
            Set<String> projCodeSet = clientEtbMap.getOrDefault(e.getId(), new ArrayList<>()).stream().map(ProjEstablishBaseInfo::getProjCode).collect(Collectors.toSet());
            projCodeSet.addAll(etbRvMap.getOrDefault(e.getId(), new ArrayList<>()).stream().map(ProjReviewBaseInfo::getProjCode).collect(Collectors.toSet()));
            projCodeSet.addAll(groupCreditRvMap.getOrDefault(e.getId(), new ArrayList<>()).stream().map(ProjReviewBaseInfo::getProjCode).collect(Collectors.toSet()));

            for (String projCode : projCodeSet) {
                SponsorClientListRSP.ClientProjRSP projRsp = new SponsorClientListRSP.ClientProjRSP();
                try {
                    ProjEstablishBaseInfo etbBaseInfo = etbCodeMap.get(projCode);
                    ProjReviewBaseInfo rvBaseInfo = rvCodeMap.get(projCode);
                    if (isNotNull(etbBaseInfo)) {
                        projRsp.setProjectName(etbBaseInfo.getProjName());
                        projRsp.setProjCode(etbBaseInfo.getProjCode());
                        projRsp.setBizType(etbBaseInfo.getBizType());
                    } else if (isNotNull(rvBaseInfo)) {
                        projRsp.setProjectName(rvBaseInfo.getProjName());
                        projRsp.setProjCode(rvBaseInfo.getProjCode());
                        projRsp.setBizType(rvBaseInfo.getBizType());
                    } else {
                        log.error("未知的项目code:{}", projCode);
                    }

                    //本身立项是否处在审批中
                    if (isNotNull(etbBaseInfo)) {
                        if (NEW_UNDER_APPROVAL == etbBaseInfo.getProcessStatus() || CHANGING_UNDER_APPROVAL == etbBaseInfo.getProcessStatus()) {
                            projRsp.setInProcess(true);
                            continue;
                        }
                    }
                    //判断关联的评审是否处在审批中
                    if (isNotNull(rvBaseInfo)) {
                        if (NEW_UNDER_APPROVAL == rvBaseInfo.getProcessStatus() || CHANGING_UNDER_APPROVAL == rvBaseInfo.getProcessStatus()) {
                            projRsp.setInProcess(true);
                            continue;
                        }
                        //判断评审合同是否处在审批中
                        List<ContractBaseInfo> contractList = rvContractMap.getOrDefault(rvBaseInfo.getId(), new ArrayList<>());
                        if (contractList.stream().anyMatch(
                                item -> CHANGE_COMMIT.name().equals(item.getContractProcessStatus())
                                        || NEW_COMMIT.name().equals(item.getContractProcessStatus())
                                        || START_RENT_COMMIT.name().equals(item.getContractProcessStatus())
                                        || NEW_RECEIPT_COMMIT.name().equals(item.getContractProcessStatus())
                                        || SETTLE_COMMIT.name().equals(item.getContractProcessStatus())
                        )) {
                            projRsp.setInProcess(true);
                            continue;
                        }
                    }

                } finally {
                    clientProjRSPList.add(projRsp);
                }
            }
            e.setClientProjRSPList(clientProjRSPList);
            //设置客户状态，客户本身在审批中或者其他关联的模块在审批中，则为审批中
            e.setInProcess(
                    UNDER_APPROVAL.name().equals(e.getProcessStatus())
                            || inTransferSet.contains(e.getId())
                            || e.getClientProjRSPList().stream().anyMatch(p -> Boolean.TRUE.equals(p.getInProcess()))
            );

        });
        return result;
    }


    private List<SponsorClientListNewRSP> getNewSponsorClients(List<Client> clientList, List<Client> groupClients, String batchNo, Long transferBelongSponsorId) {
        List<Long> clientIdList = clientList.stream().map(Client::getId).collect(Collectors.toList());
        List<Long> groupClientIdList = null;
        if (!groupClients.isEmpty()) {
            groupClientIdList = groupClients.stream().map(Client::getId).collect(Collectors.toList());
        }
        //已经在移交中的d
        List<ClientTransfer> clientTransferList = clientTransferService.getBaseMapper().selectList(Wrappers.<ClientTransfer>lambdaQuery().in(ClientTransfer::getTransferStatus, to_be_approved.name(), timed_approved.name()));
        Set<Long> inTransferSet = clientTransferList.stream().map(ClientTransfer::getClientId).collect(Collectors.toSet());
        //公海客户移交的立项
        Map<Long, List<Long>> projEstablishIdsMap = new HashMap<>();
        for (ClientTransfer clientTransfer : clientTransferList) {
            projEstablishIdsMap.putIfAbsent(clientTransfer.getClientId(), new ArrayList<>());
            List<Long> projEstablishIds = clientTransfer.getProjEstablishIds();
            if (projEstablishIds != null && !projEstablishIds.isEmpty()) {
                projEstablishIdsMap.get(clientTransfer.getClientId()).addAll(projEstablishIds);
            }
        }

        //客户的立项数据,公海客户项目主办过滤，非公海全选
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = new ArrayList<>();
        if (!clientIdList.isEmpty()) {
            projEstablishBaseInfos = etbBaseInfoService.listByClients(clientIdList);
        }
        List<ProjEstablishBaseInfo> tempEstablishBaseInfos = new ArrayList<>();
        if (groupClientIdList != null && !groupClientIdList.isEmpty()) {
            List<ProjEstablishBaseInfo> groupEstablishBaseInfos = etbBaseInfoService.listByClients(groupClientIdList);
            for (ProjEstablishBaseInfo establishBaseInfo : groupEstablishBaseInfos) {
                if (transferBelongSponsorId != null && transferBelongSponsorId.equals(establishBaseInfo.getProjSponsorUserId())) {
                    tempEstablishBaseInfos.add(establishBaseInfo);
                }
            }
        }

        Map<String, ProjEstablishBaseInfo> etbCodeMap = projEstablishBaseInfos.stream().collect(Collectors.toMap(ProjEstablishBaseInfo::getProjCode, e -> e));
        Map<Long, List<ProjEstablishBaseInfo>> clientEtbMap = projEstablishBaseInfos.stream()
                .collect(Collectors.groupingBy(ProjEstablishBaseInfo::getClientId));
        for (ProjEstablishBaseInfo establishBaseInfo : tempEstablishBaseInfos) {
            etbCodeMap.putIfAbsent(establishBaseInfo.getProjCode(), establishBaseInfo);
            clientEtbMap.putIfAbsent(establishBaseInfo.getClientId(), new ArrayList<>());
            clientEtbMap.get(establishBaseInfo.getClientId()).add(establishBaseInfo);
        }
        //可能保存并没有提交申请
        List<ClientTransfer> transferList = null;
        if (StringUtils.isNotBlank(batchNo)) {
            transferList = transferMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery()
                    .eq(ClientTransfer::getBatchNo, batchNo)
                    .isNotNull(ClientTransfer::getClientId));
        }

        //客户的评审数据（可能是一般评审和集团授信评审）,公海客户评审过滤，非公海全选
        List<ProjReviewBaseInfo> projReviewBaseInfos = new ArrayList<>();
        if (!clientIdList.isEmpty()) {
            projReviewBaseInfos = rvBaseInfoService.listByClients(clientIdList);
        }
        List<ProjReviewBaseInfo> tempReviewBaseInfos = new ArrayList<>();
        if (groupClientIdList != null && !groupClientIdList.isEmpty()) {
            List<ProjReviewBaseInfo> reviewBaseInfos = rvBaseInfoService.listByClients(groupClientIdList);
            for (ProjReviewBaseInfo projReviewBaseInfo : reviewBaseInfos) {
                if (transferBelongSponsorId != null && transferBelongSponsorId.equals(projReviewBaseInfo.getProjSponsorUserId())) {
                    tempReviewBaseInfos.add(projReviewBaseInfo);
                }
            }
        }
        Map<String, ProjReviewBaseInfo> rvCodeMap = projReviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getProjCode, e -> e, (a, b) -> a));
        Map<Long/*clientId*/, List<ProjReviewBaseInfo>/**/> etbRvMap = projReviewBaseInfos.stream().filter(e -> PROJ_ESTABLISH.name().equals(e.getRelationDataType()))
                .collect(Collectors.groupingBy(ProjReviewBaseInfo::getClientId));
        Map<Long/*clientId*/, List<ProjReviewBaseInfo>/**/> groupCreditRvMap = projReviewBaseInfos.stream().filter(e -> GROUP_CREDIT_REVIEW.name().equals(e.getRelationDataType()))
                .collect(Collectors.groupingBy(ProjReviewBaseInfo::getClientId));
        if (tempReviewBaseInfos != null && !tempReviewBaseInfos.isEmpty()) {
            List<ProjReviewBaseInfo> tbRvList = tempReviewBaseInfos.stream().filter(e -> PROJ_ESTABLISH.name().equals(e.getRelationDataType()))
                    .collect(Collectors.toList());
            List<ProjReviewBaseInfo> creditRvList = tempReviewBaseInfos.stream().filter(e -> GROUP_CREDIT_REVIEW.name().equals(e.getRelationDataType()))
                    .collect(Collectors.toList());
            for (ProjReviewBaseInfo projReviewBaseInfo : tempReviewBaseInfos) {
                rvCodeMap.putIfAbsent(projReviewBaseInfo.getProjCode(), projReviewBaseInfo);
            }
            for (ProjReviewBaseInfo projReviewBaseInfo : tbRvList) {
                etbRvMap.putIfAbsent(projReviewBaseInfo.getClientId(), new ArrayList<>());
                etbRvMap.get(projReviewBaseInfo.getClientId()).add(projReviewBaseInfo);
            }
            for (ProjReviewBaseInfo projReviewBaseInfo : creditRvList) {
                groupCreditRvMap.putIfAbsent(projReviewBaseInfo.getClientId(), new ArrayList<>());
                groupCreditRvMap.get(projReviewBaseInfo.getClientId()).add(projReviewBaseInfo);
            }
        }

        //客户项目定价,公海客户项目定价过滤，非公海全选
        List<ProjPricingBaseInfo> projPricingBaseInfos = new ArrayList<>();
        if (!clientIdList.isEmpty()) {
            projPricingBaseInfos = pricingBaseInfoService.listByClients(clientIdList);
        }
        Map<String, ProjPricingBaseInfo> pricingCodeMap = projPricingBaseInfos.stream().collect(Collectors.toMap(ProjPricingBaseInfo::getProjCode, e -> e));
        List<ProjPricingBaseInfo> tempProjPricingBaseInfos = null;
        if (groupClientIdList != null && !groupClientIdList.isEmpty()) {
            tempProjPricingBaseInfos = pricingBaseInfoService.listByClients(groupClientIdList);
            for (ProjPricingBaseInfo projPricingBaseInfo : tempProjPricingBaseInfos) {
                if (transferBelongSponsorId != null && transferBelongSponsorId.equals(projPricingBaseInfo.getProjSponsorUserId())) {
                    pricingCodeMap.putIfAbsent(projPricingBaseInfo.getProjCode(), projPricingBaseInfo);
                }
            }
        }

        //评审关联的合同,公海客户合同过滤，非公海全选
        Map<Long/*projReviewId*/, List<ContractBaseInfo>/**/> rvContractMap;
        if (CollectionUtil.isNotEmpty(projReviewBaseInfos)) {
            List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByProjReviewIds(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
                if (groupClientIdList != null && CollectionUtil.isNotEmpty(groupClientIdList)) {
                    // 过滤掉公海客户合同
                    Iterator<ContractBaseInfo> iterator = contractBaseInfoList.iterator();
                    while (iterator.hasNext()) {
                        ContractBaseInfo cbi = iterator.next();
                        if (groupClientIdList.contains(cbi.getClientId()) && transferBelongSponsorId != null && !transferBelongSponsorId.equals(cbi.getProjSponsorUserId())) {
                            iterator.remove();
                        }
                    }
                }
                rvContractMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
            } else {
                rvContractMap = Collections.emptyMap();
            }
        } else {
            rvContractMap = Collections.emptyMap();
        }
//        if (!clientIdList.isEmpty()) {
//            rvContractMap = contractBaseInfoService.listByClients(clientIdList).stream()
//                    .collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
//        } else {
//            rvContractMap = new HashMap<Long/*projReviewId*/, List<ContractBaseInfo>>();
//        }
//        if (groupClientIdList != null && !groupClientIdList.isEmpty()) {
//            List<ContractBaseInfo> tempContractList = new ArrayList<>();
//            if (!clientIdList.isEmpty()) {
//                tempContractList = contractBaseInfoService.listByClients(groupClientIdList);
//            }
//            for (ContractBaseInfo contractBaseInfo : tempContractList) {
//                if (transferBelongSponsorId.equals(contractBaseInfo.getProjSponsorUserId())) {
//                    rvContractMap.putIfAbsent(contractBaseInfo.getProjReviewId(), new ArrayList<>());
//                    rvContractMap.get(contractBaseInfo.getProjReviewId()).add(contractBaseInfo);
//                }
//            }
//        }

        //拼装结果
        clientList.addAll(groupClients);
        List<SponsorClientListNewRSP> result = copyToList(clientList, SponsorClientListNewRSP.class);
        Map<Long, String> nameMap;
        Map<Long, String> orgNameMap;
        Long belongSponsorId;
        Long belongDeptId;
        if (transferList == null || transferList.isEmpty()) {
            belongDeptId = null;
            belongSponsorId = null;
            nameMap = id2NameService.sysUserId2Name(clientList.stream().map(Client::getBelongSponsorId).collect(Collectors.toList()));
            orgNameMap = id2NameService.deptId2Name(clientList.stream().map(Client::getBelongDeptId).collect(Collectors.toList()));
        } else {
            belongSponsorId = transferList.get(0).getBelongSponsorId();
            belongDeptId = transferList.get(0).getBelongDeptId();
            nameMap = id2NameService.sysUserId2Name(transferList.stream().map(ClientTransfer::getBelongSponsorId).collect(Collectors.toList()));
            orgNameMap = id2NameService.deptId2Name(transferList.stream().map(ClientTransfer::getBelongDeptId).collect(Collectors.toList()));
        }
        //资产五级分类
        Map<Long, String> assertClassifyResult =
                assetClassifyClientAuxiliaryLibMapper.listNewestClassifyLibByClientId(new HashSet<>(clientIdList)).stream()
                        .collect(Collectors.toMap(AssetClassifyClient::getClientId,
                                AssetClassifyClient::getClassifyResult, (k1, k2) -> k2));
        result.forEach(e -> {
            if (authorityUtil.isIntraGroupCollaboration(e.getId())) {
                e.setBelongSponsorName(null);
                e.setBelongDeptName(null);
            } else {
                if (belongSponsorId != null) {
                    e.setBelongSponsorName(nameMap.get(belongSponsorId));
                } else {
                    e.setBelongSponsorName(nameMap.get(e.getBelongSponsorId()));
                }
                if (belongDeptId != null) {
                    e.setBelongDeptName(orgNameMap.get(belongDeptId));
                } else {
                    e.setBelongDeptName(orgNameMap.get(e.getBelongDeptId()));
                }
            }
            e.setAssertClassifyResult(assertClassifyResult.get(e.getId()));
            //找出客户下的一般立项
            List<SponsorClientListNewRSP.ClientProjRSP> clientProjRSPList = new ArrayList<>();
            Set<String> projCodeSet = clientEtbMap.getOrDefault(e.getId(), new ArrayList<>()).stream().map(ProjEstablishBaseInfo::getProjCode).collect(Collectors.toSet());
            projCodeSet.addAll(etbRvMap.getOrDefault(e.getId(), new ArrayList<>()).stream().map(ProjReviewBaseInfo::getProjCode).collect(Collectors.toSet()));
            projCodeSet.addAll(groupCreditRvMap.getOrDefault(e.getId(), new ArrayList<>()).stream().map(ProjReviewBaseInfo::getProjCode).collect(Collectors.toSet()));
            List<ClientTransferWeight> clientTransferWeightList = null;
            if (StringUtils.isNotBlank(batchNo)) {
                clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getClientId, e.getId())
                        .eq(ClientTransferWeight::getBatchNo, batchNo)
                        .in(isNotEmpty(projCodeSet), ClientTransferWeight::getProjCode, projCodeSet));
            } else {
                clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getClientId, e.getId())
                        .isNull(ClientTransferWeight::getBatchNo)
                        //.eq(isNotEmpty(e.getBelongSponsorId()), ClientTransferWeight::getCreateBy, e.getBelongSponsorId())
                        .in(isNotEmpty(projCodeSet), ClientTransferWeight::getProjCode, projCodeSet));
                List<ClientTransferWeight> unused = clientTransferService.removeUnusedClientTransferWeight(clientTransferWeightList);
                if (!unused.isEmpty()) {
                    for (ClientTransferWeight unusedWeight : unused) {
                        LambdaUpdateWrapper<ClientTransferWeight> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.set(ClientTransferWeight::getDeleted, YesOrNoNumberEnum.YES.getCode());
                        updateWrapper.eq(ClientTransferWeight::getId, unusedWeight.getId());
                        transferWeightMapper.update(null, updateWrapper);
                    }
                }
                clientTransferWeightList = clientTransferService.getLatestTransferWeight(clientTransferWeightList, unused);
            }
            for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                SponsorClientListNewRSP.ClientProjRSP tempProjRsp = new SponsorClientListNewRSP.ClientProjRSP();
                tempProjRsp.setProjCode(clientTransferWeight.getProjCode());
                List<Long> toDeptIds = new ArrayList<>();
                toDeptIds.add(clientTransferWeight.getToDeptId());
                Map<Long, String> tempOrgNameMap = id2NameService.deptId2Name(toDeptIds);
                Map<Long, String> id2NameMap = id2NameService.sysUserId2Name(ListUtil.toList(clientTransferWeight.getToSponsorId()));
                tempProjRsp.setToSponsorId(clientTransferWeight.getToSponsorId());
                tempProjRsp.setToSponsorName(id2NameMap.get(clientTransferWeight.getToSponsorId()));
                tempProjRsp.setToCosponsorIds(clientTransferWeight.getToCosponsorIds());
                if (clientTransferWeight.getToCosponsorIds() != null && !clientTransferWeight.getToCosponsorIds().isEmpty()) {
                    Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(clientTransferWeight.getToCosponsorIds());
                    tempProjRsp.setToCosponsorNames(clientTransferWeight.getToCosponsorIds().stream().map(sysUserMap::get).collect(Collectors.joining(",")));
                }
                tempProjRsp.setToBelongDeptId(clientTransferWeight.getToDeptId());
                tempProjRsp.setToBelongDeptName(tempOrgNameMap.get(clientTransferWeight.getToDeptId()));
                tempProjRsp.setIncomeTransferValue(clientTransferWeight.getIncomeTransferValue());
                tempProjRsp.setRiskTransferValue(clientTransferWeight.getRiskTransferValue());
                tempProjRsp.setContractCode(clientTransferWeight.getContractCode());
                tempProjRsp.setProjArchiveStatus(clientTransferWeight.getProjArchiveStatus());
                tempProjRsp.setTransferWeightId(clientTransferWeight.getId());
                clientProjRSPList.add(tempProjRsp);
            }
            //判断项目新增的合同
            if (!projCodeSet.isEmpty()) {
                for (String projCode : projCodeSet) {
                    ProjReviewBaseInfo rvBaseInfo = rvCodeMap.get(projCode);
                    if (isNotNull(rvBaseInfo)) {
                        List<ContractBaseInfo> contractList = rvContractMap.getOrDefault(rvBaseInfo.getId(), new ArrayList<>());
                        if (!clientTransferWeightList.isEmpty()) {
                            List<ClientTransferWeight> tempList = new ArrayList<>();
                            for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                                if (projCode.equalsIgnoreCase(clientTransferWeight.getProjCode())) {
                                    tempList.add(clientTransferWeight);
                                }
                            }
                            if (!contractList.isEmpty()
                                    && !tempList.isEmpty() && contractList.size() > tempList.size()) {
                                List<ContractBaseInfo> res = new ArrayList<>();
                                for (ContractBaseInfo contractBaseInfo : contractList) {
                                    boolean found = false;
                                    for (ClientTransferWeight clientTransferWeight : tempList) {
                                        if (contractBaseInfo.getContractCode().equalsIgnoreCase(clientTransferWeight.getContractCode())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        res.add(contractBaseInfo);
                                    }
                                }
                                if (!res.isEmpty()) {
                                    for (ContractBaseInfo contractBaseInfo : res) {
                                        SponsorClientListNewRSP.ClientProjRSP tempProjRsp = new SponsorClientListNewRSP.ClientProjRSP();
                                        tempProjRsp.setContractCode(contractBaseInfo.getContractCode());
                                        tempProjRsp.setProjCode(projCode);
                                        clientProjRSPList.add(tempProjRsp);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Map<String, List<SponsorClientListNewRSP.ClientProjRSP>> map = new HashMap<>();
            for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : clientProjRSPList) {
                map.putIfAbsent(clientProjRSP.getProjCode(), new ArrayList<>());
                map.get(clientProjRSP.getProjCode()).add(clientProjRSP);
            }
            List<SponsorClientListNewRSP.ClientProjRSP> res = new ArrayList<>();
            if (!projCodeSet.isEmpty()) {
                for (String projCode : projCodeSet) {
                    ProjEstablishBaseInfo etbBaseInfo = etbCodeMap.get(projCode);
                    ProjReviewBaseInfo rvBaseInfo = rvCodeMap.get(projCode);
                    ProjPricingBaseInfo pricingBaseInfo = pricingCodeMap.get(projCode);
                    List<SponsorClientListNewRSP.ClientProjRSP> projRSPList = map.get(projCode);
                    if (projRSPList != null && !projRSPList.isEmpty()) {
                        for (SponsorClientListNewRSP.ClientProjRSP projRSP : projRSPList) {
                            List<SponsorClientListNewRSP.ClientProjRSP> tempClientProjRSPList = reSetClientProjRSP(projCode, e.getId(), projRSP, etbBaseInfo, rvBaseInfo, pricingBaseInfo, rvContractMap);
                            res.addAll(tempClientProjRSPList);
                        }
                    } else {
                        SponsorClientListNewRSP.ClientProjRSP projRsp = new SponsorClientListNewRSP.ClientProjRSP();
                        List<SponsorClientListNewRSP.ClientProjRSP> tempClientProjRSPList = reSetClientProjRSP(projCode, e.getId(), projRsp, etbBaseInfo, rvBaseInfo, pricingBaseInfo, rvContractMap);
                        res.addAll(tempClientProjRSPList);
                    }
                }
            } else {
                if (!clientProjRSPList.isEmpty()) {
                    for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : clientProjRSPList) {
                        clientProjRSP.setProjStatus(ClientProjectStatus.NON_ESTABLISH.display);
                        res.add(clientProjRSP);
                    }
                } else {
                    SponsorClientListNewRSP.ClientProjRSP projRsp = new SponsorClientListNewRSP.ClientProjRSP();
                    projRsp.setProjStatus(ClientProjectStatus.NON_ESTABLISH.display);
                    res.add(projRsp);
                }
            }
            e.setClientProjRSPList(res);
            //非公海客户设置客户状态，客户本身在审批中或者其他关联的模块在审批中，则为审批中
            //公海客户检查立项状态
            if (!authorityUtil.isIntraGroupCollaboration(e.getId())) {
                e.setInProcess(
                        UNDER_APPROVAL.name().equals(e.getProcessStatus())
                                || inTransferSet.contains(e.getId())
                                || e.getClientProjRSPList().stream().anyMatch(p -> Boolean.TRUE.equals(p.getInProcess()))
                );
            } else {
                boolean inProcess = false;
                if (projEstablishIdsMap.containsKey(e.getId())) {
                    List<Long> projEstablishIds = projEstablishIdsMap.get(e.getId());
                    if (projEstablishIds != null && !projEstablishIds.isEmpty()) {
                        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = etbBaseInfoService.listByIds(projEstablishIds);
                        Set<String> projectCodeSet = projEstablishBaseInfoList.stream().map(ProjEstablishBaseInfo::getProjCode).collect(Collectors.toSet());
                        for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : e.getClientProjRSPList()) {
                            if (clientProjRSP.getProjCode() != null && projectCodeSet.contains(clientProjRSP.getProjCode())) {
                                inProcess = true;
                                break;
                            }
                        }
                    }
                }
                e.setInProcess(
                        inProcess
                                || e.getClientProjRSPList().stream().anyMatch(p -> Boolean.TRUE.equals(p.getInProcess()))
                );
            }
        });
        return result;
    }

    private List<SponsorClientListNewRSP.ClientProjRSP> reSetClientProjRSP(String projCode, Long clientId, SponsorClientListNewRSP.ClientProjRSP projRsp,
                                                                           ProjEstablishBaseInfo etbBaseInfo, ProjReviewBaseInfo rvBaseInfo, ProjPricingBaseInfo pricingBaseInfo, Map<Long, List<ContractBaseInfo>> rvContractMap) {
        List<SponsorClientListNewRSP.ClientProjRSP> rsp = new ArrayList<>();
        if (isNotNull(etbBaseInfo) && isNull(rvBaseInfo) && isNull(pricingBaseInfo)) {
            projRsp.setProjectName(etbBaseInfo.getProjName());
            projRsp.setProjCode(etbBaseInfo.getProjCode());
            projRsp.setBizType(etbBaseInfo.getBizType());
            String projEstablishProcessStatus = etbBaseInfo.getProjEstablishProcessStatus() != null ? ProjProcessState.valueOf(etbBaseInfo.getProjEstablishProcessStatus()).display : "";
            projRsp.setProjStatus(ClientProjectStatus.ESTABLISH.display() + projEstablishProcessStatus);
        } else if (isNotNull(rvBaseInfo)) {
            projRsp.setProjectName(rvBaseInfo.getProjName());
            projRsp.setProjCode(rvBaseInfo.getProjCode());
            projRsp.setBizType(rvBaseInfo.getBizType());
            String projStatus = rvBaseInfo.getProjReviewProcessStatus() != null ? ProjProcessState.valueOf(rvBaseInfo.getProjReviewProcessStatus()).display : null;
            projRsp.setProjStatus(ClientProjectStatus.REVIEW.display() + projStatus);
        } else if (isNotNull(pricingBaseInfo)) {
            projRsp.setProjectName(pricingBaseInfo.getProjName());
            projRsp.setProjCode(pricingBaseInfo.getProjCode());
            projRsp.setBizType(pricingBaseInfo.getBizType());
            String projStatus = pricingBaseInfo.getProjPricingProcessStatus() != null ? ProjProcessState.valueOf(pricingBaseInfo.getProjPricingProcessStatus()).display : null;
            projRsp.setProjStatus(ClientProjectStatus.PRICING.display() + projStatus);
        } else {
            log.error("未知的项目code:{}", projCode);
        }

        //本身立项是否处在审批中
        if (isNotNull(etbBaseInfo)) {
            if (NEW_UNDER_APPROVAL == etbBaseInfo.getProcessStatus() || CHANGING_UNDER_APPROVAL == etbBaseInfo.getProcessStatus()) {
                projRsp.setInProcess(true);
                rsp.add(projRsp);
                return rsp;
            }
        }
        //本身定价是否处在审批中
        if (isNotNull(pricingBaseInfo)) {
            if (NEW_UNDER_APPROVAL == pricingBaseInfo.getProcessStatus() || CHANGING_UNDER_APPROVAL == pricingBaseInfo.getProcessStatus()) {
                projRsp.setInProcess(true);
                rsp.add(projRsp);
                return rsp;
            }
        }
        //判断关联的评审是否处在审批中
        if (isNotNull(rvBaseInfo)) {
            if (NEW_UNDER_APPROVAL == rvBaseInfo.getProcessStatus() || CHANGING_UNDER_APPROVAL == rvBaseInfo.getProcessStatus()) {
                projRsp.setInProcess(true);
                rsp.add(projRsp);
                return rsp;
            }
            //判断评审合同是否处在审批中
            List<ContractBaseInfo> contractList = rvContractMap.getOrDefault(rvBaseInfo.getId(), new ArrayList<>());
            if (contractList != null && !contractList.isEmpty()) {
                for (ContractBaseInfo contractBaseInfo : contractList) {
                    if ((StringUtils.isNotBlank(projRsp.getContractCode())
                            && contractBaseInfo.getContractCode().equalsIgnoreCase(projRsp.getContractCode()))
                            || StringUtils.isBlank(projRsp.getContractCode())) {
                        String projStatus = null;
                        SponsorClientListNewRSP.ClientProjRSP temp = BeanUtil.copyProperties(projRsp, SponsorClientListNewRSP.ClientProjRSP.class);
                        temp.setContractCode(contractBaseInfo.getContractCode());
                        projStatus = contractBaseInfo.getContractProcessStatus() != null ? valueOf(contractBaseInfo.getContractProcessStatus()).display() : null;
                        temp.setProjStatus(ClientProjectStatus.CONTRACT.display() + projStatus);
                        rsp.add(temp);
                        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                                .eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId())
                                .eq(PaymentBaseInfo::getClientId, clientId)
                                .eq(PaymentBaseInfo::getContractCode, contractBaseInfo.getContractCode())
                                .orderByDesc(PaymentBaseInfo::getId));
                        if (paymentBaseInfoList != null && !paymentBaseInfoList.isEmpty()) {
                            projStatus = paymentBaseInfoList.get(0).getPaymentStatus() != null ? PaymentStatusEnum.valueOf(paymentBaseInfoList.get(0).getPaymentStatus()).display() : null;
                            temp.setProjStatus(ClientProjectStatus.PAYMENT.display + projStatus);
                        }
                    }
                }
                if (contractList.stream().anyMatch(
                        item -> CHANGE_COMMIT.name().equals(item.getContractProcessStatus())
                                || NEW_COMMIT.name().equals(item.getContractProcessStatus())
                                || START_RENT_COMMIT.name().equals(item.getContractProcessStatus())
                                || NEW_RECEIPT_COMMIT.name().equals(item.getContractProcessStatus())
                                || SETTLE_COMMIT.name().equals(item.getContractProcessStatus())
                )) {
                    for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : rsp) {
                        clientProjRSP.setInProcess(true);
                    }
                }
            } else {
                rsp.add(projRsp);
            }
        } else {
            rsp.add(projRsp);
        }
        //过滤重复
        List<SponsorClientListNewRSP.ClientProjRSP> res = new ArrayList<>();
        Map<Long, List<SponsorClientListNewRSP.ClientProjRSP>> map = new HashMap<>();
        for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : rsp) {
            map.putIfAbsent(clientProjRSP.getTransferWeightId(), new ArrayList<>());
            map.get(clientProjRSP.getTransferWeightId()).add(clientProjRSP);
        }
        for (Map.Entry<Long, List<SponsorClientListNewRSP.ClientProjRSP>> entry : map.entrySet()) {
            List<SponsorClientListNewRSP.ClientProjRSP> value = entry.getValue();
            res.addAll(value);
        }
        return res;
    }

    @Override
    public R<List<SponsorClientListRSP>> sponsorClientList(SponsorClientListREQ req) {
        List<Client> clientList = clientService.bySponsorIdAndDeptId(req.getBelongSponsorId(), req.getBelongDeptId());
        if (CollUtil.isEmpty(clientList)) {
            return R.ok(new ArrayList<>());
        }
        List<SponsorClientListRSP> result = getSponsorClients(clientList);
        return R.ok(result);
    }

    @Override
    public R<List<SponsorClientListNewRSP>> sponsorClientNewList(SponsorClientListNewREQ req) {
        List<Client> clientList = clientService.bySponsorIdAndDeptId(req.getBelongSponsorId(), req.getBelongDeptId());
        List<Client> res = new ArrayList<>();
        for (Client client : clientList) {
            if (!NORMAL.name().equals(client.getClientType())) {
                res.add(client);
            }
        }
        //公海客户
        List<Client> tempList = getIntraGroupCollaboration(req.getBelongSponsorId());
        if (CollUtil.isEmpty(res) && CollUtil.isEmpty(tempList)) {
            return R.ok(new ArrayList<>());
        }
        List<SponsorClientListNewRSP> result = getNewSponsorClients(res, tempList, null, req.getBelongSponsorId());
        return R.ok(result);
    }

    @Override
    public R<Void> sponsorClientSubmit(SponsorClientSubmitREQ req) {
        List<SponsorClientSubmitREQ.TransferClient> transferClientList = req.getTransferClientList();
        if (CollUtil.isEmpty(transferClientList)) {
            log.info("转交客户列表为空，直接返回");
        }
        List<Long> clientIdList = transferClientList.stream().map(SponsorClientSubmitREQ.TransferClient::getClientId).collect(Collectors.toList());
        List<Client> clients = clientService.listByClientIds(clientIdList);
        List<SponsorClientListRSP> sponsorClients = getSponsorClients(clients);
        for (SponsorClientListRSP client : sponsorClients) {
            if (Boolean.TRUE.equals(client.getInProcess())) {
                err("客户处在审批流程中，不可移交");
            }
            for (SponsorClientListRSP.ClientProjRSP proj : client.getClientProjRSPList()) {
                if (Boolean.TRUE.equals(proj.getInProcess())) {
                    err("客户存在流程中的立项/评审/合同");
                }
            }
        }
        clientTransferService.submit(req);
        return R.ok();
    }

    @Override
    public R<Void> sponsorClientNewSubmit(SponsorClientSubmitNewREQ req) {
        List<SponsorClientSubmitNewREQ.TransferClient> transferClientList = req.getTransferClientList();
        if (CollUtil.isEmpty(transferClientList)) {
            log.info("转交客户列表为空，直接返回");
        }
        if (req.getTransferClientList() != null) {
            List<SponsorClientSubmitNewREQ.TransferClient> clientList = req.getTransferClientList();
            Set<Long> diffToSponsorIds = new HashSet<>();
            Set<Long> diffToBelongDeptIds = new HashSet<>();
            for (SponsorClientSubmitNewREQ.TransferClient transferClient : clientList) {
                List<SponsorClientSubmitNewREQ.ClientProjRSP> clientProjRSPList = transferClient.getClientProjRSPList();
                //未立项
                if (clientProjRSPList.size() == 1) {
                    SponsorClientSubmitNewREQ.ClientProjRSP rsp = clientProjRSPList.get(0);
                    if (StringUtils.isBlank(rsp.getProjCode()) && rsp.getToBelongDeptId() == null) {
                        err("客户新的移交部门为空");
                    } else if (StringUtils.isBlank(rsp.getProjCode()) && rsp.getToSponsorId() == null) {
                        err("客户新的移交主办为空");
                    }
                }
                //信息未填完整
                for (SponsorClientSubmitNewREQ.ClientProjRSP clientProjRSP : clientProjRSPList) {
                    if (StringUtils.isNotBlank(clientProjRSP.getProjCode()) && clientProjRSP.getToBelongDeptId() == null) {
                        err("客户新的移交部门为空");
                    } else if (StringUtils.isNotBlank(clientProjRSP.getProjCode()) && clientProjRSP.getToSponsorId() == null) {
                        err("客户新的移交主办为空");
                    } else if (StringUtils.isNotBlank(clientProjRSP.getProjCode()) && StringUtils.isBlank(clientProjRSP.getProjArchiveStatus())) {
                        err("客户项目资料归属状态为空");
                    }
                    diffToSponsorIds.add(clientProjRSP.getToSponsorId());
                    diffToBelongDeptIds.add(clientProjRSP.getToBelongDeptId());
                }
            }
            if (!diffToSponsorIds.isEmpty() && diffToSponsorIds.size() > 1) {
                err("本次所移交客户中新的所属主办不一致");
            }
            if (!diffToBelongDeptIds.isEmpty() && diffToBelongDeptIds.size() > 1) {
                err("本次所移交客户中新的所属部门不一致");
            }
        }
        List<Long> clientIdList = transferClientList.stream().map(SponsorClientSubmitNewREQ.TransferClient::getId).collect(Collectors.toList());
        List<Client> allClients = clientService.listByClientIds(clientIdList);
        List<Client> clients = new ArrayList<>();
        List<Client> groupClients = new ArrayList<>();
        for (Client client : allClients) {
            if (authorityUtil.isIntraGroupCollaboration(client.getId())) {
                groupClients.add(client);
            } else {
                clients.add(client);
            }
        }

        List<SponsorClientListNewRSP> sponsorClients = getNewSponsorClients(clients, groupClients, null, req.getBelongSponsorId());
        for (SponsorClientListNewRSP client : sponsorClients) {
            if (Boolean.TRUE.equals(client.getInProcess())) {
                err("客户处在审批流程中，不可移交");
            }
            for (SponsorClientListNewRSP.ClientProjRSP proj : client.getClientProjRSPList()) {
                if (Boolean.TRUE.equals(proj.getInProcess())) {
                    err("客户存在流程中的立项/评审/合同");
                }
            }
        }
        clientTransferService.newSubmit(req);
        return R.ok();
    }

    @Override
    public R<Void> sponsorClientNewModify(SponsorClientModifyNewREQ req) {
        Set<Long> clientIds = new HashSet<>(ListUtil.toList(req.getId()));
        List<Client> clientList = new ArrayList<>();
        List<Client> groupClients = new ArrayList<>();
        if (authorityUtil.isIntraGroupCollaboration(req.getId())) {
            groupClients = clientService.listByIds(clientIds);
        } else {
            clientList = clientService.listByIds(clientIds);
        }
        if (CollUtil.isEmpty(clientList) && CollUtil.isEmpty(groupClients)) {
            err("客户为空");
        }
        List<SponsorClientListNewRSP> result = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
            result = getNewSponsorClients(clientList, groupClients, req.getBatchNo(), req.getBelongSponsorId());
        } else {
            result = getNewSponsorClients(clientList, groupClients, null, req.getBelongSponsorId());
        }

        if (CollUtil.isEmpty(result)) {
            err("客户详情为空");
        }
        List<ClientTransferWeight> clientTransferWeightList = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
            clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                    .eq(ClientTransferWeight::getClientId, req.getId())
                    .eq(ClientTransferWeight::getBatchNo, req.getBatchNo())
                    .eq(ClientTransferWeight::getDeleted, 0));
        } else {
            clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                    .eq(ClientTransferWeight::getClientId, req.getId())
                    .isNull(ClientTransferWeight::getBatchNo)
                    //.eq(isNotEmpty(req.getBelongSponsorId()), ClientTransferWeight::getCreateBy, req.getBelongSponsorId())
                    .eq(ClientTransferWeight::getDeleted, 0));
            List<ClientTransferWeight> unused = clientTransferService.removeUnusedClientTransferWeight(clientTransferWeightList);
            if (!unused.isEmpty()) {
                for (ClientTransferWeight unusedWeight : unused) {
                    LambdaUpdateWrapper<ClientTransferWeight> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(ClientTransferWeight::getDeleted, YesOrNoNumberEnum.YES.getCode());
                    updateWrapper.eq(ClientTransferWeight::getId, unusedWeight.getId());
                    transferWeightMapper.update(null, updateWrapper);
                }
            }
            clientTransferWeightList = clientTransferService.getLatestTransferWeight(clientTransferWeightList, unused);
        }
        List<ClientTransferWeight> res = new ArrayList<>();
        if (clientTransferWeightList.isEmpty() || clientTransferWeightList.size() == 1) {
            for (SponsorClientListNewRSP sponsorClientListNewRSP : result) {
                for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : sponsorClientListNewRSP.getClientProjRSPList()) {
                    ClientTransferWeight clientTransferWeight = new ClientTransferWeight();
                    if (StringUtils.isNotBlank(clientProjRSP.getContractCode())
                            && StringUtils.isNotBlank(req.getContractCode())
                            && clientProjRSP.getContractCode().equalsIgnoreCase(req.getContractCode())) {
                        clientTransferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
                        clientTransferWeight.setToCosponsorIds(req.getToCosponsorIds());
                    } else if (StringUtils.isBlank(clientProjRSP.getContractCode())
                            && StringUtils.isBlank(req.getContractCode())
                            && StringUtils.isNotBlank(clientProjRSP.getProjCode())
                            && StringUtils.isNotBlank(req.getProjCode())) {
                        clientTransferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
                        clientTransferWeight.setToCosponsorIds(req.getToCosponsorIds());
                    } else if (StringUtils.isBlank(clientProjRSP.getProjCode())
                            && StringUtils.isBlank(req.getProjCode())) {
                        clientTransferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
                        clientTransferWeight.setToCosponsorIds(req.getToCosponsorIds());
                    }
                    getClientTransferWeight(req, res, clientTransferWeight, clientProjRSP);
                }
            }
        } else if (isNotSameWeightSize(result, clientTransferWeightList)) {
            for (SponsorClientListNewRSP sponsorClientListNewRSP : result) {
                for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : sponsorClientListNewRSP.getClientProjRSPList()) {
                    boolean found = false;
                    for (ClientTransferWeight transferWeight : clientTransferWeightList) {
                        if (transferWeight.getId().equals(clientProjRSP.getTransferWeightId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ClientTransferWeight clientTransferWeight = new ClientTransferWeight();
                        clientTransferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
                        clientTransferWeight.setToCosponsorIds(req.getToCosponsorIds());
                        getClientTransferWeight(req, res, clientTransferWeight, clientProjRSP);
                    }
                }
            }
            for (ClientTransferWeight weight : clientTransferWeightList) {
                LambdaUpdateWrapper<ClientTransferWeight> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(ClientTransferWeight::getCreateTime, LocalDateTime.now());
                updateWrapper.eq(ClientTransferWeight::getId, weight.getId());
                transferWeightMapper.update(null, updateWrapper);
            }
            if (!res.isEmpty()) {
                transferWeightService.saveOrUpdateBatch(res);
            }
            return R.ok();
        } else {
            for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                for (SponsorClientListNewRSP sponsorClientListNewRSP : result) {
                    for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : sponsorClientListNewRSP.getClientProjRSPList()) {
                        if (clientTransferWeight.getId().equals(req.getTransferWeightId())) {
                            clientTransferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
                            clientTransferWeight.setToCosponsorIds(req.getToCosponsorIds());
                        }
                        if (clientTransferWeight.getId().equals(clientProjRSP.getTransferWeightId())) {
                            getClientTransferWeight(req, res, clientTransferWeight, clientProjRSP);
                        }
                        //公海客户立项分在不同主办人
                        if (!clientTransferWeight.getId().equals(req.getTransferWeightId())
                                && !clientTransferWeight.getId().equals(clientProjRSP.getTransferWeightId())
                                && authorityUtil.isIntraGroupCollaboration(req.getId())) {
                            ClientTransferWeight transferWeight = new ClientTransferWeight();
                            transferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
                            transferWeight.setToCosponsorIds(req.getToCosponsorIds());
                            getClientTransferWeight(req, res, transferWeight, clientProjRSP);
                        }
                    }
                }

            }
        }
        if (!res.isEmpty()) {
            transferWeightService.saveOrUpdateBatch(res);
        }
        return R.ok();
    }

    @Override
    public R<Void> sponsorClientNewModifyBatch(SponsorClientModifyNewBatchREQ reqBatch) {
        List<SponsorClientModifyNewREQ> reqList = reqBatch.getSponsorClientModifyNewBatch();
        List<ClientTransferWeight> res = new ArrayList<>();
        for(SponsorClientModifyNewREQ req : reqList){
            Set<Long> clientIds = new HashSet<>(ListUtil.toList(req.getId()));
            List<Client> clientList = new ArrayList<>();
            List<Client> groupClients = new ArrayList<>();
            if (authorityUtil.isIntraGroupCollaboration(req.getId())) {
                groupClients = clientService.listByIds(clientIds);
            } else {
                clientList = clientService.listByIds(clientIds);
            }
            if (CollUtil.isEmpty(clientList) && CollUtil.isEmpty(groupClients)) {
                err("客户为空");
            }
            List<SponsorClientListNewRSP> result = new ArrayList<>();
            if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
                result = getNewSponsorClients(clientList, groupClients, req.getBatchNo(), req.getBelongSponsorId());
            } else {
                result = getNewSponsorClients(clientList, groupClients, null, req.getBelongSponsorId());
            }
            if (CollUtil.isEmpty(result)) {
                err("客户详情为空");
            }
            List<ClientTransferWeight> clientTransferWeightList = new ArrayList<>();
            if (StringUtils.isNotBlank(req.getProcessInstanceId())) {
                clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getClientId, req.getId())
                        .eq(ClientTransferWeight::getBatchNo, req.getBatchNo())
                        .eq(ClientTransferWeight::getDeleted, 0));
            } else {
                clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getClientId, req.getId())
                        .isNull(ClientTransferWeight::getBatchNo)
                        //.eq(isNotEmpty(req.getBelongSponsorId()), ClientTransferWeight::getCreateBy, req.getBelongSponsorId())
                        .eq(ClientTransferWeight::getDeleted, 0));
            }

            if(req.getTransferWeightId() != null){
                ClientTransferWeight clientTransferWeight = transferWeightMapper.selectOne(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getId, req.getTransferWeightId())
                        .eq(ClientTransferWeight::getDeleted, 0));
                if(ObjectUtil.isNotEmpty(clientTransferWeight)){
                    getClientTransferWeight(req, res, clientTransferWeight);
                    //
                    List<ClientTransferWeight> unusedWeights = new ArrayList<>();
                    for (ClientTransferWeight weight : clientTransferWeightList) {
                        if (StringUtils.isNotBlank(clientTransferWeight.getContractCode())
                                &&StringUtils.isNotBlank(weight.getContractCode())
                                &&clientTransferWeight.getContractCode().equals(weight.getContractCode())
                                &&!clientTransferWeight.getId().equals(weight.getId())) {
                            unusedWeights.add(weight);
                        }
                        if (StringUtils.isBlank(clientTransferWeight.getContractCode())
                                &&StringUtils.isBlank(weight.getContractCode())
                                &&StringUtils.isNotBlank(clientTransferWeight.getProjCode())
                                &&StringUtils.isNotBlank(weight.getProjCode())
                                &&clientTransferWeight.getProjCode().equals(weight.getProjCode())
                                &&!clientTransferWeight.getId().equals(weight.getId())) {
                            unusedWeights.add(weight);
                        }
                    }
                    if (!unusedWeights.isEmpty()) {
                        for (ClientTransferWeight unusedWeight : unusedWeights) {
                            LambdaUpdateWrapper<ClientTransferWeight> updateWrapper = new LambdaUpdateWrapper<>();
                            updateWrapper.set(ClientTransferWeight::getDeleted, YesOrNoNumberEnum.YES.getCode());
                            updateWrapper.eq(ClientTransferWeight::getId, unusedWeight.getId());
                            transferWeightMapper.update(null, updateWrapper);
                        }
                    }
                }
            }else{
                ClientTransferWeight clientTransferWeight = new ClientTransferWeight();
                getClientTransferWeight(req, res, clientTransferWeight);
            }
        }
        if (!res.isEmpty()) {
            transferWeightService.saveOrUpdateBatch(res);
        }
        return R.ok();
    }


    @Override
    public R<Void> sponsorClientNewRemove(SponsorClientRemoveNewREQ req) {
        List<Client> clientList = clientService.bySponsorIdAndDeptId(req.getBelongSponsorId(), req.getBelongDeptId());
        List<Client> groupClients = getIntraGroupCollaboration(req.getBelongSponsorId());
        if (CollUtil.isEmpty(clientList)) {
            return R.ok();
        }
        List<SponsorClientListNewRSP> result = getNewSponsorClients(clientList, groupClients, null, req.getBelongSponsorId());
        if (result.isEmpty()) {
            return R.ok();
        }
        List<ClientTransferWeight> clientTransferWeightList = new ArrayList<>();
        for (SponsorClientListNewRSP sponsorClientListNewRSP : result) {
            for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : sponsorClientListNewRSP.getClientProjRSPList()) {
                if (!sponsorClientListNewRSP.getInProcess()) {
                    if (clientProjRSP.getTransferWeightId() != null) {
                        ClientTransferWeight clientTransferWeight = transferWeightMapper.selectOne(Wrappers.<ClientTransferWeight>lambdaQuery()
                                .eq(ClientTransferWeight::getId, clientProjRSP.getTransferWeightId()));
                        clientTransferWeightList.add(clientTransferWeight);
                    }
                }
            }
        }
        if (!clientTransferWeightList.isEmpty()) {
            for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                LambdaUpdateWrapper<ClientTransferWeight> clientTransferWeightWrapper = new LambdaUpdateWrapper<>();
                clientTransferWeightWrapper.eq(ClientTransferWeight::getId, clientTransferWeight.getId());
                clientTransferWeightWrapper.set(ClientTransferWeight::getToSponsorId, null);
                clientTransferWeightWrapper.set(ClientTransferWeight::getIncomeTransferValue, null);
                clientTransferWeightWrapper.set(ClientTransferWeight::getToCosponsorIds, null);
                clientTransferWeightWrapper.set(ClientTransferWeight::getProjArchiveStatus, null);
                clientTransferWeightWrapper.set(ClientTransferWeight::getRiskTransferValue, null);
                clientTransferWeightWrapper.set(ClientTransferWeight::getToDeptId, null);
                transferWeightMapper.update(null, clientTransferWeightWrapper);
            }
        }
        return R.ok();
    }

    private void getClientTransferWeight(SponsorClientModifyNewREQ req, List<ClientTransferWeight> res,
                                         ClientTransferWeight clientTransferWeight, SponsorClientListNewRSP.ClientProjRSP clientProjRSP) {
        clientTransferWeight.setToSponsorId(req.getToSponsorId());
        clientTransferWeight.setToDeptId(req.getToBelongDeptId());
        clientTransferWeight.setRiskTransferValue(req.getRiskTransferValue());
        clientTransferWeight.setIncomeTransferValue(req.getIncomeTransferValue());
        clientTransferWeight.setContractCode(clientProjRSP.getContractCode());
        clientTransferWeight.setClientId(req.getId());
        clientTransferWeight.setProjCode(clientProjRSP.getProjCode());
        res.add(clientTransferWeight);
    }

    private void getClientTransferWeight(SponsorClientModifyNewREQ req, List<ClientTransferWeight> res,
                                         ClientTransferWeight clientTransferWeight) {
        clientTransferWeight.setToSponsorId(req.getToSponsorId());
        clientTransferWeight.setToDeptId(req.getToBelongDeptId());
        clientTransferWeight.setRiskTransferValue(req.getRiskTransferValue());
        clientTransferWeight.setIncomeTransferValue(req.getIncomeTransferValue());
        clientTransferWeight.setContractCode(req.getContractCode());
        clientTransferWeight.setClientId(req.getId());
        clientTransferWeight.setProjCode(req.getProjCode());
        clientTransferWeight.setProjArchiveStatus(req.getProjArchiveStatus());
        clientTransferWeight.setToSponsorId(req.getToSponsorId());
        clientTransferWeight.setToCosponsorIds(req.getToCosponsorIds());
        clientTransferWeight.setUpdateTime(LocalDateTime.now());
        res.add(clientTransferWeight);
    }

    private boolean isNotSameWeightSize(List<SponsorClientListNewRSP> result, List<ClientTransferWeight> weightList) {
        if (result != null) {
            for (SponsorClientListNewRSP sponsorClientListNewRSP : result) {
                if (sponsorClientListNewRSP != null
                        && weightList != null
                        && sponsorClientListNewRSP.getClientProjRSPList() != null
                        && sponsorClientListNewRSP.getClientProjRSPList().size() > weightList.size()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public R<SponsorClientDetailRSP> transferDetail(SponsorClientDetailREQ req) {
        String batchNo = req.getBatchNo();
        List<ClientTransfer> transferList = clientTransferService.list(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, batchNo));
        SponsorClientDetailRSP rsp = new SponsorClientDetailRSP();
        List<Client> clients = clientService.listByClientIds(transferList.stream().map(ClientTransfer::getClientId).collect(Collectors.toSet()));
        List<SponsorClientListRSP> sponsorClients = getSponsorClients(clients);
        //处在审批中，肯定是不在流程中的；这里为了使用统一的方法getSponsorClients，所以把inProcess处理下
        sponsorClients.forEach(e -> e.setInProcess(false));
        rsp.setSponsorClientList(sponsorClients);
        //
        Long belongSponsorId = transferList.get(0).getBelongSponsorId();
        Long belongDeptId = transferList.get(0).getBelongDeptId();
        Long toSponsorId = transferList.get(0).getToSponsorId();
        Long toDeptId = transferList.get(0).getToDeptId();
        List<Long> toCosponsorIds = transferList.get(0).getToCosponsorIds();
        ArrayList<Long> userIdList = ListUtil.toList(belongSponsorId, toSponsorId);
        if (CollUtil.isNotEmpty(toCosponsorIds)) {
            userIdList.addAll(toCosponsorIds);
        }
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> deptMap = id2NameService.deptId2Name(ListUtil.toList(toDeptId, belongDeptId));

        rsp.setBelongSponsorName(sysUserMap.get(belongSponsorId));
        rsp.setBelongSponsorId(belongSponsorId);
        rsp.setBelongDeptId(belongDeptId);
        rsp.setBelongDeptName(deptMap.get(belongDeptId));

        rsp.setToSponsorName(sysUserMap.get(toSponsorId));
        if (CollUtil.isNotEmpty(toCosponsorIds)) {
            rsp.setToCosponsorNames(toCosponsorIds.stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setToDeptName(deptMap.get(toDeptId));
        rsp.setTransferDate(transferList.get(0).getTransferDate());
        return R.ok(rsp);
    }

    @Override
    public R<SponsorClientDetailNewRSP> newTransferDetail(SponsorClientDetailNewREQ req) {
        String batchNo = req.getBatchNo();
        List<ClientTransfer> transferList = clientTransferService.list(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, batchNo));
        Long belongSponsorId = transferList.get(0).getBelongSponsorId();
        SponsorClientDetailNewRSP rsp = new SponsorClientDetailNewRSP();
        List<Client> allClients = clientService.listByClientIds(transferList.stream().map(ClientTransfer::getClientId).collect(Collectors.toSet()));
        List<Client> clients = new ArrayList<>();
        List<Client> groupClients = new ArrayList<>();
        for (Client client : allClients) {
            if (authorityUtil.isIntraGroupCollaboration(client.getId())) {
                groupClients.add(client);
            } else {
                clients.add(client);
            }
        }
        List<SponsorClientListNewRSP> sponsorClients = getNewSponsorClients(clients, groupClients, batchNo, belongSponsorId);
        //处在审批中，肯定是不在流程中的；这里为了使用统一的方法getSponsorClients，所以把inProcess处理下
        sponsorClients.forEach(e -> e.setInProcess(false));
        rsp.setSponsorClientNewList(sponsorClients);
        //
        //Long belongSponsorId = transferList.get(0).getBelongSponsorId();
        Long belongDeptId = transferList.get(0).getBelongDeptId();
        Long toSponsorId = transferList.get(0).getToSponsorId();
        Long toDeptId = transferList.get(0).getToDeptId();
        String description = transferList.get(0).getDescription();
        List<Long> toCosponsorIds = transferList.get(0).getToCosponsorIds();
        ArrayList<Long> userIdList = ListUtil.toList(belongSponsorId, toSponsorId);
        if (CollUtil.isNotEmpty(toCosponsorIds)) {
            userIdList.addAll(toCosponsorIds);
        }
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> deptMap = id2NameService.deptId2Name(ListUtil.toList(toDeptId, belongDeptId));
        rsp.setBelongSponsorName(sysUserMap.get(belongSponsorId));
        rsp.setBelongSponsorId(belongSponsorId);
        rsp.setBelongDeptId(belongDeptId);
        rsp.setBelongDeptName(deptMap.get(belongDeptId));
        rsp.setTransferDate(transferList.get(0).getTransferDate());
        rsp.setDescription(description);
        return R.ok(rsp);
    }

    @Override
    public R<Void> newTransferDetailModify(SponsorClientDetailNewModifyREQ req) {
        clientTransferService.newTransferDetailModify(req);
        return R.ok();
    }


    public R<Void> exportExcel(ClientTransferExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("客户移交" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            download(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出客户移交发生未知异常", e);
            throw new MithrasException("导出客户移交发生未知异常");
        }
        return R.ok();
    }

    private void download(ServletOutputStream outputStream, ClientTransferExportREQ req) {
        List<Client> clientList = clientService.bySponsorIdAndDeptId(req.getBelongSponsorId(), req.getBelongDeptId());
        List<Client> clientTemp = getIntraGroupCollaboration(req.getBelongSponsorId());
        if (CollUtil.isEmpty(clientList) && CollUtil.isEmpty(clientTemp)) {
            throw new MithrasException("不存在数据，导出失败");
        }
        List<Client> clientRes = new ArrayList<>();
        List<Client> groupClients = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getBatchNo())) {
            String batchNo = req.getBatchNo();
            List<ClientTransfer> transferList = clientTransferService.list(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, batchNo));
            List<Client> processClients = clientService.listByClientIds(transferList.stream().map(ClientTransfer::getClientId).collect(Collectors.toSet()));
            clientRes.addAll(processClients);
        } else {
            if (req.getClientIds() != null && !req.getClientIds().isEmpty()) {
                for (Client client : clientList) {
                    for (Long clientId : req.getClientIds()) {
                        if (client.getId().equals(clientId)) {
                            clientRes.add(client);
                        }
                    }
                }
                for (Client client : clientTemp) {
                    for (Long clientId : req.getClientIds()) {
                        if (client.getId().equals(clientId)) {
                            groupClients.add(client);
                        }
                    }
                }
            } else {
                clientRes.addAll(clientList);
                groupClients.addAll(clientTemp);
            }
        }

        List<SponsorClientListNewRSP> sponsorClientListNewRSPList = getNewSponsorClients(clientRes, groupClients, req.getBatchNo(), req.getBelongSponsorId());
        List<ClientTransferExcelModel> excelModels = new ArrayList<>();
        for (SponsorClientListNewRSP clientListNewRSP : sponsorClientListNewRSPList) {
            if (clientListNewRSP.getClientProjRSPList() != null && !clientListNewRSP.getClientProjRSPList().isEmpty()) {
                for (SponsorClientListNewRSP.ClientProjRSP clientProjRSP : clientListNewRSP.getClientProjRSPList()) {
                    ClientTransferExcelModel model = ClientTransferExcelModel.builder()
                            .clientName(clientListNewRSP.getClientName())
                            .clientType(clientListNewRSP.getClientType() != null ? ClientType.valueOf(clientListNewRSP.getClientType()).display() : null)
                            .assertClassifyResult(clientListNewRSP.getAssertClassifyResult() != null ? ClientFiveClassEnum.of(clientListNewRSP.getAssertClassifyResult()).getDisplay() : null)
                            .belongSponsorName(clientListNewRSP.getBelongSponsorName())
                            .belongDeptName(clientListNewRSP.getBelongDeptName())
                            .projectName(clientProjRSP.getProjectName())
                            .projCode(clientProjRSP.getProjCode())
                            .contractCode(clientProjRSP.getContractCode())
                            .bizType(clientProjRSP.getBizType() != null ? ProjectBizType.valueOf(clientProjRSP.getBizType()).display() : null)
                            .toSponsorName(clientProjRSP.getToSponsorName())
                            .toCosponsorNames(clientProjRSP.getToCosponsorNames())
                            .toBelongDeptName(clientProjRSP.getToBelongDeptName())
                            .projStatus((clientProjRSP.getProjStatus()))
                            .projArchiveStatus(clientProjRSP.getProjArchiveStatus() != null ? ClientProjArchiveEnum.valueOf(clientProjRSP.getProjArchiveStatus()).display() : null)
                            .riskTransferValue(clientProjRSP.getRiskTransferValue() != null ? Util.mithrasInteger2BigDecimal(clientProjRSP.getRiskTransferValue()).intValue() : null)
                            .incomeTransferValue(clientProjRSP.getIncomeTransferValue() != null ? Util.mithrasInteger2BigDecimal(clientProjRSP.getIncomeTransferValue()).intValue() : null)
                            .build();
                    excelModels.add(model);
                }
            }

        }
        clientTransferExcelExporter.exportExcel(excelModels, outputStream);
    }

    public List<Client> getIntraGroupCollaboration(Long belongSponsorId) {
        Set<Long> clientIdSet = new HashSet<>();
        //立项
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = establishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjSponsorUserId, belongSponsorId));
        for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfos) {
            if (belongSponsorId.equals(projEstablishBaseInfo.getProjSponsorUserId())) {
                clientIdSet.add(projEstablishBaseInfo.getClientId());
            }
        }
        Set<Long> clientIdList = projEstablishBaseInfos.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toSet());

        //客户的评审数据（可能是一般评审和集团授信评审）
        if (!clientIdList.isEmpty()) {
            List<ProjReviewBaseInfo> projReviewBaseInfos = rvBaseInfoService.listByClients(new ArrayList<>(clientIdList));
            for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfos) {
                if (belongSponsorId.equals(projReviewBaseInfo.getProjSponsorUserId())) {
                    clientIdSet.add(projReviewBaseInfo.getClientId());
                }
            }
        }

        if (!clientIdList.isEmpty()) {
            //评审关联的合同
            Map<Long, List<ContractBaseInfo>> rvContractMap = contractBaseInfoService.listByClients(new ArrayList<>(clientIdList)).stream()
                    .collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));

            // 填充承租人
            List<ContractBaseInfo> contractBaseInfos = new ArrayList<>();
            for (Map.Entry<Long, List<ContractBaseInfo>> entry : rvContractMap.entrySet()) {
                List<ContractBaseInfo> contractBaseInfoList = entry.getValue();
                if (!contractBaseInfoList.isEmpty()) {
                    contractBaseInfos.addAll(contractBaseInfoList);
                }
            }
            Set<Long> contractBaseInfoIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            if (!contractBaseInfoIds.isEmpty()) {
                List<ContractTenantry> contractTenantryList = SpringUtil.getBean(ContractTenantryService.class).listByContractIds(contractBaseInfoIds);
                if (CollectionUtil.isNotEmpty(contractTenantryList)) {
                    clientIdSet.addAll(contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet()));
                }
            }
        }

        //过滤拿到公海客户
        List<Client> res = new ArrayList<>();
        if (!clientIdSet.isEmpty()) {
            List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIdSet));
            for (Client client : clientList) {
                if (authorityUtil.isIntraGroupCollaboration(client.getId())) {
                    res.add(client);
                }
            }
        }
        return res;
    }
}
