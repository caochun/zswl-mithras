import { makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, Modal } from '@zswl/components'
import Api from './api'
import { message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }

  orgList = []
  founderList = []
  visible = false
  //地址信息
  table = new TableStore({
    request: async (parameter) => {
      const { projName, projCode, contractCode, ...rest } = parameter
      return await Api.getList({
        ...rest,
        extra: {
          projName,
          projCode,
          contractCode,
        },
      })
    },
  })

  // processInstanceId = null
  // //抄送
  // sendDuplicate = ({ processInstanceId }) => {
  //   this.processInstanceId = processInstanceId
  //   this.visible = true
  // }

  //跳转
  jump = async (value, ids) => {
    const res = await Api.jump({
      processInstanceId: ids,
      activityId: value.join(''),
    })

    message.success('跳转成功')
  }

  fastHandleData = {}
  fastHandleOpen = (id, type) => {
    this.fastHandleModal.open()
    this.fastHandleData = {
      id,
      type,
      typeText: type === 'pass' ? '一键通过' : '一键拒绝',
    }
  }

  fastHandleModal = new ModalStore({
    onFinish: async (values) => {
      const { message: newMessage } = values
      const { type, id } = this.fastHandleData
      const handleApi = type === 'pass' ? Api.pass : Api.reject
      await handleApi({
        processInstanceId: id,
        message: newMessage,
      })
      this.fastHandleModal.close()
      this.table.search()
      message.success('操作成功')
    },
  })
  // //一键通过
  // pass = async (ids) => {
  //   Modal.confirm({
  //     title: `请确认是否【一键通过】该流程？`,
  //     onOk: async () => {
  //       await Api.pass({
  //         processInstanceId: ids,
  //       })
  //       this.table.search()
  //       message.success('操作成功！')
  //     },
  //   })
  // }
  // //一键拒绝
  // reject = async (ids) => {
  //   Modal.confirm({
  //     title: `请确认是否【一键拒绝】该流程？？`,
  //     onOk: async () => {
  //       await Api.reject({
  //         processInstanceId: ids,
  //       })
  //       this.table.search()
  //       message.success('操作成功！')
  //     },
  //   })
  // }
  //转发
  processInstanceId = null
  forWardModalStore = new ModalStore({
    onOpen: async (ids) => {
      this.processInstanceId = ids
    },
    onFinish: async (values) => {
      await Api.forward({
        processInstanceId: this.processInstanceId,
        employeeId: values?.user,
      })
      message.success('操作成功！')
      this.table.search()
      this.forWardModalStore.close()
      this.processInstanceId = null
    },
  })
}
export default Store
