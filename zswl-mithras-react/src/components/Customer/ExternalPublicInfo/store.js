import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message, Modal } from 'antd'
import Api from '@/api/customer/externalPublicInfoApi'
// import detailStore from '../store'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  clientId

  publicNum = {}
  //动产抵押
  chattelMortgage = new TableStore({
    request: async (params) => {
      const data = await Api.getMortgageInfoList({ clientId: this.clientId, ...params })
      this.publicNum.dsdy = data.total
      return data
    },
  })
  //股权出质
  equityPledge = new TableStore({
    request: async (params) => {
      const data = await Api.getequityInfoList({ clientId: this.clientId, ...params })
      this.publicNum.gqcz = data.total
      return data
    },
  })
  //行政处罚
  administrative = new TableStore({
    request: async (params) => {
      const data = await Api.getPunishmentInfoList({
        clientId: this.clientId,
        ...params,
      })
      this.publicNum.xzcf = data.total
      return data
    },
  })
  //环保处罚
  environmental = new TableStore({
    request: async (params) => {
      const data = await Api.getPenaltyList({ clientId: this.clientId, ...params })
      this.publicNum.hbcf = data.total
      return data
    },
  })

  //经营异常
  abnormalOperation = new TableStore({
    request: async (params) => {
      const data = await Api.getAbnormalList({ clientId: this.clientId, ...params })
      this.publicNum.jyyc = data.total
      return data
    },
  })
  //司法协助
  judicial = new TableStore({
    request: async (params) => {
      const data = await Api.getJudicialList({ clientId: this.clientId, ...params })
      this.publicNum.sfxz = data.total
      return data
    },
  })
  //法律诉讼
  legalAction = new TableStore({
    request: async (params) => {
      const data = await Api.getLawSuitList({ clientId: this.clientId, ...params })
      this.publicNum.flss = data.total
      return data
    },
  })
  //限制消费令
  limitConsumption = new TableStore({
    request: async (params) => {
      const data = await Api.getConsumptionRestrictionList({
        clientId: this.clientId,
        ...params,
      })
      this.publicNum.xzxfl = data.total
      return data
    },
  })
  //被执行人
  personSubject = new TableStore({
    request: async (params) => {
      const data = await Api.getZhixingInfoList({ clientId: this.clientId, ...params })
      this.publicNum.bzxr = data.total
      return data
    },
  })
  //失信人
  dishonestPeople = new TableStore({
    request: async (params) => {
      const data = await Api.getDishonestList({ clientId: this.clientId, ...params })
      this.publicNum.sxr = data.total
      return data
    },
  })
  //中登网
  zhongDW = new TableStore({
    request: async (params) => {
      const data = await Api.getZhongdengInfoList({
        clientId: this.clientId,
        ...params,
      })
      this.publicNum.zdw = data.total
      return data
    },
  })
  //新增环保处罚
  environmentalModal = new ModalStore({
    onOpen(values) {
      if (values) {
        const { penaltyTime, ...rest } = values
        return {
          penaltyTime: moment(penaltyTime),
          ...rest,
        }
      }
    },
    onFinish: async (values, { id } = {}) => {
      values.penaltyTime = moment(values.penaltyTime).format('yyyy-MM-DD HH:mm:ss')
      if (id) {
        await Api.editEnvironment({
          id,
          ...values,
          clientId: this.clientId,
        })
      } else {
        await Api.addEnvironment({ clientId: this.clientId, ...values })
      }
      this.environmental.search()
      this.environmentalModal.close()
      message.success('提交成功！')
    },
  })
  //删除环保处罚
  deleteEnvironment = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.deleteEnvironment({ id })
        this.environmental.search()
        message.success('删除成功！')
      },
    })
  }
  //新增中登网
  zDwModal = new ModalStore({
    onOpen(values) {
      if (values) {
        const { regDate, regExpireDate, ...rest } = values
        return {
          regDate: moment(regDate),
          regExpireDate: moment(regExpireDate),
          ...rest,
        }
      }
    },
    onFinish: async (values, { id } = {}) => {
      values.regDate = moment(values.regDate).format('yyyy-MM-DD HH:mm:ss')
      values.regExpireDate = moment(values.regExpireDate).format('yyyy-MM-DD HH:mm:ss')
      if (id) {
        await Api.editZhongdengInfo({
          id,
          ...values,
          clientId: this.clientId,
        })
      } else {
        await Api.addZhongdengInfo({ clientId: this.clientId, ...values })
      }
      this.zhongDW.search()
      this.zDwModal.close()
      message.success('提交成功！')
    },
  })
  //删除环保处罚
  deleteZhongdengInfo = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.deleteZhongdengInfo({ id })
        this.zhongDW.search()
        message.success('删除成功！')
      },
    })
  }

  //同步公开信息接口
  synchronizationPublic = async () => {
    await Api.syncExternal({ clientId: this.clientId })
    this.chattelMortgage.search()
    this.equityPledge.search()
    this.administrative.search()
    this.environmental.search()
    this.abnormalOperation.search()
    this.judicial.search()
    this.legalAction.search()
    this.limitConsumption.search()
    this.personSubject.search()
    this.dishonestPeople.search()
    this.zhongDW.search()
    message.success('同步成功！')
  }
}
export default new Store()
