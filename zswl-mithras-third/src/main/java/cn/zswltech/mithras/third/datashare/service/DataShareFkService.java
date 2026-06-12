package cn.zswltech.mithras.third.datashare.service;

import cn.zswltech.mithras.third.datashare.persistence.model.DataShareFk;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

public interface DataShareFkService extends IService<DataShareFk> {
    Set<String> sendToArchives(String lastModifyStartDate, String lastModifyEndDate);

    void batchGetDetails(List<String> businessNoList);

    void sendCQ2AttachmentSave(DataShareFk dataShareFk);

    void download(String filename, HttpServletResponse response);
}
