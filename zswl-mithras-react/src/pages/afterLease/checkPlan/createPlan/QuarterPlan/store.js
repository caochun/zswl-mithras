import { makeAutoObservable } from '@zswl/admin'
import { ModalStore } from '@zswl/components'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  planId
  quarterProjList = []
  getQuarterProjList = async (data) => {
    this.quarterProjList = await Api.getQuarterProjList(data)
  }

  $editModal = new ModalStore({
    onOpen: (data) => {
      return {
        ...data,
        sponsorUserId: data.sponsorId
          ? {
              value: data.sponsorId,
              label: data.sponsorName,
            }
          : undefined,
        riskManagerId: data.riskManagerId
          ? {
              value: data.riskManagerId,
              label: data.riskManagerName,
            }
          : undefined,
      }
    },
    onFinish: async (values) => {
      const { clientId, id, checkWay, check, riskManagerId, sponsorUserId, reportType } = values
      await Api.saveProjList({
        clientId,
        id,
        planId: this.planId,
        checkWay,
        check,
        sponsorUserId: sponsorUserId?.value || sponsorUserId,
        riskManagerId: riskManagerId?.value || riskManagerId,
        riskManagerName: riskManagerId?.label,
        reportType,
      })
      this.$editModal.close()
      this.getQuarterProjList({ id: this.planId })
      // this.getProjDeptList({ id: this.planId })
    },
  })
}
export default Store
