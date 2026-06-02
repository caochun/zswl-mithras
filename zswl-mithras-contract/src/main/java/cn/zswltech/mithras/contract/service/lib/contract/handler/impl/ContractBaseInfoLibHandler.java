package cn.zswltech.mithras.contract.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.service.lib.LibCommonConvert;
import cn.zswltech.mithras.contract.service.lib.contract.handler.ContractLibAbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractBaseInfoLibHandler
        extends ContractLibAbstractHandler<ContractBaseInfoLib, ContractBaseInfo, ContractBaseInfoDetailRSP> {

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
