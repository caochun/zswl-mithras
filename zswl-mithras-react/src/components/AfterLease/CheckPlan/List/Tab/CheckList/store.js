import { TableStore, ModalStore, Modal } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { timeFormat, yearFormat } from '@/utils'
import { message } from 'antd'
import Api from '@/api/afterLease/checkPlan'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  selectedKey = () => {
    const { rows } = this.$table.getSelected()
    if (rows.length > 0) {
      return rows[0]
    }
    return null
  }

  $table = new TableStore({
    request: (params) => {
      return Api.getPageList({
        ...params,
        year: params.year ? yearFormat(params.year) : undefined,
        deadLineForm: params.deadLineForm ? timeFormat(params.deadLineForm[0]) : undefined,
        deadLineTo: params.deadLineForm ? timeFormat(params.deadLineForm[1]) : undefined,
      })
    },
  })

  // 创建
  createModal = new ModalStore({
    onFinish: async (values) => {
      const { endDate, deadLine, belongSponsorId, ...rest } = values
      const data = await Api.addCheckPlan({
        ...rest,
        belongSponsorId: belongSponsorId?.value,
        deadLine: deadLine ? timeFormat(deadLine) : undefined,
      })

      this.createModal.close()
      this.$table.search()
      if (values.planType === 'COMMONLY') {
        history.push(`/afterLease/checkPlan/commonTemplate/${data}`)
      }
    },
  })

  // 关闭
  closePlan = () => {
    if (!this.selectedKey()) {
      message.info('选中项为空')
      return
    }
    const { planName, id } = this.selectedKey()
    Modal.confirm({
      title: `请确认是否关闭计划：${planName}？`,
      onOk: async () => {
        await Api.closeCheckPlan({ id })
        message.success('操作成功')
        this.$table.search()
      },
    })
  }
  // 编辑
  editPlan = () => {
    if (!this.selectedKey()) {
      message.info('选中项为空')
      return
    }
    const { id } = this.selectedKey()
    history.push(`/afterLease/checkPlan/createPlan/${id}`)
  }
  getPreDetail = async(id) => {
    return await Api.getDetail({id})
  }
}
export default new Store()
