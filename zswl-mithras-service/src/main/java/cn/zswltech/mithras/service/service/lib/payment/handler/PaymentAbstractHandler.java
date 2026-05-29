package cn.zswltech.mithras.service.service.lib.payment.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class PaymentAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long paymentId, boolean needClearLast, Integer versionType) {
        if (!needHandle(paymentId)) {
            return;
        }
        super.flushData(version, paymentId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param paymentId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long paymentId, String version) {
        if (!needHandle(paymentId)) {
            return;
        }
        super.reset(paymentId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "payment_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "payment_id";
    }

    public void validateData(Client client) {
    }

    public abstract PaymentInfoModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.PAYMENT;
    }

   /* //获取单个版本区数据
    public ENTITY getEditionEntityByVersion(String version, Long mainId){
        return lib2Entity(service.getOne(Wrappers.<LIB>query().eq(ILib.FIELD_ORIGIN_ID, mainId)
                .eq(ILib.FIELD_VERSION, version)
                .orderByDesc(ILib.FIELD_ID)
                .last(StringUtil.mysqlLimit(0, 1))));
    }
    //获取版本区数据列表
    public List<ENTITY> getEditionListByVersion(String version, Long mainId){
        List<ENTITY> list = new ArrayList<>();
        for (LIB lib : service.list(Wrappers.<LIB>query().eq(ILib.FIELD_ORIGIN_ID, mainId)
                .eq(ILib.FIELD_VERSION, version))) {
            ENTITY entity = lib2Entity(lib);
            list.add(entity);
        }
        return list;
    }*/
}
