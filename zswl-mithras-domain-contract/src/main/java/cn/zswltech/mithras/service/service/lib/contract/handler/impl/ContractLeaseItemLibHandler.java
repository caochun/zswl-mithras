package cn.zswltech.mithras.service.service.lib.contract.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeaseItemLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.contract.ContractLeaseItemLibService;
import cn.zswltech.mithras.service.service.lib.contract.handler.ContractLibAbstractHandler;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractLeaseItemLibHandler
        extends ContractLibAbstractHandler<ContractLeaseItemLib, ContractLeaseItem, ContractLeaseItemListRSP.RowDataModel> {
    @Resource
    private ContractLeaseItemLibService contractLeaseItemLibService;

    @Override
    protected ContractLeaseItemLib entity2Lib(ContractLeaseItem f) {
        return BeanUtil.copyProperties(f, ContractLeaseItemLib.class);
    }

    @Override
    protected ContractLeaseItem lib2Entity(ContractLeaseItemLib t) {
        return BeanUtil.copyProperties(t, ContractLeaseItem.class);
    }

    @Override
    protected ContractLeaseItemListRSP.RowDataModel lib2Rsp(ContractLeaseItemLib f) {
        ContractLeaseItemListRSP.RowDataModel rowDataModel = new ContractLeaseItemListRSP.RowDataModel();
        rowDataModel.setItemId(f.getOriginId());
        if (StrUtil.isNotBlank(f.getRowData())) {
            Map<String, Object> cellMap = JSONUtil.toBean(f.getRowData().replace("\n", "\\n"), Map.class);
            List<ContractLeaseItemListRSP.CellDataModel> cellDataModelList = new LinkedList<>();
            for (Map.Entry<String, Object> entry : cellMap.entrySet()) {
                cellDataModelList.add(new ContractLeaseItemListRSP.CellDataModel(entry.getKey(), entry.getValue()));
            }
            rowDataModel.setDataList(cellDataModelList);
        }
        return rowDataModel;
    }

    @Override
    public CommonVersionDiffBO libCompareLib(CommonVersion newVersion, CommonVersion oldVersion) {
        if (Objects.isNull(newVersion) || Objects.isNull(oldVersion)) {
            throw new MithrasException("数据比较的两个版本不能为空");
        }
        // 新数据
        LambdaQueryWrapper<ContractLeaseItemLib> newDataQuery = Wrappers.lambdaQuery();
        newDataQuery.eq(ContractLeaseItem::getContractId, newVersion.getMainId());
        newDataQuery.eq(ContractLeaseItemLib::getVersion, newVersion.getVersion());
        newDataQuery.orderByAsc(ContractLeaseItemLib::getOriginId);
        newDataQuery.last(StringUtil.mysqlLimitOne());
        ContractLeaseItemLib newData = contractLeaseItemLibService.getOne(newDataQuery);
        // 老数据
        LambdaQueryWrapper<ContractLeaseItemLib> oldDataQuery = Wrappers.lambdaQuery();
        oldDataQuery.eq(ContractLeaseItem::getContractId, newVersion.getMainId());
        oldDataQuery.eq(ContractLeaseItemLib::getVersion, newVersion.getVersion());
        oldDataQuery.orderByAsc(ContractLeaseItemLib::getOriginId);
        oldDataQuery.last(StringUtil.mysqlLimitOne());
        ContractLeaseItemLib oldData = contractLeaseItemLibService.getOne(oldDataQuery);
        // 因为每一次的租赁物清单都是删除再导入的，所以只要比对最小的编辑区id是否一致，如果不一致说明肯定重新导入了，算作全量变化
        CommonVersionDiffBO commonVersionDiffBO = new CommonVersionDiffBO();
        if (Objects.nonNull(newData) && Objects.nonNull(oldData)) {
            commonVersionDiffBO.setModuleChanged(!Objects.equals(newData.getOriginId(), oldData.getOriginId()));
        } else if (Objects.isNull(newData) && Objects.isNull(oldData)) {
            commonVersionDiffBO.setModuleChanged(Boolean.FALSE);
        } else {
            commonVersionDiffBO.setModuleChanged(Boolean.TRUE);
        }
        Map<String, Object> oldDataMap = new HashMap<>();
        oldDataMap.put("version", oldVersion.getVersion());
        Map<String, DiffValue> newDataMap = new HashMap<>();
        DiffValue diffValue = new DiffValue();
        diffValue.setBeforeValue(oldVersion.getVersion());
        diffValue.setValue(newVersion.getVersion());
        diffValue.setIsChange(Boolean.TRUE);
        newDataMap.put("version", diffValue);
        commonVersionDiffBO.setBeforeData(Collections.singletonList(oldDataMap));
        commonVersionDiffBO.setAfterData(Collections.singletonList(newDataMap));
        return commonVersionDiffBO;
    }

    /*@Override
    public CommonVersionDiffBO libCompareLib(CommonVersion newVersion, CommonVersion oldVersion) {
        CommonVersionDiffBO commonVersionDiffBO = new CommonVersionDiffBO();
        commonVersionDiffBO.setModuleChanged(Boolean.FALSE);
        // FIXME 租赁物清单改为分页后，版本数据比对未处理，统一在租赁物审核分支上开发，租赁物审核分支未发布前租赁物清单的版本不对功能暂时不可用
        return commonVersionDiffBO;
    }*/

    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.LEASE_ITEM;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
