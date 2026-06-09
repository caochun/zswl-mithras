package cn.zswltech.mithras.service.gendoc.render;

import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2025/6/5
 * @description 个人征信授权书
 */
@Component
public class ContractNormalCreditAuthRender extends AbstractContractRender<Client> {
    @Override
    protected Set<Long> signClientIds(Client client) {
        return Collections.emptySet();
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(Client client) {
        return false;
    }

    @Override
    protected String customTemplateKey(Client client) {
        return "";
    }

    @Override
    public String render(OutputStream outputStream, Client client) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put("clientName", client.getClientName());
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("基础资料", "个人征信授权书.docx");
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "个人征信授权书-" + client.getClientName() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }
}
