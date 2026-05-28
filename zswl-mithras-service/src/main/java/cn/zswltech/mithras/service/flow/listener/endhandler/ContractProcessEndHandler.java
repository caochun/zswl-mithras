package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.contract.text.ContractTextStatusEnum;
import cn.zswltech.mithras.service.enums.contract.text.SigningWayEnum;
import cn.zswltech.mithras.service.enums.projlifecycle.ProcessEventDescEnum;
import cn.zswltech.mithras.service.flow.listener.endhandler.contract.ContractProcessEndWorkerFactory;
import cn.zswltech.mithras.service.gendoc.render.ContractActualRentRender;
import cn.zswltech.mithras.service.gendoc.render.ContractSettleOwnerChangeRender;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.contract.ContractTextManageMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractSignInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTextManage;
import cn.zswltech.mithras.service.mapper.model.projlifecycle.ProjLifecycleEvent;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.contract.ContractSignInfoAsyncService;
import cn.zswltech.mithras.service.service.contract.ContractSignInfoService;
import cn.zswltech.mithras.service.service.contract.text.ContractTextManageService;
import cn.zswltech.mithras.service.service.contract.text.ContractTextSignInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.CONTRACT;

/**
 * 合同流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
@Slf4j
public class ContractProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {

    @Resource
    private ContractService contractService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ContractProcessEndWorkerFactory contractProcessEndWorkerFactory;
    @Resource
    private ContractSettleOwnerChangeRender contractSettleOwnerChangeRender;
    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private ContractTextManageMapper contractTextManageMapper;
    @Resource
    private ContractTextSignInfoService contractTextSignInfoService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractTextManageService contractTextManageService;
    @Resource
    private ContractSignInfoService contractSignInfoService;
    @Resource
    protected ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractActualRentRender contractActualRentRender;
    @Resource
    private ContractSignInfoAsyncService contractSignInfoAsyncService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return CONTRACT.getModelKeyList().contains(endContext.getModelKey());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        log.info("进入ContractProcessEndHandler.handle方法");
        //合同正常结清 生成所有权转移证书
        if(equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.ContractNormalSettleFlow.name(),ProcessModelTypeEnum.ContractStartRentFlow.name(),ProcessModelTypeEnum.ContractStartRentAutoFlow.name())){
            //获取流程状态
            ProcessBusinessStatusEnum pbs = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
            //    运行中 RUNNING
            //    审批通过 结束 PASS
            //    审批拒绝 结束 REJECT
            //    取消 结束 CANCEL
            //    流程挂起 SUSPEND
            //    审批 一键通过 结束 PASS_ALL
            //    审批 一键拒绝 结束 REJECT_ALL
            switch (pbs) {
                case PASS:
                case PASS_ALL: {
                    this.doPass(endContext,endContext.getModelKey());
                    break;
                }
                case REJECT:
                case REJECT_ALL: {
                    break;
                }
                case CANCEL: {
                    break;
                }
                default: {
                    throw new MithrasException("非法的流程状态");
                }
            }
        }
        contractProcessEndWorkerFactory.getWorker(endContext.getModelKey()).processEnd(endContext.getModelKey(), Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
//        contractService.processEnd(endContext.getModelKey(), Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(Long.valueOf(endContext.getBusinessKey()));
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(contractBaseInfo.getProjReviewId());
        getProjIdAndProjType(endEvent,projReviewBaseInfo);
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("立项评审审批"));
    }
    //合同正常结清审批通过生成所有权转移证书
    //合同起租审批通过生成实际租金表
    private void doPass(ProcessEndContext processEndContext,String modelKey) {
        String processInstanceId = processEndContext.getProcessInstanceId();
        Long contractId = Long.valueOf(processEndContext.getBusinessKey());
        //获取合同信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            // 判断合同文本管理模块是否存在对应记录
            ContractTextManage contractTextManage = SpringUtil.getBean(ContractTextManageMapper.class).selectByContractId(contractId);
            //获取已初始化的所有权转移证书文件---2026年2月6日版本获取页面编辑的文件进行填充
            //获取实际租金表文件数据----2026年2月6日版本
            MaterialsList materialsList = null;
            LambdaQueryWrapper<MaterialsList> query = new LambdaQueryWrapper<>();
            query.eq(MaterialsList::getBelongId, contractBaseInfo.getId());
            if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                //合同结清
                query.eq(MaterialsList::getMaterialsType, ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name());
            }else{
                //合同起租
                query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name());
                query.eq(ObjectUtil.isNotNull(ContractStatus.START_RENT.name()), MaterialsList::getMaterialsType, ContractStatus.START_RENT.name());
                query.eq(MaterialsList::getFilename, "实际租金表" + GlobalConstants.OFFICE_WORD_SUFFIX);
                query.orderByDesc(MaterialsList::getCreateTime);
                query.last(StringUtil.mysqlLimitOne());
            }
            materialsList = materialsListService.getOne(query);

            if(ObjectUtil.isNotEmpty(materialsList)){
                log.info("填充所有权转移证书、实际租金表文件开始........modelKey："+modelKey);
                os = new ByteArrayOutputStream();
                String fileName = "";
                if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                    //合同结清获取所有权转移证书
                    fileName = contractSettleOwnerChangeRender.render2(os, contractBaseInfo,materialsList);
                }else{
                    //合同起租获取实际租金表文档
                    fileName = contractActualRentRender.render2(os, contractBaseInfo,materialsList);
                }
                is = new ByteArrayInputStream(os.toByteArray());
                if(ObjectUtil.isNotEmpty(contractTextManage)){
                    Long fileId = null;
                    if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                        //将填充的所有权转移证书写入主合同附件
                        fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_OWNER_CHANGE.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                    }else{
                        //获取实际租金表附件处理
                        fileId = materialsList.getId();
                        //删除多余待签约信息
                        List<ContractSignInfo> contractSignInfos = contractSignInfoService.list(Wrappers.<ContractSignInfo>lambdaQuery()
                                .eq(ContractSignInfo::getFileId, fileId));
                        List<Long> ids = contractSignInfos.stream().map(ContractSignInfo::getId).collect(Collectors.toList());
                        contractSignInfoService.removeByIds(ids);
                    }
                    //将所有权转移证书、实际租金表写入合同文本管理待签约列表
                    contractSignInfoService.saveContractSignInfoSettle(contractSettleOwnerChangeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractSettleOwnerChangeRender.signatories(contractBaseInfo));
                    //文本用印
                    String account = AccountUtil.getLoginInfo().getAccount();
                    contractSignInfoAsyncService.asyncInvokeSettle(contractId, fileId, contractTextManageService, contractTextManage,processInstanceId,modelKey,account);
                } else {
                    // 合同文本管理模块不存在对应记录，直接用印后反显到文档列表
                    // 存量文本用印
                    String account = AccountUtil.getLoginInfo().getAccount();
                    contractSignInfoAsyncService.asyncInvokeSettle(contractId, materialsList.getId(), processInstanceId ,modelKey, account);
                }
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            throw new MithrasException("签约所有权转移证书发生未知异常！"+e);
        } finally {
            if (Objects.nonNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(os)) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
