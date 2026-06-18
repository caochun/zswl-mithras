import { makeAutoObservable, http } from '@zswl/admin'
import { TableStore, ModalStore, Modal, PageStore, FormStore } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { message } from 'antd'
import { downFile } from '@/utils'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async () => {
      const data = await http.post('/file/template/type/list')
      return { data }
    },
  })

  list = new TableStore({
    request: async (params) => {
      const { data } = await this.page.getData()
      return await http.post('/file/template/list', {
        ...params,
        templateType: params?.templateType || data.templateTypes?.[0],
      })
    },
  })

  newModal = new ModalStore({
    onFinish: async (values) => {
      const { fileList } = DataUpload.classify(values.file)
      await http.post(
        '/file/template/add',
        { ...values, file: fileList[0] },
        { type: 'upload', timeout: 0 }
      )
      message.success('新增成功')
      this.newModal.close()
      this.list.search()
    },
  })

  replaceModal = new ModalStore({
    onFinish: async (values, initialValues) => {
      const { fileList } = DataUpload.classify(values.file)
      await http.post(
        '/file/template/replace',
        {
          id: initialValues.id,
          file: fileList[0],
        },
        { type: 'upload', timeout: 0 }
      )
      message.success('替换成功')
      this.replaceModal.close()
      this.list.search()
    },
  })

  rollback = (id) => {
    Modal.confirm({
      title: '确认回滚吗？',
      onOk: async () => {
        await http.post('/file/template/history/rollback', { id })
        message.success('回滚成功')
        this.list.search()
      },
    })
  }

  historyModal = new ModalStore({
    onOpen: (initialValues) => {
      this.historyList.search(initialValues)
    },
  })

  historyList = new TableStore({
    pagination: false,
    request: async (params) => {
      return await http.post('/file/template/history/list', params)
    },
  })

  editModal = new ModalStore({})
  form = new FormStore({})

  addType = async () => {
    const name = await this.form.getFieldValue('name')
    if (!name.length) {
      return message.error('请先输入模版文件类型')
    }
    await http.post('/file/template/type/add', { name })
    message.success('新增成功！')
    this.page.init()
    this.form.resetFields()
  }

  remove = (name) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await http.post('/file/template/type/remove', { name })
        message.success('删除成功！')
        this.page.init()
      },
    })
  }

  download = async ({ id, fileId }) => {
    const res = await Api.getFileDownload({ mainId: id, fileId, moduleType: 'FILE_TEMPLATE' })
    await downFile(res)
  }

  handleRoleChange = (e, record) => {
    console.log(e, record)
  }

  // 列表编辑态索引
  editIndex = -1
  editItem = ({ rowIndex }) => {
    this.editIndex = rowIndex
  }
  cancelEdit = () => {
    this.editIndex = -1
  }
  confirmEdit = async ({ record }) => {
    const { values } = await this.list.submit()
    const editData = values[record.id]
    await Api.postFileTemplateUpdate({
      id: record.id,
      faceSignShowFlag: editData?.faceSignShowFlag,
    })
    message.success('更新成功')
    this.editIndex = -1
    this.list.search()
  }
}
export default Store
