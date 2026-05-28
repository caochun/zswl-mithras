import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/common/fileList'
import financialReportApi from '@/api/financialReport/financialReportApi'
import { downFile } from '@/utils'
import { message } from 'antd'

class Store {
  type = null
  currentId = null

  constructor({ listType, id, businessVersion }) {
    this.listType = listType
    this.id = id
    this.businessVersion = businessVersion
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id, version: businessVersion })
    },
  })

  detailModal = new ModalStore({})

  handleDetail = (record) => {
    this.currentId = record.reportInstanceId // 保存当前记录ID
    this.detailModal.open({ ...record, version: this.businessVersion })
  }
  approvalTable = new TableStore({
    request: (values) => {
      return financialReportApi.postFlowApplyList({
        id: this.id,
        ...values,
      })
    },
  })
  handleDownloadTemplate = async () => {
    const { reportCategoryCode } = this.detailModal.getInitialValues()
    // 处理模板下载
    const url = await financialReportApi.postTemplateFileUrl({
      reportCategoryCode,
    })
    downFile(url)
  }
  import = async (file) => {
    const { importSuccess, errorMessageList } = await financialReportApi.postReportImport({
      reportInstanceId: this.currentId,
      file,
    })
    if (importSuccess) {
      message.success('导入成功')
    } else {
      Modal.error({
        width: 800,
        title: '导入失败！不满足以下勾稽关系无法导入！',
        content: (
          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
            {errorMessageList.map((item, index) => (
              <div key={index}>{item}</div>
            ))}
          </div>
        ),
      })
    }
  }
}
export default Store
