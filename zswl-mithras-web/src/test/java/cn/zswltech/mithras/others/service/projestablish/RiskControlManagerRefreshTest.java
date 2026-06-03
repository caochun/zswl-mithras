package cn.zswltech.mithras.others.service.projestablish;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.credit.application.groupcredit.establish.service.GroupCreditEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/21 15:13
 */
public class RiskControlManagerRefreshTest extends ApplicationTest {

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoLibService projEstablishBaseInfoLibService;
    @Resource
    private ClientService clientService;

    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoLibService groupCreditEstablishBaseInfoLibService;

    /**
     * @see BaseModel
     * <p>
     * 注意这个脚本直接跑会将所有数据的更新时间刷新
     * 跑之前将BaseModel中updateTime字段的更新策略删除
     */
    @Test
    public void test() {
        List<ProjEstablishBaseInfo> baseInfos = projEstablishBaseInfoService.list();
        for (ProjEstablishBaseInfo baseInfo : baseInfos) {
            if (baseInfo.getRiskControlManagerId() != null && !baseInfo.getRiskControlManagerId().startsWith("[")) {
                List<Long> ids = Collections.singletonList(Long.parseLong(baseInfo.getRiskControlManagerId()));
                baseInfo.setRiskControlManagerId(JSON.toJSONString(ids));
            }
        }
        projEstablishBaseInfoService.updateBatchById(baseInfos);

        List<ProjEstablishBaseInfoLib> libs = projEstablishBaseInfoLibService.list();
        for (ProjEstablishBaseInfoLib lib : libs) {
            if (lib.getRiskControlManagerId() != null && !lib.getRiskControlManagerId().startsWith("[")) {
                List<Long> ids = Collections.singletonList(Long.parseLong(lib.getRiskControlManagerId()));
                lib.setRiskControlManagerId(JSON.toJSONString(ids));
            }
        }
        projEstablishBaseInfoLibService.updateBatchById(libs);
    }


    /**
     * @see BaseModel
     * <p>
     * 注意这个脚本直接跑会将所有数据的更新时间刷新
     * 跑之前将BaseModel中updateTime字段的更新策略删除
     */
    @Test
    public void test2() {
        List<GroupCreditEstablishBaseInfo> baseInfos = groupCreditEstablishBaseInfoService.list();
        for (GroupCreditEstablishBaseInfo baseInfo : baseInfos) {
            if (baseInfo.getRiskControlManagerId() != null && !baseInfo.getRiskControlManagerId().startsWith("[")) {
                List<Long> ids = Collections.singletonList(Long.parseLong(baseInfo.getRiskControlManagerId()));
                baseInfo.setRiskControlManagerId(JSON.toJSONString(ids));
            }
        }
        groupCreditEstablishBaseInfoService.updateBatchById(baseInfos);

        List<GroupCreditEstablishBaseInfoLib> libs = groupCreditEstablishBaseInfoLibService.list();
        for (GroupCreditEstablishBaseInfoLib lib : libs) {
            if (lib.getRiskControlManagerId() != null && !lib.getRiskControlManagerId().startsWith("[")) {
                List<Long> ids = Collections.singletonList(Long.parseLong(lib.getRiskControlManagerId()));
                lib.setRiskControlManagerId(JSON.toJSONString(ids));
            }
        }
        groupCreditEstablishBaseInfoLibService.updateBatchById(libs);
    }

    @Test
    public void test3() {
        List<ProjEstablishBaseInfo> effect = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, "TAKE_EFFECT"));
        Set<Long> lesseeInfoIds = new HashSet<>();
        Set<Long> guaranteeInfoIds = new HashSet<>();
        effect.forEach(e -> {
            List<ProjEstablishPersonInfo> lesseeInfo = JSON.parseArray(e.getLesseeInfo(), ProjEstablishPersonInfo.class);
            List<ProjEstablishPersonInfo> guaranteeInfo = JSON.parseArray(e.getGuaranteeInfo(), ProjEstablishPersonInfo.class);
            if(ObjectUtil.isNotEmpty(lesseeInfo)){
                lesseeInfo.forEach(a -> lesseeInfoIds.add(a.getClientId()));
            }
            if(ObjectUtil.isNotEmpty(guaranteeInfo)){
                guaranteeInfo.forEach(a -> guaranteeInfoIds.add(a.getClientId()));
            }
        });

        List<Client> list = clientService.list(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientType, ClientType.CORPORATION.name())
                .in(Client::getId, lesseeInfoIds));

        List<Client> list2 = clientService.list(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientType, ClientType.CORPORATION.name())
                .in(Client::getId, guaranteeInfoIds));
        System.out.println("承租人：" + list.size());
        System.out.println("担保人：" + list2.size());
    }
}
