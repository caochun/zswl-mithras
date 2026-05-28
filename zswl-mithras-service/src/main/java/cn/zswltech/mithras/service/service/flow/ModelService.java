package cn.zswltech.mithras.service.service.flow;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.XmlUtil;
import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.domain.req.ModelPageReq;
import cn.zswltech.flow.core.domain.resp.ModelResp;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.NodeDefineEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.flow.model.ModelConfigRSP;
import cn.zswltech.mithras.dto.flow.model.ModelDetailRSP;
import cn.zswltech.mithras.dto.flow.model.ModelListREQ;
import cn.zswltech.mithras.dto.flow.model.ModelListRSP;
import cn.zswltech.mithras.dto.flow.model.SaveModelREQ;
import cn.zswltech.mithras.service.convert.flow.FlowModelConvert;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.mapper.corp.GeneralDictionaryMapper;
import cn.zswltech.mithras.service.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 审批流模型
 *
 * @author wangchuanhao
 * @date 2022/10/24 10:07 PM
 */
@Service
public class ModelService {

    private static final Pattern CDATA_PATTERN = Pattern.compile("<!\\[CDATA\\[(.*?)]]>");


    @Resource
    private FlowModelApiService modelApiService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;

    @Transactional(rollbackFor = Exception.class)
    public String save(SaveModelREQ req) {
        return modelApiService.saveAdaptFront(req.getBpmnXml());
    }

    @SneakyThrows
    public ModelDetailRSP detail(String modelId) {
        ModelDetailRSP modelDetailRSP = new ModelDetailRSP();
        byte[] bs = modelApiService.getBpmnXml(modelId);
//        modelDetailRSP.setBpmnXml(cleanCData(new String(bs)));
        modelDetailRSP.setBpmnXml(new String(bs));
        return modelDetailRSP;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deploy(String modelId) {
        modelApiService.deploy(modelId);
    }

    public PageR<ModelListRSP> list(ModelListREQ req) {
        ModelPageReq flowReq = FlowModelConvert.REQ2FlowReq(req);
        Page<ModelResp> flowRespPage = modelApiService.queryModel(flowReq);
        List<ModelListRSP> resList = flowRespPage.getContents().stream().map(FlowModelConvert::flowResp2RSP).collect(Collectors.toList());
        // 处理下模型名字 统一管控
        resList.forEach(rsp -> rsp.setModelName(Optional.ofNullable(ProcessModelTypeEnum.getByName(rsp.getModelKey())).map(ProcessModelTypeEnum::getDisplay).orElse(rsp.getModelName())));
        return PageR.of(resList, flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String modelId) {
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        if (Objects.isNull(model)) {
            throw new MithrasException("流程模型不存在");
        }
        if (Objects.nonNull(ProcessModelTypeEnum.getByName(model.getKey()))) {
            throw new MithrasException("系统内置流程模型，不允许删除");
        }
        modelApiService.delete(modelId, true);
    }

    /**
     * 流程模型节点字段配置
     * @return
     */
    public ModelConfigRSP modelConfig() {
        ModelConfigRSP modelConfigRSP = new ModelConfigRSP();
        modelConfigRSP.setStartNoneEvent(new ModelConfigRSP.NodeConfig());
        modelConfigRSP.setEndNoneEvent(new ModelConfigRSP.NodeConfig());
        modelConfigRSP.setExclusiveGateway(new ModelConfigRSP.NodeConfig());

        // 执行条件
        ModelConfigRSP.FieldDefinition conditionExpression = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("执行条件")
                .fieldKey("conditionExpression")
                .fieldType("INPUT")
                .build();
        ModelConfigRSP.NodeConfig sequenceFlowConfig = new ModelConfigRSP.NodeConfig();
        sequenceFlowConfig.getFieldList().add(conditionExpression);
        modelConfigRSP.setSequenceFlow(sequenceFlowConfig);

        ModelConfigRSP.NodeConfig userTaskConfig = new ModelConfigRSP.NodeConfig() ;

        // 审批者类型
        ModelConfigRSP.SelectOption modelTarget = new ModelConfigRSP.SelectOption("模型中指定审批人", String.valueOf(UserDefineTypeEnum.MODEL_TARGET.getType()));
        ModelConfigRSP.SelectOption startUser = new ModelConfigRSP.SelectOption("流程发起人", String.valueOf(UserDefineTypeEnum.START_USER.getType()));
        ModelConfigRSP.SelectOption varTarget = new ModelConfigRSP.SelectOption("根据变量选择", String.valueOf(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType()));
        ModelConfigRSP.SelectOption jobTarget = new ModelConfigRSP.SelectOption("根据岗位选择", String.valueOf(UserDefineTypeEnum.PROCESS_START_JOB.getType()));
        ModelConfigRSP.SelectOption custom = new ModelConfigRSP.SelectOption("程序特殊处理", String.valueOf(UserDefineTypeEnum.CUSTOM.getType()));
        ModelConfigRSP.FieldDefinition approverType = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("审批者类型")
                .fieldKey("approverType")
                .fieldType("SELECT")
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("OPTION")
                        .optionList(ListUtil.toList(modelTarget, startUser, varTarget, jobTarget, custom))
                        .singleFlag(true)
                        .build())
                .build();

        // 岗位
        List<GeneralDictionary> jobDictList = generalDictionaryMapper.selectList(Wrappers.<GeneralDictionary>lambdaQuery()
                .eq(GeneralDictionary::getDictKey, "job")
        );
        ModelConfigRSP.FieldDefinition jobCode = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("岗位")
                .fieldKey("jobCode")
                .fieldType("SELECT")
                .dependList(ListUtil.toList(ModelConfigRSP.DependField.builder()
                        .fieldName("approverType")
                        .fieldValue(jobTarget.getValue())
                        .build()))
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("OPTION")
                        .optionList(jobDictList.stream().map(e -> new ModelConfigRSP.SelectOption(e.getDisplay(), e.getCode())).collect(Collectors.toList()))
                        .singleFlag(true)
                        .build())
                .build();

        // 审批取值变量名
        ModelConfigRSP.FieldDefinition userDefineVarName = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("变量名")
                .fieldKey("userDefineVarName")
                .fieldType("INPUT")
                .dependList(ListUtil.toList(ModelConfigRSP.DependField.builder()
                        .fieldName("approverType")
                        .fieldValue(varTarget.getValue())
                        .build()))
                .build();

        // 审批人列表
        ModelConfigRSP.FieldDefinition assgineeList = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("审批人列表")
                .fieldKey("assgineeList")
                .fieldType("SELECT")
                .dependList(ListUtil.toList(ModelConfigRSP.DependField.builder()
                        .fieldName("approverType")
                        .fieldValue(modelTarget.getValue())
                        .build()))
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("API")
                        .apiUrl("/select/founder")
                        .singleFlag(false)
                        .build())
                .build();

        // 审批方式
        ModelConfigRSP.SelectOption oneSelect = new ModelConfigRSP.SelectOption("或签", String.valueOf(ParallelApprovalMethedEnum.ONE.getType()));
        ModelConfigRSP.SelectOption allSelect = new ModelConfigRSP.SelectOption("会签", String.valueOf(ParallelApprovalMethedEnum.ALL.getType()));
        ModelConfigRSP.FieldDefinition parallelApprovalMethed = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("审批方式")
                .fieldKey("parallelApprovalMethed")
                .fieldType("SELECT")
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("OPTION")
                        .optionList(ListUtil.toList(oneSelect, allSelect))
                        .singleFlag(true)
                        .build())
                .build();

        // 首次是否跳过
        ModelConfigRSP.FieldDefinition skipFirst = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("首次是否跳过")
                .fieldKey("skipFirst")
                .fieldType("SELECT")
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("BOOLEAN")
                        .singleFlag(true)
                        .build())
                .build();

        // 按钮列表
        ModelConfigRSP.FieldDefinition buttonList = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("按钮列表")
                .fieldKey("buttonList")
                .fieldType("SELECT")
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("OPTION")
                        .optionList(Stream.of(ApprovalButtonTypeEnum.values()).map(e -> new ModelConfigRSP.SelectOption(e.getConfigureDisplay(), e.name())).collect(Collectors.toList()))
                        .singleFlag(false)
                        .build())
                .build();

        // 动态表单
        ModelConfigRSP.FieldDefinition dynamicFormList = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("动态表单列表")
                .fieldKey("dynamicFormList")
                .fieldType("SELECT")
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("OPTION")
                        .optionList(Stream.of(FlowDynamicFormEnum.values()).map(e -> new ModelConfigRSP.SelectOption(e.name(), e.name())).collect(Collectors.toList()))
                        .singleFlag(false)
                        .build())
                .build();

        // 可回退节点列表
        ModelConfigRSP.FieldDefinition canBackActivityIdList = ModelConfigRSP.FieldDefinition.builder()
                .fieldName("可回退节点列表")
                .fieldKey("canBackActivityIdList")
                .fieldType("SELECT")
                .selectConfig(ModelConfigRSP.SelectConfig.builder()
                        .sourceType("CUSTOM")
                        .singleFlag(false)
                        .build())
                .build();

        userTaskConfig.setFieldList(ListUtil.toList(approverType, jobCode, userDefineVarName, assgineeList, parallelApprovalMethed, skipFirst, buttonList, dynamicFormList, canBackActivityIdList));
        modelConfigRSP.setUserTask(userTaskConfig);

        return modelConfigRSP;
    }

    private static String cleanCData(String originXml) {
        String resXml = originXml;
        Matcher matcher = CDATA_PATTERN.matcher(resXml);
        // 转义逻辑 暂时不这样用 不然报错
//        while (matcher.find()) {
//            String originData = matcher.group();
//            Matcher tmpMatcher = CDATA_PATTERN.matcher(originData);
//            String escapeData = XmlUtil.escape(tmpMatcher.replaceAll("$1"));
//            resXml = resXml.replace(originData, escapeData);
//        }
        while (matcher.find()) {
            resXml = matcher.replaceAll("$1");
        }
        return resXml;
    }

}
