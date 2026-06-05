package cn.zswltech.mithras.guanbao.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.ReportGroupListRSP;
import cn.zswltech.mithras.dto.ReportSelectRSP;
import cn.zswltech.mithras.service.config.guanyuan.GuanYuanConfigProperties;
import cn.zswltech.mithras.guanbao.enums.ManagementReportSourceEnum;
import cn.zswltech.mithras.guanbao.mapper.ManagementReportMapper;
import cn.zswltech.mithras.guanbao.mapper.model.ManagementReport;
import cn.zswltech.mithras.guanbao.util.GuanYuanSsoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @create: 2023-01-04
 **/

@Service
public class ManagementReportService {

    @Resource
    private ManagementReportMapper managementReportMapper;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;
    @Resource
    private GuanYuanConfigProperties guanYuanConfigProperties;


    public List<ReportSelectRSP> reportList(){
        String str = "{\"domainId\":\"guanbi\",\"externalUserId\":\"zulin\"}";
        try {
            String encodedData = GuanYuanSsoUtil.toHexString(GuanYuanSsoUtil.privateEncrypt(str, GuanYuanSsoUtil.getPrivateKey(guanYuanConfigProperties.getPrivateKey())));
            AccountVO loginInfo = AccountUtil.getLoginInfo();
            List<Long> orgs = userOrgJobDOMapper.selectOrgIdByUserId(loginInfo.getId());
            Response<SystemConfigDO> config = systemConfigService.getConfig("managementReportPermission");
            String json = null;
            if (config.getData() != null){
                json = config.getData().getConfigValue();
            }
            Set<String> types = new HashSet<>();
            if (StrUtil.isNotEmpty(json)) {
                JSONObject jsonObject = JSON.parseObject(json);
                JSONObject user = jsonObject.getJSONObject("user");
                String type = user.getString(loginInfo.getAccount());
                if (StrUtil.isNotEmpty(type)){
                    String[] split = type.split(",");
                    types.addAll(ListUtil.toList(split));
                }
                JSONObject org = jsonObject.getJSONObject("org");
                for (Long orgId : orgs){
                    String tmp = org.getString(String.valueOf(orgId));
                    if (StrUtil.isNotEmpty(tmp)){
                        String[] split = tmp.split(",");
                        types.addAll(ListUtil.toList(split));
                    }
                }
            }
            List<ReportSelectRSP> result = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(types)) {
                List<ManagementReport> reportList = managementReportMapper.selectList(Wrappers.<ManagementReport>lambdaQuery().in(ManagementReport::getReportType, types));
                if (CollectionUtil.isNotEmpty(reportList)) {
                    for (ManagementReport managementReport : reportList) {
                        ReportSelectRSP rsp = this.convert(managementReport);
                        if (Objects.equals(managementReport.getReportSource(), ManagementReportSourceEnum.MITHRAS.name())) {
                            // 融租易自研不需要特殊处理
                            result.add(rsp);
                        }
                        if (Objects.equals(managementReport.getReportSource(), ManagementReportSourceEnum.GUAN_YUAN.name())) {
                            rsp.setValue(managementReport.getReportUrl() + "?ps=iframe2&provider=guanbi&ssoToken=" + encodedData);
                            result.add(rsp);
                        }
                    }
                }
            }
            return result;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private ReportSelectRSP convert(ManagementReport managementReport) {
        ReportSelectRSP rsp = new ReportSelectRSP();
        rsp.setId(managementReport.getId());
        rsp.setReportType(managementReport.getReportType());
        rsp.setReportKey(managementReport.getReportKey());
        rsp.setReportSource(managementReport.getReportSource());
        rsp.setLabel(managementReport.getReportName());
        rsp.setValue(managementReport.getReportUrl());
        rsp.setReportTypeName(managementReport.getReportTypeName());
        rsp.setSortNum(managementReport.getSortNum());
        return rsp;
    }

    public List<ReportGroupListRSP> reportGroupList() {
        List<ReportSelectRSP> selectRSPS = this.reportList();
        if (ObjectUtil.isEmpty(selectRSPS)) {
            return new ArrayList<>();
        }
        selectRSPS.removeIf(e ->ObjectUtil.isEmpty(e.getReportTypeName()));
        if (ObjectUtil.isEmpty(selectRSPS)) {
            return new ArrayList<>();
        }
        //获取所有刷新数据
        Response<SystemConfigDO> configRsp = getBean(SystemConfigService.class).getConfig("guanyuanReportRefreshBtn");
        SystemConfigDO data = configRsp.getData();
        List<String> nameList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(data) && ObjectUtil.isNotEmpty(data.getConfigValue())) {
            nameList.addAll(JSONUtil.toList(data.getConfigValue(), String.class));
        }
        //nameList.contains(reportName)
        List<ReportGroupListRSP> rsps = new ArrayList<>();
        Map<String, List<ReportSelectRSP>> stringListMap = selectRSPS.stream().collect(Collectors.groupingBy(ReportSelectRSP::getReportTypeName));
        stringListMap.forEach((k, v) -> {
            if(ObjectUtil.isNotEmpty(v)) {
                ReportGroupListRSP rsp = new ReportGroupListRSP();
                rsp.setTitle(v.get(0).getReportTypeName());
                rsp.setIsLeaf(Boolean.FALSE);
                rsp.setChildren(v.stream().map(select -> new ReportGroupListRSP(select.getReportKey(), select.getLabel(), select.getValue(), nameList.contains(select.getLabel()), Boolean.TRUE, null)).collect(Collectors.toList()));
                rsps.add(rsp);
            }
        });
        return rsps;
    }


}

