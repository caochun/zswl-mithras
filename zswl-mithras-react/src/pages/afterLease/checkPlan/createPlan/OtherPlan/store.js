import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore } from '@zswl/components'
import { message, Modal } from 'antd'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  planId
  notQuarterProjList = []
  getNotQuarterProjList = async (data) => {
    this.notQuarterProjList = await Api.getnotQuarterProjList(data)
  }
  isCreate = true
  $editModal = new ModalStore({
    onOpen: (data) => {
      this.isCreate = !data
      if (data) {
        return {
          ...data,
          sponsorUserId: data.sponsorId
            ? {
                value: data.sponsorId,
                label: data.sponsorName,
              }
            : undefined,
          clientName: {
            key: data.clientId,
            label: data.clientName,
          },
          riskManagerId: data.riskManagerId
            ? {
                value: data.riskManagerId,
                label: data.riskManagerName,
              }
            : undefined,
        }
      }
    },
    onFinish: async (values) => {
      const { clientName, checkWay, riskManagerId, sponsorUserId, id, reportType } = values
      await Api.saveProjList({
        id,
        planId: this.planId,
        clientId: clientName.key || clientName,
        checkWay,
        check: true,
        riskManagerId: riskManagerId?.value,
        riskManagerName: riskManagerId?.label,
        sponsorUserId: sponsorUserId?.value || sponsorUserId,
        reportType,
      })
      this.$editModal.close()
      this.getNotQuarterProjList({ id: this.planId })
    },
  })
  remove = async ({ id }) => {
    Modal.confirm({
      title: `是否移除该客户？`,
      onOk: async () => {
        await Api.removeProjItem({
          id,
        })
        message.success('移除成功')
        this.getNotQuarterProjList({ id: this.planId })
      },
    })
  }
}
export default Store
