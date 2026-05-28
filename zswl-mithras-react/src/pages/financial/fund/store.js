import { TableStore, Modal, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import Api from '@/pages/financial/fund/api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  $table
  selectedKey = () => {
    const { rows } = this.$table.getSelected()
    if (rows.length > 0) {
      const { id, financingStatus, approvalStatus, projSponsorUserId } = rows[0]
      return {
        id,
        financingStatus,
        approvalStatus,
        projSponsorUserId,
      }
    }
    return null
  }
  sumData = {}
  // $table = new TableStore({
  //   request: async (searchData) => {
  //     const data = await Api.postList({financingStatus:['NEW','EFFECT','CARRY_INTEREST'],...searchData})
  //     this.sumData = data.sum || {}
  //     return data.records
  //   },
  // })

  delete = (record) => {
    Modal.confirm({
      title: '是否确定作废？',
      onOk: async () => {
        await Api.postCancel({ financingId: record.id })
        message.success('操作成功')
        this.$table.search()
      },
    })
  }

  remove = (record) => {
    Modal.confirm({
      title: '是否确定删除？',
      onOk: async () => {
        await Api.postRemove({ financingId: record.id })
        message.success('操作成功')
        this.$table.search()
      },
    })
  }

  $createModal = new ModalStore({
    onFinish: async ({ financingAmount, fundCreditId, businessType }) => {
      const data = await Api.postCreate({ financingAmount, fundCreditId, businessType })
      this.$createModal.close()
      message.success('操作成功')
      history.push(`/financial/fund/detail/${data}?newProject=true`)
      this.$table.search()
    },
  })

  onEffect = () => {
    if (!this.selectedKey()) {
      message.info('选中项为空')
      return
    }
    const { id } = this.selectedKey()
    history.push(`/financial/fund/effect/${id}`)
  }

  onChange = async () => {
    if (!this.selectedKey()) {
      message.info('选中项为空')
      return
    }
    const { id } = this.selectedKey()
    const res = await Api.postChangeCheck({
      financingId: id,
    })
    if (res.isChanging) {
      if (res.changeSubType === 'CHANGE_OTHER') {
        history.push(`/financial/fund/detail/${id}?changeType=${'CHANGE_OTHER'}`)
      } else {
        history.push(`/financial/fund/change/${id}?changeType=${res.changeSubType}`)
      }
    } else {
      this.$changeModal.open()
    }
  }

  $changeModal = new ModalStore({
    onFinish: async ({ changeType }) => {
      const { id } = this.selectedKey()
      if (changeType === 'CHANGE_OTHER') {
        history.push(`/financial/fund/detail/${id}?changeType=${changeType}`)
      } else {
        history.push(`/financial/fund/change/${id}?changeType=${changeType}`)
      }
      this.$changeModal.close()
    },
  })

  submitApproval = async (id, version) => {
    await Api.postFlowEffect({ id, version })
  }
  changeLog = (id) => {
    history.push(`/financial/fund/detail/log/${id}`)
  }
}
export default Store
