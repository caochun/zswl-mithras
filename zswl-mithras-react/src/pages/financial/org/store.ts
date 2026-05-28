import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/financial/financialManageOrg'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      return Api.postOrganizationList(searchData)
    },
  })
  /**
   * 删除
   */
  delete = () => {
    Modal.confirm({
      title: '是否确定刪除机构？',
      onOk: async () => {
        const { keys: ids } = this.table.getSelected()
        await Api.postOrganizationRemove({ ids })
        this.table.search()
      },
    })
  }

  finish = async (values) => {
    const { area, areaName, country, detail, ...rest } = values
    const [provinceName, cityName, districtName] = areaName ?? []
    const [province, city, district] = area ?? []
    const addressInfo = {
      country: country?.value,
      countryName: country?.label,
      provinceName,
      cityName,
      districtName,
      province,
      city,
      district,
      detail,
    }

    const params = {
      addressInfo,
      ...rest,
    }
    if (params.id) {
      const data = await Api.postOrganizationModify(params)
    } else {
      const data = await Api.postOrganizationAdd(params)
    }
    this.createModal.close()
    this.table.search()
    // history.push(``)
  }
  // 0:新增 1:初始编辑 2:编辑
  editMode = 0
  createModal = new ModalStore({
    onOpen: async (value) => {
      if (value?.id) {
        this.editMode = 1
        const { addressInfo, agreementDepositRateDueTime, ...rest } =
          await Api.postOrganizationDetail({
            id: value.id,
          })
        const {
          country,
          countryName,
          provinceName,
          cityName,
          districtName,
          province,
          city,
          district,
          detail,
        } = addressInfo || {}
        const area = [province, city, district].filter((item) => item)
        return {
          country: {
            label: countryName,
            value: country,
          },
          area,
          areaName: [provinceName, cityName, districtName].filter((item) => item),
          agreementDepositRateDueTime:
            agreementDepositRateDueTime && moment(agreementDepositRateDueTime),
          detail,
          ...rest,
        }
      }
      this.editMode = 0
      return {}
    },
  })
}
export default new Store()
