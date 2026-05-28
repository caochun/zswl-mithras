import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import moment from 'moment'
import { timeFormat } from '@/utils'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor(data) {
    this.businessVersion = data?.businessVersion
    this.isFormApproval = data?.isFormApproval
    this.contractId = data?.contractId
    this.bizType = data?.bizType
    this.baoJiaShowValue = data?.baoJiaShowValue
    makeAutoObservable(this)
  }
  generate = () => {
    // if (!this.baoJiaShowValue) {
    //   message.error('请先填写报价信息')
    //   return
    // }
    this.generateModal.open({
      planStartDate: this.planStartDate,
    })
  }
  planStartDate = ''
  $table = new TableStore({
    request: async () => {
      if (this.isFormApproval) {
        const res = await Api.getListCompare({
          contractId: this.contractId,
          businessVersion: this.businessVersion,
        })
        this.planStartDate = res.planStartDate?.value
        return res.rentEstimateList.lsitMap
      } else {
        const res = await Api.getList({ contractId: this.contractId })
        this.planStartDate = res.planStartDate
        return res.rentEstimateList
      }
    },
  })

  handleMenuClick = ({ key }) => {
    if (key === '1') {
      this.exportRentData({
        contractId: this.contractId,
        filename: '概算租金表.xlsx',
        businessVersion: this.businessVersion,
      })
    } else if (key === '2') {
      this.exportCashData({
        contractId: this.contractId,
        filename: '概算现金流表.xlsx',
        businessVersion: this.businessVersion,
      })
    }
  }

  // 导出实际租金表
  exportRentData = async (params) => {
    await Api.postExportRentData(params)
    message.success('导出成功')
  }
  // 导出现金
  exportCashData = async (params) => {
    await Api.postExportCashData(params)
    message.success('导出成功')
  }
  // 导入场景值
  // CREATE - 创建合同
  // CHANGE_OTHER - 合同变更-其他

  scene
  generateModal = new ModalStore({
    onOpen: (values) => {
      const { planStartDate } = values
      if (planStartDate) {
        return { planStartDate: moment(planStartDate) }
      }
    },
    onFinish: async (values) => {
      const { file, planStartDate } = values
      Api.postEstimateGenerate({
        id: this.contractId,
        planStartDate: timeFormat(planStartDate),
        scene: this.scene,
      }).then((res) => {
        message.success('生成现金流成功')
        this.generateModal.close()
        this.$table.search({ contractId: this.contractId })
      })
    },
  })
  $createModal = new ModalStore({
    onOpen: (values) => {
      const { planStartDate } = values
      if (planStartDate) {
        return { planStartDate: moment(planStartDate) }
      }
    },
    onFinish: async (values) => {
      const { file, planStartDate } = values
      const { fileList } = DataUpload.classify(file)
      await Api.postImportEstimate({
        file: fileList[0],
        contractId: this.contractId,
        planStartDate: timeFormat(planStartDate),
        scene: this.scene,
      })
      message.success('导入成功')
      this.$createModal.close()
      this.$table.search({ contractId: this.contractId })
    },
  })
}
export default Store
