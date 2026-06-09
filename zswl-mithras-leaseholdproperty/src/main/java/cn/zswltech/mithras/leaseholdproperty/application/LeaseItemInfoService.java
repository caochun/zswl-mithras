package cn.zswltech.mithras.leaseholdproperty.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.LeaseOperationTypeEnum;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author yangxiong
 * @description 针对表【lease_item_info(租赁物管理信息)】的数据库操作Service
 * @createDate 2023-09-19 16:09:40
 */
public interface LeaseItemInfoService extends IService<LeaseItemInfo> {
    /**
     * 获取租赁物信息元数据
     *
     * @param id 租赁物审核管理id
     * @return 元数据信息
     */
    LeaseItemMetadataRSP getLeaseItemMetadata(Long id);

    /**
     * 保存租赁物信息元数据
     *
     * @param req 保存参数
     */
    void saveLeaseItemMetadata(LeaseItemMetadataREQ req);


    void init();

    /**
     * 下载租赁物清单模板
     *
     * @param id           租赁物审核管理数据id
     * @param outputStream 文件流
     */
    void downloadLeaseItemTemplate(Long id, OutputStream outputStream);

    /**
     * 导入租赁物清单
     *
     * @param id          租赁物管理id
     * @param inputStream 导入文件流
     */
    void importItemList(Long id, InputStream inputStream);

    /**
     * 生成租赁物清单文件
     *
     * @param leaseItemInfo 租赁物审核记录信息
     */
    void generateLeaseItemFile(LeaseItemInfo leaseItemInfo);

    /**
     * 导出租赁物清单
     *
     * @param req          查询参数
     * @param outputStream 输出流
     */
    void exportItemList(LeaseItemListExportREQ req, OutputStream outputStream);

    /**
     * 租赁物清单列表
     *
     * @param req 查询条件
     * @return 租赁物清单
     */
    LeaseItemListRSP listItemWithPage(LeaseItemListREQ req);

    /**
     * 台账界面-分页查询租赁物
     *
     * @param param 分页查询参数
     * @return R<PageR < LeaseLedgerMainRSP>>
     */
    R<PageR<LeaseLedgerMainRSP>> getPage(LeaseLedgerMainREQ param);

    /**
     * 台账详情页-通过ID获取合同详情信息
     *
     * @param id 记录ID
     * @return R<LedgerContractDetailRSP>
     */
    R<LedgerContractDetailRSP> getContractInfoById(Long id);

    /**
     * 台账详情页-通过ID获取重复检查部分信息
     *
     * @param id 记录ID
     * @return R<LeaseCheckRepeatRSP>
     */
    R<LeaseCheckRepeatRSP> getLeaseCheckRepeatById(Long id);

    /**
     * 集合导出数据根据记录ID
     *
     * @param outputStream 输出流
     * @param ids          记录ID集合
     */
    void download(ServletOutputStream outputStream, List<Long> ids);

    /**
     * 中登网查重信息保存
     *
     * @param param 参数
     * @return R<Boolean>
     */
    R<Boolean> checkRepeatSave(LeaseCheckRepeatREQ param);

    /**
     * 占有合同ID修改
     *
     * @param contractId 合同ID
     * @param type       操作类型
     * @param targetId   操作对象ID
     * @return 操作结果
     */
    Boolean updateContractIds(Long targetId, Long contractId, LeaseOperationTypeEnum type);

    List<FileUploadRSP> flowUpdate(LeaseFlowUploadREQ param);

    LeaseItemInfo getNewestOne(Long projReviewId);

    void checkData(Long id);
}
