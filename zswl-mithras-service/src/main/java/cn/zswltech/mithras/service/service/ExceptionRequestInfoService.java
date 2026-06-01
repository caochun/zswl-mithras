package cn.zswltech.mithras.service.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordREQ;
import cn.zswltech.mithras.dto.third.financial.CqApiRecordRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.third.ExceptionSourceENUM;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiRequestInspector;
import cn.zswltech.mithras.service.service.third.financial.impl.handle.CQ2AccountApplicationHandle;
import cn.zswltech.mithras.service.service.third.financial.impl.handle.CQ2CollectionHandle;
import cn.zswltech.mithras.service.service.third.financial.impl.handle.CQ2PaymentHandle;
import cn.zswltech.mithras.service.service.third.financial.impl.handle.CQ2PlanCollectionHandle;
import cn.zswltech.mithras.service.service.third.financial.req.CQ2AccountApplicationReq;
import cn.zswltech.mithras.service.service.third.financial.req.CQ2CollectionReq;
import cn.zswltech.mithras.service.service.third.financial.req.CQ2PaymentReq;
import cn.zswltech.mithras.service.service.third.financial.req.CQ2PlanCollectionReq;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @description 异常请求记录表
* @author vico
* @date 2023-03-31
*/


@Service
public class ExceptionRequestInfoService extends ExceptionRequestRecordService {

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;

    public PageR<CqApiRecordRSP> pageList(CqApiRecordREQ req) {
        Page<ExceptionRequestInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<ExceptionRequestInfo> conditionQuery = Wrappers.lambdaQuery();
        List<String> platformList = PlatformApiEnum.getCqNeedManualApiList().stream().map(Enum::name).collect(Collectors.toList());
        conditionQuery.in(ExceptionRequestInfo::getPlatform, platformList);
        if (StrUtil.isNotBlank(req.getBusinessId())) {
            conditionQuery.like(ExceptionRequestInfo::getBusinessId, req.getBusinessId());
        }
        if (StrUtil.isNotBlank(req.getBillType())) {
            conditionQuery.eq(ExceptionRequestInfo::getPlatform, req.getBillType());
        }
        if (Objects.nonNull(req.getIsDone())) {
            conditionQuery.eq(ExceptionRequestInfo::getRetryFlag, req.getIsDone());
        }
        if (Objects.nonNull(req.getUpdateTimeFrom())) {
            conditionQuery.ge(BaseModel::getUpdateTime, req.getUpdateTimeFrom().atStartOfDay());
        }
        if (Objects.nonNull(req.getUpdateTimeTo())) {
            conditionQuery.lt(BaseModel::getUpdateTime, req.getUpdateTimeTo().plusDays(1).atStartOfDay());
        }
        conditionQuery.like(ObjectUtil.isNotEmpty(req.getBusinessTitle()), ExceptionRequestInfo::getBusinessTitle, req.getBusinessTitle());
        conditionQuery.orderByDesc(ExceptionRequestInfo::getId);
        Page<ExceptionRequestInfo> pageResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<CqApiRecordRSP> list = pageResult.getRecords().stream().map(e -> {
            CqApiRecordRSP rsp = new CqApiRecordRSP();
            rsp.setId(e.getId());
            rsp.setBillType(e.getPlatform());
            rsp.setBusinessId(e.getBusinessId());
            if (Objects.equals(e.getRetryFlag(), YesOrNoNumberEnum.NO.getCode())) {
                rsp.setReqJson(e.getReqData());
                rsp.setResJson(e.getResponse());
            }
            //
            PlatformApiRequestInspector platformApiHandler = platformApiHandleFactory.getPlatformApiRequestInspector(PlatformApiEnum.of(e.getPlatform()));
            if(ObjectUtil.isNotEmpty(platformApiHandler)) {
                //获取说明
                rsp.setSituationDescription(platformApiHandler.getSituationDescription(e.getReqData()));
            }
            rsp.setIsDone(e.getRetryFlag());
            rsp.setSource(e.getSource());
            //关联流水类型名称
            rsp.setSourceName(ExceptionSourceENUM.getSourceName(e.getSource()));
            //接口状态
            rsp.setStatus(getSendStatus(e.getRetryFlag(), e.getWithdrawFlag()));
            if(YesOrNoNumberEnum.YES.getCode().equals(e.getWithdrawFlag())){
                rsp.setWithdrawFailMessage(e.getWithdrawFailMessage());
            }
            rsp.setUpdateTime(e.getUpdateTime());
            rsp.setBusinessTitle(e.getBusinessTitle());
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(list, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }

    private String getSendStatus(Integer retryFlag, Integer withdrawFlag) {
        if (YesOrNoNumberEnum.NO.getCode().equals(retryFlag)) {
            return "推送失败";
        }
        if (YesOrNoNumberEnum.NO.getCode().equals(withdrawFlag)) {
            return "已删除";
        } else {
            return "已推送";
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void operate(Long id, boolean ignore) {
        ExceptionRequestInfo exceptionRequestInfo = this.getById(id);
        if (Objects.isNull(exceptionRequestInfo)) {
            throw new MithrasException("记录不存在");
        }
        if (Objects.equals(exceptionRequestInfo.getRetryFlag(), YesOrNoNumberEnum.YES.getCode())) {
            throw new MithrasException("当前状态不允许操作");
        }
        if (!ignore) {
            // 执行数据重推
            PlatformApiEnum platformApiEnum = PlatformApiEnum.of(exceptionRequestInfo.getPlatform());
            if (Objects.isNull(platformApiEnum)) {
                throw new MithrasException("未定义的单据类型");
            }
            switch (platformApiEnum) {
                case CQ2_PLAN_COLLECTION: {
                    SpringUtil.getBean(CQ2PlanCollectionHandle.class).execute(JSONUtil.toList(exceptionRequestInfo.getReqData(), CQ2PlanCollectionReq.class));
                    break;
                }
                case CQ2_COLLECTION: {
                    SpringUtil.getBean(CQ2CollectionHandle.class).execute(JSONUtil.toList(exceptionRequestInfo.getReqData(), CQ2CollectionReq.class));
                    break;
                }
                case CQ2_PAYMENT: {
                    SpringUtil.getBean(CQ2PaymentHandle.class).execute(JSONUtil.toList(exceptionRequestInfo.getReqData(), CQ2PaymentReq.class));
                    break;
                }
                case CQ2_ACCOUNT_APPLICATION: {
                    SpringUtil.getBean(CQ2AccountApplicationHandle.class).execute(JSONUtil.toBean(exceptionRequestInfo.getReqData(), CQ2AccountApplicationReq.class));
                    break;
                }
                default: {
                    throw new MithrasException("暂不支持的单据类型");
                }
            }
        } else {
            // 变更记录状态
            ExceptionRequestInfo toUpdate = new ExceptionRequestInfo();
            toUpdate.setId(exceptionRequestInfo.getId());
            toUpdate.setRetryFlag(YesOrNoNumberEnum.YES.getCode());
            this.updateById(toUpdate);
        }
    }
}
