import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore, TableStore, ModalStore } from '@zswl/components'
import { message } from 'antd'
import { transformClientStatus } from '@/utils/domains/customer/CustomerUtils'
import Api from '@/api/customer/maintainApi'
import customerRatApi from '@/api/customer/customerRat/customerRatApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  baseStore
  page = new PageStore({
    request: async (params) => {
      // 调用接口判断，该客户已被${客户所属业务部门}-${客户所属主办}占有，当前无查看权限。
      const occupyData = await Api.postClientApplyOccupy({
        clientId: params.id,
        processInstanceId: params.processInstanceId,
      })
      if (occupyData?.message) {
        message.warn(occupyData?.message)
        return {
          message: occupyData?.message,
        }
      }
      // 得到客户状态和管控权限 {clientStatus,authorityLevel,isReleased}
      const statusAuthData = await Api.postClientApplyStatus({
        clientId: params.id,
        processInstanceId: params.processInstanceId,
      })

      // 客户详情页面是否有编辑查看权限、是否只显示工商信息 {showCommerceInfo,canEdit}
      const ownData = await Api.postClientApplyOwn({
        clientId: params.id,
        processInstanceId: params.processInstanceId,
      })
      return {
        message: '',
        ...ownData,
        ...statusAuthData,
        //特殊处理， 覆盖clientStatus
        clientStatus: transformClientStatus({
          clientStatus: statusAuthData?.clientStatus,
          isReleased: statusAuthData?.isReleased,
        }),
      }
    },
  })

  // 日志
  changeLog = async (id) => {
    history.push(`/customer/maintain/detail/log/${id}`)
  }

  // 提交权限申请弹窗
  authorityModal = new ModalStore({
    onFinish: async (values) => {
      await Api.postClientAuthorityEffect({
        id: this.page.getParams().id,
        opinion: values.opinion,
      })
      this.authorityModal.close()
      message.success('提交成功')
    },
  })

  //客户信息变更（提交审批）
  clientEffect = async () => {
    await Api.clientEffect({ id: this.page.getParams().id })
    message.success('提交成功！')
  }
  evaluationTable = new TableStore({
    request: async (params) => {
      const { id: clientId } = this.page.getParams()
      return customerRatApi.postClientPage({ clientId })
    },
  })
}
export default Store
