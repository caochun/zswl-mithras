import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/financial/liquidity/dataSet'
import { message } from 'antd'
import orgApi from '@/api/financial/financialManageOrg'
import moment from 'moment'
import _, { uniqueId } from 'lodash'

const formatDetail = (data = []) => {
  const newData = data.reduce((pre, { date, amount, id, ...v }) => {
    const newDate = date && moment(date).format('YYYY-MM-DD')
    const obj = {
      ...v,
      amount,
      date: newDate,
    }
    const find = pre.find((m) => m.date === newDate)
    if (find) {
      find.amount = +find.amount + +amount
    } else {
      pre.push(obj)
    }
    return pre
  }, [])
  return newData
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  overdueRate = undefined
  baseModal = new ModalStore({
    onOpen: async (value) => {
      const data = await Api.postSettingDetail({
        timeFrom: this.newTime.timeFrom,
        timeTo: this.newTime.timeTo,
        overdueRate: this.overdueRate,
      })
      const { timeTo, timeFrom, inDetail, outDetail, ...rest } = data
      return {
        ...rest,
        inDetail: inDetail.map(({ ...v }) => ({
          ...v,
          id: uniqueId(),
        })),
        outDetail: outDetail.map(({ ...v }) => ({
          ...v,
          id: uniqueId(),
        })),
        time: `${timeFrom}~${timeTo}`,
      }
    },

    onFinish: async (values) => {
      const { beginCashflowAmount, otherIncome, otherExpenses, inDetail, outDetail } = values
      const inDetailFormat = await Api.postSettingEdit({
        beginCashflowAmount,
        otherIncome,
        otherExpenses,
        inDetail: formatDetail(inDetail),
        outDetail: formatDetail(outDetail),
      })
      message.success('保存成功')
      const newData = this.time
      this.time = ''
      setTimeout(() => {
        this.time = newData
      }, 0)

      this.baseModal.close()
    },
  })
  newTime = {}
  openModal = () => {
    return this.baseModal.open()
  }
  setOverdueRate = (value) => {
    this.overdueRate = value
  }
  time = ''
  setTime = (value) => {
    this.time = value
  }
  load = 0
  reload = () => {
    this.load++
  }
  // 0:新增 1:初始编辑 2:编辑 :3:查看
  editMode = 0

  createModal = new ModalStore({
    onOpen: async (value) => {
      if (value?.id) {
        this.editMode = 3
        const { addressInfo, ...rest } = await orgApi.postOrganizationDetail({
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
          detail,
          ...rest,
        }
      }
      this.editMode = 0
      return {
        // organizationName: undefined,
        // organizationType: undefined,
        // interBankNo: undefined,
        // uscCode: undefined,
        // contactInfo: {
        //   name: undefined,
        //   job: undefined,
        //   tel: undefined,
        //   email: undefined,
        // },
        // id: undefined,
        // accountsInfo: [],
        // remark: undefined,
      }
    },
  })
}
export default Store
