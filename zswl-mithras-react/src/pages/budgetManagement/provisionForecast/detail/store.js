import { makeAutoObservable } from '@zswl/admin'
import { App, Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import predictDetailApi from '@/api/budget/provisioning/predictDetailApi'
import { Input, message } from 'antd'
import predictConfigApi from '@/api/budget/provisioning/predictConfigApi'
import predictListApi from '@/api/budget/provisioning/predictListApi'
import { validateModal } from '@/pages/budget/flowCenter/BankFlow/SpecialPayFlowListModal'

const enum2LabelEnum = (enums = []) => {
  const isObject = !!enums?.[0]?.label

  // 先去重再转化为 { label, value: label } 格式
  const uniqueEnums = [...new Set(enums.map((item) => (isObject ? item?.label : item)))]
  return (uniqueEnums ?? []).map((label) => {
    return { label, value: label }
  })
}
/**
 * 设备预测计划详情页面状态管理类
 * 管理页面数据、表格状态和相关操作
 */

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 参数配置表格
  configTable = new TableStore({
    request: async (params) => {
      const { id } = this.page.getParams()

      const res = await predictConfigApi.postConfigList({ executePredictId: id, ...params })
      return res
    },
  })
  // 数据校对表格
  dataTable = new TableStore({
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await predictDetailApi.postRecordList({ executePredictId: id, ...params })
      return res
    },
  })
  // 模拟拨备计提表格
  provisionTable = new TableStore({
    request: async (params) => {
      const { id } = this.page.getParams()
      const res = await predictDetailApi.postRecordList({ ...params, executePredictId: id })
      return res
    },
  })

  activeTab = 'manual'
  setActiveTab = (tab) => {
    this.activeTab = tab
  }
  fileList = []
  beforeUpload = (file) => {
    this.fileList = [file]
    return false
  }
  addModal = new ModalStore({
    onOpen: (record = {}) => {
      this.setActiveTab('manual')
      this.fileList = []
      if (record.contractExpirationDate) {
        record.contractExpirationDate = moment(record.contractExpirationDate)
      }
      if (record.classify) {
        record.riskLevel = record.classify
      }
      return record ?? {}
    },

    onFinish: async (values) => {
      if (this.activeTab === 'batch') {
        await this.batchImport({
          file: this.fileList[0],
          executePredictId: this.page.getParams().id,
        })
        return
      }
      const payload = { ...values, executePredictId: this.page.getParams().id }

      // 业务类型：使用全局枚举，labelInValue 获取 code 与名称
      if (values.contractLeaseType && typeof values.contractLeaseType === 'object') {
        payload.bizType = values.contractLeaseType.value
        payload.contractLeaseType = values.contractLeaseType.label
      }
      // 合同编号：下拉返回 {label, value}
      if (values.contractCode && typeof values.contractCode === 'object') {
        payload.contractId = values.contractCode.value
        payload.contractCode = values.contractCode.label
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
      if (values.riskLevel) {
        payload.classify = values.riskLevel
      }

      await this.addRecord(payload)
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
      const repeatContract = await predictDetailApi.postRecordAddCheck(record)

      await validateModal(
        {
          title: `「合同编号${repeatContract}」重复，是否继续新增？`,
          content: '点击【确认】则数据覆盖，点击【取消】则回到列表页',
        },
        !!repeatContract
      )
    }

    const func = isEdit ? predictDetailApi.postRecordModify : predictDetailApi.postRecordAdd
    const res = await func({
      ...record,
    })

    message.success('操作成功')
    this.addModal.close()
    // 刷新表格数据
    this.provisionTable.search()
    this.dataTable.search()
  }

  /**
   * 批量导入数据
   * @param {FormData} formData - 包含文件的表单数据
   */
  batchImport = async (formData) => {
    const repeatContract = await predictDetailApi.postRecordCheck(formData)
    await validateModal(
      {
        title: `「合同编号${repeatContract?.join('、')}」重复，是否继续新增？`,
        content: '点击【确认】则数据覆盖，点击【取消】则回到列表页',
      },
      !!repeatContract.length
    )

    const res2 = await predictDetailApi.postRecordImport(formData)

    message.success('导入成功')
    this.addModal.close()
    // 刷新表格数据
    this.provisionTable.search()
    this.dataTable.search()
  }

  /**
   * 打开新增数据弹窗
   */
  openAddModal = (record = {}) => {
    this.addModal.open(record)
  }

  page = new PageStore({
    request: async (params) => {
      const { id: executePredictId } = params
      try {
        const [
          { configEnum: innerBreachMapping },
          { configEnum: ratingMapping },
          { configEnum: lossLgd },
        ] = await Promise.all([
          predictConfigApi.postConfigDetail({
            configCode: 'INNER_BREACH_MAPPING',
            executePredictId,
          }),
          predictConfigApi.postConfigDetail({ configCode: 'RATING_MAPPING', executePredictId }),
          predictConfigApi.postConfigDetail({ configCode: 'LOSS_LGD', executePredictId }),
        ])
        const { innerLevelEnum } = JSON.parse(innerBreachMapping)?.enums ?? {}
        const { outerLevelEnum } = JSON.parse(ratingMapping)?.enums ?? {}
        const { leaseTypeEnum } = JSON.parse(lossLgd)?.enums ?? {}
        const { assetClassifyResultEnum, kpiRatingModelGroupEnum } = App.getData().optionsType
        const enums = {
          assetClassifyResultEnum: enum2LabelEnum(assetClassifyResultEnum),
          kpiRatingModelGroupEnum: enum2LabelEnum(kpiRatingModelGroupEnum),
          innerLevelEnum: enum2LabelEnum(innerLevelEnum),
          outerLevelEnum: enum2LabelEnum(outerLevelEnum),
          leaseTypeEnum: enum2LabelEnum(leaseTypeEnum),
        }
        return {
          enums,
        }
      } catch (error) {
        console.log('error: ', error)
        return { enums: {} }
      }
    },
  })
  /**
   * 模拟拨备计提
   */
  handleProvisionSimulation = async () => {
    const { id } = this.page.getParams()
    await predictListApi.postPredictCalculation({ id })
    await this.provisionTable.search()
    message.success('测算完成')
  }
  itemDelete = async (record) => {
    const res = await predictDetailApi.postRecordRemove({
      executePredictId: this.page.getParams().id,
      id: record.id,
    })

    message.success('删除成功')
    // 刷新表格数据
    this.dataTable.search()
  }
}

export default new Store()
