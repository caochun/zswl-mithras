package cn.zswltech.mithras.application.orchestration.job.contract;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.job.service.ContractTextSignJobService;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractActualRentRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractSettleOwnerChangeRender;
import cn.zswltech.mithras.contract.mapper.contract.ContractTextManageMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractSignInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTextManage;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.ContractSignInfoService;
import cn.zswltech.mithras.application.orchestration.contract.text.ContractTextManageService;
import cn.zswltech.mithras.application.orchestration.contract.text.ContractTextSignInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 合同文本用印
 **/
@Slf4j
@Component
public class ContractTextSignJobServiceImpl implements ContractTextSignJobService {

    @Resource
    private ContractSignInfoService contractSignInfoService;
    @Resource
    private ContractTextSignInfoService contractTextSignInfoService;
    @Resource
    private ContractTextManageService contractTextManageService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    private ContractSettleOwnerChangeRender contractSettleOwnerChangeRender;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractActualRentRender contractActualRentRender;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService flowProcessApiService;


    @Override
    public void contractTextSign() throws Exception {
        /*要求必须要传入参数  手动执行用印   暂定合同号*/

        /*针对合同的正常结清的用印  失败的部分重新拉起 */
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            String param = XxlJobHelper.getJobParam();
            JSONObject json = JSONObject.parseObject(param);
            if (json.containsKey("contractId")
                    && StringUtils.isNotEmpty(json.getString("contractId"))
                    && json.containsKey("processInstanceId")
                    && StringUtils.isNotEmpty(json.getString("processInstanceId"))) {
            } else {
                throw new RuntimeException("参数错误 contractId,processInstanceId");
            }

            Long contractId = json.getLong("contractId");
            String processInstanceId = json.getString("processInstanceId");
            String operationAccount = getOperationAccount(processInstanceId);
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
            String account = CharSequenceUtil.isNotEmpty(operationAccount) ? operationAccount : id2NameService.sysUserId2NameSingle(contractBaseInfo.getProjSponsorUserId());
            ContractTextManage contractTextManage = SpringUtil.getBean(ContractTextManageMapper.class).selectByContractId(contractId);

            LambdaQueryWrapper<MaterialsList> query = new LambdaQueryWrapper<>();
            query.eq(MaterialsList::getBelongId, json.getString("contractId"));
            query.eq(MaterialsList::getMaterialsType, ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name());
            query.last("limit 1");
            MaterialsList materialsList = materialsListService.getOne(query);

            if (ObjectUtil.isNotEmpty(materialsList)) {
                log.info("填充所有权转移证书........modelKey：" + ProcessModelTypeEnum.ContractNormalSettleFlow.name());
                os = new ByteArrayOutputStream();
                //合同结清获取所有权转移证书
                String fileName = contractSettleOwnerChangeRender.render2(os, contractBaseInfo, materialsList);
                is = new ByteArrayInputStream(os.toByteArray());

                if (ObjectUtil.isNotEmpty(contractTextManage)) {
                    //将填充的所有权转移证书写入主合同附件
                    Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_OWNER_CHANGE.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);

                    //将所有权转移证书、实际租金表写入合同文本管理待签约列表
                    contractSignInfoService.saveContractSignInfoSettle(contractSettleOwnerChangeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractSettleOwnerChangeRender.signatories(contractBaseInfo));
                    //文本用印
                    asyncInvokeSettle(contractId, fileId, contractTextManage, ProcessModelTypeEnum.ContractNormalSettleFlow.name(), account);
                } else {
                    // 合同文本管理模块不存在对应记录，直接用印后反显到文档列表
                    // 存量文本用印
                    asyncInvokeSettle(contractId, materialsList.getId(), ProcessModelTypeEnum.ContractNormalSettleFlow.name(), account);
                }
            }
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

    /**
     * 合同起租-实际租金表用印
     * @throws Exception
     */
    @Override
    public void contractTextSignRent() throws Exception {
        /*要求必须要传入参数  手动执行用印   暂定合同号*/

        /*针对合同的起租的用印  失败的部分重新拉起 */
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            String param = XxlJobHelper.getJobParam();
            JSONObject json = JSONObject.parseObject(param);
            if (json.containsKey("contractId")
                    && StringUtils.isNotEmpty(json.getString("contractId"))
                    && json.containsKey("processInstanceId")
                    && StringUtils.isNotEmpty(json.getString("processInstanceId"))) {
            } else {
                throw new RuntimeException("参数错误 contractId,processInstanceId");
            }

            Long contractId = json.getLong("contractId");
            String processInstanceId = json.getString("processInstanceId");
            String operationAccount = getOperationAccount(processInstanceId);
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
            String account = CharSequenceUtil.isNotEmpty(operationAccount) ? operationAccount : id2NameService.sysUserId2NameSingle(contractBaseInfo.getProjSponsorUserId());
            ContractTextManage contractTextManage = SpringUtil.getBean(ContractTextManageMapper.class).selectByContractId(contractId);

            LambdaQueryWrapper<MaterialsList> query = new LambdaQueryWrapper<>();
            query.eq(MaterialsList::getBelongId, json.getString("contractId"));
            //合同起租
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name());
            query.eq(ObjectUtil.isNotNull(ContractStatus.START_RENT.name()), MaterialsList::getMaterialsType, ContractStatus.START_RENT.name());
            query.eq(MaterialsList::getFilename, "实际租金表" + GlobalConstants.OFFICE_WORD_SUFFIX);
            query.orderByDesc(MaterialsList::getCreateTime);
            query.last(StringUtil.mysqlLimitOne());
            MaterialsList materialsList = materialsListService.getOne(query);

            if (ObjectUtil.isNotEmpty(materialsList)) {
                log.info("填充所有权转移证书........modelKey：" + ProcessModelTypeEnum.ContractStartRentFlow.name());
                os = new ByteArrayOutputStream();
                //合同结清获取所有权转移证书
                String fileName = contractActualRentRender.render2(os, contractBaseInfo,materialsList);;
                is = new ByteArrayInputStream(os.toByteArray());

                if (ObjectUtil.isNotEmpty(contractTextManage)) {
                    //获取实际租金表附件处理
                    Long fileId = materialsList.getId();
                    //删除多余待签约信息
                    List<ContractSignInfo> contractSignInfos = contractSignInfoService.list(Wrappers.<ContractSignInfo>lambdaQuery()
                            .eq(ContractSignInfo::getFileId, fileId));
                    List<Long> ids = contractSignInfos.stream().map(ContractSignInfo::getId).collect(Collectors.toList());
                    contractSignInfoService.removeByIds(ids);
                    //将所有权转移证书、实际租金表写入合同文本管理待签约列表
                    contractSignInfoService.saveContractSignInfoSettle(contractSettleOwnerChangeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractSettleOwnerChangeRender.signatories(contractBaseInfo));
                    //文本用印
                    asyncInvokeSettle(contractId, fileId, contractTextManage, ProcessModelTypeEnum.ContractStartRentFlow.name(), account);

                } else {
                    // 合同文本管理模块不存在对应记录，直接用印后反显到文档列表
                    // 存量文本用印
                    asyncInvokeSettle(contractId, materialsList.getId(), ProcessModelTypeEnum.ContractStartRentFlow.name(), account);
                }
            }
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

    private void asyncInvokeSettle(Long contractId, Long fileId, ContractTextManage textManage, String
            modelKey, String account) {
        log.info("所有权转移证书、实际租金表准备开始签约........");
        //转换当前文件
        Long signId = contractTextManageService.transformFile(contractId, textManage.getId(), fileId);
        log.info("合同结清合同文本管理模块开始转换文件结束........");
        //针对文件签约
        contractTextSignInfoService.fileSign(signId, account, contractId, modelKey);
    }

    //针对存量没有合同文本管理的数据进行签约
    @Deprecated
    private void asyncInvokeSettle(Long contractId, Long fileId, String modelKey, String account) {
        log.info("文件准备开始签约........");
        //针对存量合同的文件签约
        contractTextSignInfoService.existFileSign(fileId, account, contractId, modelKey);
    }

    private String getOperationAccount(String processInstanceId){
        String accountId = "";
        ProcessHistoryReq historyReq = new ProcessHistoryReq();
        historyReq.setPageIndex(1);
        historyReq.setPageSize(Integer.MAX_VALUE);
        historyReq.setProcessInstanceId(processInstanceId);
        cn.zswltech.flow.core.util.Page<ProcessHistoryResp> history = flowProcessApiService.history(historyReq);
        if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getContents())) {
            List<ProcessHistoryResp> collect = history.getContents().stream()
                    .filter(Objects::nonNull)
                    .filter(e -> Objects.equals("userTask_financeManager", e.getTaskActivityId())
                            || Objects.equals("userTask_headofyyglb2", e.getTaskActivityId()))
                    .sorted(Comparator.comparing(ProcessHistoryResp::getOperateTime,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(collect)) {
                accountId = collect.get(0).getOperatorId();
            }
        }
        log.info("最近一次操作的操作人accountId:"+accountId);
        String account = "";
        if(StringUtils.isNotEmpty(accountId)){
            UserDO user = sysUserService.getSpecificUser(Long.parseLong(accountId));
            account = user.getAccount();
        }
        log.info("最近一次操作的操作人:"+account);
        return account;
    }
}
