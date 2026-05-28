package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.service.enums.projreview.ProjectType;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.lib.LibCommonConvert;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.service.lib.projreview.handler.ProjReviewLibAbstractHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractBaseInfoLibHandler
        extends ContractLibAbstractHandler<ContractBaseInfoLib, ContractBaseInfo, ContractBaseInfoDetailRSP> {

    @Resource
    private Id2NameService id2NameService;

    @Autowired
    private LibCommonConvert<ContractBaseInfo, ContractBaseInfoDetailRSP> libCommonConvert;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("contractStatus");
        fields.add("contractProcessStatus");
        //fields.add("estimatedLeaseDate");
        fields.add("actualLeaseDate");
        fields.add("changeRemark");
        fields.add("itemListHeader");
        fields.add("itemTotalAmount");
        return fields;
    }

    @Override
    protected ContractBaseInfoLib entity2Lib(ContractBaseInfo f) {
        return BeanUtil.copyProperties(f, ContractBaseInfoLib.class);
    }

    @Override
    protected ContractBaseInfo lib2Entity(ContractBaseInfoLib t) {
        return BeanUtil.copyProperties(t, ContractBaseInfo.class);
    }

    @Override
    protected ContractBaseInfoDetailRSP lib2Rsp(ContractBaseInfoLib f) {
        return libCommonConvert.entity2RSP(this.actualLib2Entity(f));
    }


    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.BASE_INFO;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public boolean isMainTable() {
        return true;
    }
}
