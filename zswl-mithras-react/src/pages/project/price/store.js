import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/project/projectPricingApi'
import { timeFormat } from '@/utils'
import { debounce as _debounce } from 'lodash'
import { message } from 'antd'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        createFrom: searchData.createDate ? timeFormat(searchData.createDate[0]) : undefined,
        createTo: searchData.createDate ? timeFormat(searchData.createDate[1]) : undefined,
        updateFrom: searchData.updateDate ? timeFormat(searchData.updateDate[0]) : undefined,
        updateTo: searchData.updateDate ? timeFormat(searchData.updateDate[1]) : undefined,
        createDate: undefined,
        updateDate: undefined,
      }
      return Api.getList(currentData)
    },
  })
  options = getData().optionsType
  getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (this.options && this.options[key]) {
      this.options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }
  /**
   * 撤回
   */
  remove = () => {
    Modal.confirm({
      title: '是否确定关闭审批？',
      onOk: async () => {
        const { keys } = this.table.getSelected()
        if (keys === 0) {
          return
        }
        await Api.remove({ ids: keys })
        this.table.search()
      },
    })
  }

  createModal = new ModalStore({
    onFinish: async (values) => {
      const { curtab } = values
      Modal.confirm({
        title: `是否对该项目进行定价`,
        content: values.projName.label,
        onOk: async () => {
          const { projPricingId } = await Api.postPricingCreate({
            ...values,
            projName: values.projName.label,
            projReviewId: values.projName.value,
          })
          this.createModal.close()
          this.table.search()
          history.push(`/project/price/detail/${projPricingId}?newProject=true`)
        },
      })
    },
  })

  toDetail = (id, bizType) => {
    if (id) {
      history.push(`/customer/maintain/detail/${id}?bizType=${bizType}&typeId=create`)
    }
  }
}
export default Store
