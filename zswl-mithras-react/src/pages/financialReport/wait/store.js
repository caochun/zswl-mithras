import { App, Modal, ModalStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import financialReportApi from '@/api/report/financialReportApi'
import { downFile } from '@/utils'
import { message } from 'antd'

class Store {
  type = null
  currentId = null

  constructor({ listType }) {
    this.listType = listType
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (params) => {
      const waitReportStatusList = params.reportStatusList?.length
        ? params.reportStatusList
        : ['WAIT', 'FAILURE']
      return financialReportApi.postReportPageList({
        ...params,
        refereePage: this.listType === 'finish' ? 'reported' : undefined,
        reportStatusList: this.listType === 'wait' ? waitReportStatusList : ['SUCCESS'],
      })
    },
  })
  approvalTable = new TableStore({
    request: (params) => {
      const { reportPeriod, reportPeriodCategory, ...rest } = params
      const reportPeriodCategoryList = reportPeriodCategory ? [reportPeriodCategory] : []
      return financialReportApi.postReportPageList({
        processStatusList: ['APPROVAL_PASS'],
        reportStatusList: ['WAIT', 'FAILURE'],
        refereePage: 'apply',
        reportPeriodCategoryList,
        ...params,
      })
    },
  })
  handleChangeCategory = (value) => {
    const { associationReportCategoryRefPeriod } = App.getData().optionsType
    const periodCategory = associationReportCategoryRefPeriod.find(
      (item) => item.label === value
    )?.value
    const form = this.addModal.getFormStore()
    const isRealTime = periodCategory === 'REALTIME'
    form.setFieldsValue({
      periodCategory,
      year: moment(),
      period: isRealTime ? moment().format('YYYY-MM-DD') : '',
    })
  }
  addModal = new ModalStore({
    onFinish: async (values) => {
      const { year, periodCategory, period, reportCategoryCode } = values
      const response = await financialReportApi.postReportCreate({
        year: moment(year).year(),
        periodCategory,
        period: periodCategory === 'REALTIME' ? 0 : period,
        reportCategoryCode,
      })
      this.table.search()
      this.currentId = response
      this.addModal.close()
      this.detailModal.open({ reportCategoryCode, processStatus: 'UN_SUBMIT' })
    },
  })
  detailModal = new ModalStore({})

  // 金融局上报弹窗
  reportModal = new ModalStore({
    onFinish: async () => {
      const { rows, keys } = this.approvalTable.getSelected()
      const { importSuccess, errorMessageList } = await financialReportApi.postReportPush({
        ids: keys,
      })
      if (importSuccess) {
        message.success('金融局上报成功')
        this.table.search()
        this.reportModal.close()
      } else {
        Modal.error({
          width: 800,
          title: '金融局上报失败！',
          content: (
            <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {errorMessageList.map((item, index) => (
                <div key={index}>{item}</div>
              ))}
            </div>
          ),
        })
      }
    },
  })

  /**
   * 打开金融局上报弹窗
   */
  openReportModal = async () => {
    const params = this.table.getParams()
    this.reportModal.open()
  }
  handleNewReport = () => {
    this.addModal.open({ year: moment() })
  }
  handleDownloadTemplate = async () => {
    const { reportCategoryCode } = this.detailModal.getInitialValues()
    // 处理模板下载
    const url = await financialReportApi.postTemplateFileUrl({
      reportCategoryCode,
    })
    downFile(url)
  }
  import = async (file) => {
    console.log('file: ', file)
    // const { fileList } = DataUpload.classify(files)
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
  submit = async () => {
    const { rows, keys } = this.table.getSelected()
    const reportInstanceIdList = rows.map((item) => item.reportInstanceId)
    const { importSuccess, errorMessageList } = await financialReportApi.postFlowSubmit({
      reportInstanceIdList,
    })
    if (importSuccess) {
      message.success('提交审批成功')
      this.table.search()
    } else {
      Modal.error({
        width: 800,
        title: '提交审批失败！以下报表未维护完整！',
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
  handleDelete = async ({ id }) => {
    const response = await financialReportApi.postReportDelete({ id })
    this.table.search()
  }
  handleDetail = (record) => {
    this.currentId = record.reportInstanceId // 保存当前记录ID
    this.detailModal.open(record)
  }
}
export default Store
