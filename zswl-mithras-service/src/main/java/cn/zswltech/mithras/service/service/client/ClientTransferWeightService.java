package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.client.ClientApplyOccupyREQ;
import cn.zswltech.mithras.dto.client.client.ClientApplyOccupyRSP;
import cn.zswltech.mithras.dto.client.client.ClientApplyStatusREQ;
import cn.zswltech.mithras.dto.client.client.ClientApplyStatusRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.ClientAuthEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.service.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.client.ClientTransferMapper;
import cn.zswltech.mithras.service.mapper.client.ClientTransferWeightMapper;
import cn.zswltech.mithras.service.mapper.kpi.KpiProjectDistributionMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.service.mapper.model.client.ClientTransferWeight;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistribution;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.common.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ClientTransferWeightService extends ServiceImpl<ClientTransferWeightMapper, ClientTransferWeight> {


}
