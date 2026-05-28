package cn.zswltech.mithras.service.service.leaseholdproperty.impl;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFileNameComparisonREQ;
import cn.zswltech.mithras.service.enums.lease.LeaseOCRTypeEnum;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemVatInvoiceService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseOCRService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yupengfei
 * @date 2024/5/15 11:53
 */
@Service
public class LeaseOCRServiceImpl implements LeaseOCRService {

    @Resource
    private LeaseVehicleRegistrationServiceImpl vehicleRegistrationServiceImpl;

    @Resource
    private LeaseItemVatInvoiceService leaseItemVatInvoiceService;

    @Override
    public R<List<String>> fileNameComparison(LeaseFileNameComparisonREQ req) {
        //将文件后缀去除
        List<String> fileNameList = req.getFileNameList().stream().map(fileName -> fileName.split("\\.")[0]).collect(Collectors.toList());
        List<String> lockedFileName = new ArrayList<>();
        //根据操作类型获取发票名称
        if (LeaseOCRTypeEnum.LEASE_VAT_INVOICE.name().equals(req.getLeaseOCRType())) {
            lockedFileName = leaseItemVatInvoiceService.getLockedFileName(req.getLeaseholdId(), req.getOperateType());
        }
        //根据操作类型获取车证名称
        if (LeaseOCRTypeEnum.LEASE_VEHICLE_REGISTRATION.name().equals(req.getLeaseOCRType())) {
            lockedFileName = vehicleRegistrationServiceImpl.getLockedPrefixFileName(req.getLeaseholdId(), req.getOperateType());
        }
        if (CollectionUtils.isEmpty(lockedFileName)) {
            return R.ok();
        }

        //对文件进行过滤，此集合仅保留相同名称的文件名
        fileNameList.retainAll(lockedFileName);

        return R.ok(fileNameList);
    }
}
