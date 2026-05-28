import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore, ModalStore, Modal } from '@zswl/components'
import { getIsClientDetailParams } from '@/pages/customer/maintain/utils'
import Api from './api'
import detailStore from '../../store'
import { message } from 'antd'
import moment from 'moment'
const dateFormat = 'yyyy-MM-DD'
class Store {
  constructor(id, businessVersion, startUserId) {
    // this.clientId = id
    // this.businessVersion = businessVersion
    // this.startUserId = startUserId
    makeAutoObservable(this)
  }
  businessVersion

  //关联企业
  affiliated = new TableStore({
    request: async (params, { sorter }) => {
      if (getQuery('typeId') == 'approval') {
        return await Api.getApprovalEnterpriseList({
          clientId: getQuery('businessKey'),
          orderField: sorter.columnKey,
          orderType: sorter.order,
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      }
      return await Api.getRelatedList({
        clientId: this.clientId,
        orderField: sorter.columnKey,
        orderType: sorter.order,
        ...params,
        ...getIsClientDetailParams(),
      })
    },
  })
  //新增关联企业
  enterprisesModal = new ModalStore({
    onOpen(value) {
      if (value) {
        if (getQuery('typeId') == 'approval') {
          let obj2 = {}
          let obj1 = Object.keys(value).map((key) => {
            return { [key]: value[key]?.value }
          })
          obj1.forEach((v) => {
            obj2 = Object.assign(obj2, v)
          })
          obj2.establishDate = obj2.establishDate && moment(obj2.establishDate, dateFormat)
          obj2.industryTypeEnter = obj2.industryTypeName
          return obj2
        } else {
          value.establishDate = value.establishDate && moment(value.establishDate, dateFormat)
          value.industryTypeEnter = value.industryTypeName
          return value
        }
      }
    },
    onFinish: async (values, { id, industryType, industryTypeEnter } = {}) => {
      if (id) {
        values.establishDate = moment(values.establishDate).format('yyyy-MM-DD')
        if (values.industryTypeEnter) {
          if (values.industryTypeEnter == industryTypeEnter) {
            values.industryType = industryType
          } else {
            values.industryType = values.industryTypeEnter[values.industryTypeEnter?.length - 1]
          }
        }

        await Api.editEnterprise({
          id,
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
        })
      } else {
        values.industryType =
          values.industryTypeEnter && values.industryTypeEnter[values.industryTypeEnter.length - 1]
        values.establishDate = moment(values.establishDate).format('yyyy-MM-DD')

        await Api.addEnterprise({
          clientId: getQuery('typeId') == 'approval' ? getQuery('businessKey') : this.clientId,
          ...values,
        })
      }
      this.affiliated.search()
      this.enterprisesModal.close()
      message.success('提交成功！')
    },
  })
  //删除关联企业
  removeEnterprise = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.removeEnterprise({ id: id?.value ?? id })
        this.affiliated.search()
        message.success('删除成功！')
      },
    })
  }
}
export default new Store()
