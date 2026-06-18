import { makeAutoObservable } from '@zswl/admin'
import { message, Modal } from 'antd'
import Api from '../api'

class Store {
  constructor({ planId, detail }) {
    this.planId = planId
    this.detail = detail
    makeAutoObservable(this)
  }

  onCuiBan = async () => {
    const detailData = this.detail
    const restCount = detailData.totalCount - detailData.finishCount
    if (restCount === 0) {
      message.warn('所有客户都已完成检查，无需催办！')
      return
    }
    Modal.confirm({
      title: `该检查计划还有${restCount}个客户未完成检查。是否一键通知？`,
      onOk: async () => {
        await Api.postCuiBan({
          id: this.planId,
        })
        message.success('操作成功')
      },
    })
  }
  handleDownBtn = ({ key }, { deptId, selectedList }) => {
    if (key === 'downDeptReport') {
      this.onDownDeptReport(deptId)
    } else if (key === 'downDeptChooseReport') {
      this.onDownDeptChooseReport(selectedList)
    }
  }
  // 部门报告下载
  onDownDeptReport = async (deptId) => {
    await Api.downDeptReport({
      planId: this.planId,
      deptId,
    })
  }
  // 选中报告下载
  onDownDeptChooseReport = async (selectedList) => {
    if (selectedList?.length === 0) {
      message.info('选中项为空')
      return
    }
    await Api.downDeptChooseReport({
      ids: selectedList,
    })
  }

  quarterProjList = []
  getQuarterProjList = async (data) => {
    this.quarterProjList = await Api.getQuarterProjList(data)
  }
  notQuarterProjList = []
  getNotQuarterProjList = async (data) => {
    this.notQuarterProjList = await Api.getnotQuarterProjList(data)
  }
}
export default Store
