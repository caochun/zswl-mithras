import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import Api from '@/api/common/fileList'
import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  isHymx = false
  showDistrict = false
  clientDisabled = true
  clientCodeRequire = true
  isUpdate = false
  modelName = ''
  clientId = ''
  page = new PageStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id })
    },
  })
  table = new TableStore({
    request: (params) => {
      return customerRatApi.postClientPage(params)
    },
  })
  createRat = true
  postClientInfo = async(clientId, model, cb) => {
    try {
      const res = await customerRatApi.postClientInfo({ clientId })
      const fieldsValues = this.isUpdate && model ? {...res,model} : {...res}
      this.showDistrict = res.hymxFlag && !res.province
      this.createModal.getFormStore().setFieldsValue(fieldsValues)
      this.createRat = true
    } catch (error) {
      this.createRat = false
      cb && cb()
    }
  }
  createModal = new ModalStore({
    onOpen: (record) => {
      this.clientDisabled = true
      record && this.postClientInfo(record?.clientId, record?.modelCode)
    },
    onFinish: async (params) => {
      if(!this.createRat){
        message.info('客户基本信息不完整，请在客户管理模块维护客户基本信息后再发起评级！')
        return
      }
      const initial = this.createModal.getInitialValues()
      const isEdit = initial?.editType === 'edit'
      if (isEdit) {
        await customerRatApi.postClientUpdate({
          id: initial?.id,
          [(!params.clientCode && params.model.value === 'client_hymx') ? 'clientName' : 'clientId']: initial?.clientId,
          code: params.model.value,
          name: params.model.label,
        })
        message.success('更新成功')
        this.createModal.close()
        this.table.search()
        return
      }
      const { model, ...rest } = params
      let _rest = rest
      if(model.value === 'client_hymx' && _rest.clientId && !params.clientCode){
        _rest = { ..._rest, clientName:rest.clientId }
        delete _rest.clientId
      }else if(this.clientId){
        _rest = { ..._rest, clientId:this.clientId }
      }
      const { exist } = await customerRatApi.postClientAccessCheck({
        ..._rest,
        code: model.value || model,
        name: model.label || this.modelName,
      })
      const add = async () => {
        const { id } = await customerRatApi.postClientAdd({
          ..._rest,
          code: model.value || model,
          name: model.label || this.modelName,
        })
        this.createModal.close()
        this.table.search()
        history.push(`/customer/customerRat/detail/${id}?model=${model.value}`)
      }
      if (exist) {
        Modal.confirm({
          title: '提示',
          content: `当前客户存在正在生效的评级，是否对当前评级进行更新？`,
          okText: '更新',
          onOk: async () => {
            await add()
          },
          cancelText: '不更新',
          onCancel: () => {
            this.createModal.close()
          },
        })
      } else {
        await add()
      }
    },
  })
  delete = async (id) => {
    await customerRatApi.postClientDelete({ id })
    this.table.search()
    message.success('删除成功')
  }
  add = () => {
    this.isUpdate = false
    this.clientDisabled = true
    this.createModal.open()
  }
  edit = (record) => {
    this.isUpdate = true
    this.modelName = record.modelName
    this.clientId = record.clientId
    this.createModal.open({ editType: 'edit', ...record, model:record.modelCode })
  }
  recordModal = new ModalStore({})
  recordTable = new TableStore({
    request: async (params) => {
      const res = await customerRatApi.postClientOverturnRecord(params)
      return res
    },
  })
  view = (id) => {
    this.recordModal.open({ id })
    setTimeout(() => {
      this.recordTable.search({ id })
    }, 10)
  }
}
export default new Store()
