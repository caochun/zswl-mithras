import { TableStore, Modal, ModalStore, PageStore, SearchBarStore, App } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import EclRecordApi from '@/api/budget/provisioning/eclRecordApi'
import moment from 'moment'
import { message } from 'antd'
import provisioning from '@/api/budget/provisioning/provisioning'
import eclBusinessApi from '@/api/budget/provisioning/eclBusinessApi'
import { validateModal } from '@/utils/modal'
import ftpInterestChangeApi from '@/api/budget/pricing/ftpInterestChangeApi'
import { dateRangeTransformV2 } from '@/utils'

const enum2LabelEnum = (enums) => {
  const isObject = !!enums?.[0]?.label

  // 先去重再转化为 { label, value: label } 格式
  const uniqueEnums = [...new Set(enums.map((item) => (isObject ? item?.label : item)))]
  return (uniqueEnums ?? []).map((label) => {
    return { label, value: label }
  })
}
class Store {
  constructor({ afterSubmit }) {
    this.afterSubmit = afterSubmit
    makeAutoObservable(this)
  }
  activeTab = 'manual'
  setActiveTab = (tab) => {
    this.activeTab = tab
  }
  /**
   * 删除记录
   * @param {Object} record - 要删除的记录
   */
  itemDelete = (record) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        const res = await EclRecordApi.postAllRemove({
          id: record.id,
        })

        message.success('删除成功')
        // 刷新表格数据
        this.$table.search()
        this.$exportTable.search()
      },
    })
  }
  compareItemDelete = (record) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        const res = await EclRecordApi.postRecordRemove({
          id: record.id,
        })

        message.success('删除成功')
        // 刷新表格数据
        this.$table.search()
        this.$exportTable.search()
      },
    })
  }
  addModal = new ModalStore({
    onOpen: (record = {}) => {
      this.setActiveTab('manual')
      this.fileList = []
      if (record.promotionResult) {
        record.promotionResultHandle = record.promotionResult
      }
      if (record.contractExpirationDate) {
        record.contractExpirationDate = moment(record.contractExpirationDate)
      }
      if (record.classify) {
        record.riskLevel = record.classify
      }
      return record
    },

    onFinish: async (values) => {
      if (this.activeTab === 'batch') {
        await this.batchImport({ file: this.fileList[0] })
        return
      }
      const payload = { ...values }

      // 业务类型：使用全局枚举，labelInValue 获取 code 与名称
      if (values.contractLeaseType && typeof values.contractLeaseType === 'object') {
        payload.bizType = values.contractLeaseType.value
        payload.contractLeaseType = values.contractLeaseType.label
      }

      // 业务部门：组织选择器，labelInValue 获取 id 与名称
      if (values.profitBelongDeptName && typeof values.profitBelongDeptName === 'object') {
        payload.profitBelongDeptId = values.profitBelongDeptName.value
        payload.profitBelongDeptName = values.profitBelongDeptName.label
      }

      // 项目类型：枚举选择器，labelInValue 获取 code 与名称
      if (values.projClassifyName && typeof values.projClassifyName === 'object') {
        payload.projClassify = values.projClassifyName.value
        payload.projClassifyName = values.projClassifyName.label
      }

      // 到期日：统一日期格式
      if (values.contractExpirationDate) {
        payload.contractExpirationDate = moment(values.contractExpirationDate).format('YYYY-MM-DD')
      }

      await this.addRecord(payload)
    },
  })
  /**
   * 打开新增数据弹窗
   */
  openAddModal = (record = {}) => {
    this.addModal.open(record)
  }

  searchBar = new SearchBarStore({
    onSearch: (values) => {
      this.$table.setParams(values)
      this.$exportTable.setParams(values)
      this.$table.search(values)
      this.$exportTable.search(values)
    },
    optimizeParams: (params) => {
      return { ...params, ...dateRangeTransformV2(params.createTime, 'createTime') }
    },
  })
  /**
   * 添加单条记录
   * @param {Object} record - 记录数据
   */
  addRecord = async (record) => {
    const isEdit = !!record.id
    if (!isEdit) {
      // 检查合同编号是否存在
      const repeatContract = await EclRecordApi.postRecordAddCheck(record)

      await validateModal(
        {
          title: `「合同编号${repeatContract}」重复，是否继续新增？`,
          content: '点击【确认】则数据覆盖，点击【取消】则回到列表页',
        },
        !!repeatContract
      )
    }

    const func = isEdit ? EclRecordApi.postRecordModify : EclRecordApi.postRecordAdd
    const res = await func({
      ...record,
    })

    message.success('操作成功')
    this.addModal.close()
    if (this.afterSubmit) {
      this.afterSubmit?.()
    } else {
      // 刷新表格数据
      this.$table.search()
      this.$exportTable.search()
    }
  }

  /**
   * 批量导入数据
   * @param {FormData} formData - 包含文件的表单数据
   */
  batchImport = async (formData) => {
    const repeatContract = await EclRecordApi.postImportCheck(formData)
    await validateModal(
      {
        title: `「合同编号${repeatContract?.join('、')}」重复，是否继续新增？`,
        content: '点击【确认】则数据覆盖，点击【取消】则回到列表页',
      },
      !!repeatContract.length
    )

    const res2 = await EclRecordApi.postRecordImport(formData)

    message.success('导入成功')
    this.addModal.close()
    // 刷新表格数据
    this.$table.search()
    this.$exportTable.search()
  }

  /**
   * 下载导入模板
   */
  downloadTemplate = async () => {
    await EclRecordApi.getImportTemplate()
  }

  page = new PageStore({
    request: async (params) => {
      try {
        const [
          { configEnum: innerBreachMapping },
          { configEnum: ratingMapping },
          { configEnum: lossLgd },
        ] = await Promise.all([
          eclBusinessApi.postConfigDetail({ configCode: 'INNER_BREACH_MAPPING' }),
          eclBusinessApi.postConfigDetail({ configCode: 'RATING_MAPPING' }),
          eclBusinessApi.postConfigDetail({ configCode: 'LOSS_LGD' }),
        ])
        const { outerLevelEnum } = JSON.parse(ratingMapping)?.enums
        const { innerLevelEnum } = JSON.parse(innerBreachMapping)?.enums
        const { leaseTypeEnum } = JSON.parse(lossLgd)?.enums
        const { assetClassifyResultEnum, kpiRatingModelGroupEnum } = App.getData().optionsType
        return {
          enums: {
            assetClassifyResultEnum: enum2LabelEnum(assetClassifyResultEnum),
            kpiRatingModelGroupEnum: enum2LabelEnum(kpiRatingModelGroupEnum),
            innerLevelEnum: enum2LabelEnum(innerLevelEnum),
            outerLevelEnum: enum2LabelEnum(outerLevelEnum),
            leaseTypeEnum: enum2LabelEnum(leaseTypeEnum),
          },
        }
      } catch (error) {
        return { enums: {} }
      }
    },
  })
  $table = new TableStore({
    request: async (params) => {
      // 使用eclRecordApi接口获取数据
      const { contractNo, clientName, time, ...rest } = params || {}
      const data = await EclRecordApi.postRecordList(params)

      // 字段映射处理
      if (data?.list) {
        data.list = data.list.map((item) => {
          return {
            ...item,
            remainingPrincipal: item.remainPrincipal || '-',
            shouldInterest: item.accruedInterest || '-',
            earnestBalance: item.deposit || '-',
          }
        })
      }
      return data
    },
  })
  $exportTable = new TableStore({
    request: async (params) => {
      const data = await EclRecordApi.postListCompare(params)

      // 字段映射处理
      if (data) {
        return data.map((item) => {
          return {
            ...item,
            remainingPrincipal: item.remainPrincipal || '-',
            shouldInterest: item.accruedInterest || '-',
            earnestBalance: item.deposit || '-',
          }
        })
      }
    },
  })

  //测算
  calc = async () => {
    const data = await provisioning.postInfoRefresh({})
    message.success('测算成功')
    // 刷新表格数据
    this.$table.search()
    this.$exportTable.search()
  }
  fileList = []
  beforeUpload = (file) => {
    this.fileList = [file]
    return false
  }
  onRemove = (file) => {
    const index = this.fileList.indexOf(file)
    const newFileList = this.fileList.slice()
    newFileList.splice(index, 1)
    this.fileList = newFileList
  }
  receiptCodeList = []
  onChangeContractCode = async ({ value }) => {
    const form = this.addModal.getFormStore()
    const res = await ftpInterestChangeApi.postReceiptListByContract(
      { contractId: value },
      'eclReceiptlistByContract'
    )
    this.receiptCodeList = res?.map(({ receiptCode: label }) => ({ label, value: label })) || []
    form.setFieldsValue({
      receiptCode: null,
    })
  }
  exportLoading = false
  setExportLoading = (val) => {
    this.exportLoading = val
  }
}
export default Store
