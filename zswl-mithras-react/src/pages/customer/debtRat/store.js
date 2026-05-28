import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import debtRatApi from '@/api/customer/customerRat/debtRatApi'
import { message } from 'antd'

class Store {
  constructor({ afterClose }) {
    this.afterClose = afterClose
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {},
  })
  afterClose = () => {}
  table = new TableStore({
    request: (params) => {
      const { projReviewId } = this.page.getParams()
      return debtRatApi.postAmountPage({ projReviewId, ...params })
    },
  })
  delete = async (id) => {
    await debtRatApi.postAmountDelete({ id })
    message.success('删除成功')
    this.table.search()
  }
  add = () => {
    this.createModal.open()
  }
  edit = (record) => {
    this.createModal.open({ editType: 'edit', ...record })
  }
  createModal = new ModalStore({
    onOpen: async (record = {}) => {
      const { projReviewId } = this.page.getParams()
      const { evaluationSubjectList, evaluationSubjectId, evaluationSubjectName, ...rest } =
        await debtRatApi.postAmountLesseeInfo({
          projReviewId,
        })
      this.lesseeOption = evaluationSubjectList
      return {
        ...record,
        name: record.modelName,
        code: record.modelCode,
        ...rest,
        evaluationSubject: {
          value: evaluationSubjectId,
          label: evaluationSubjectName,
        },
      }
    },
    onFinish: async (params) => {
      const { projReviewId } = this.page.getParams()
      const initial = this.createModal.getInitialValues()
      const isEdit = initial?.editType === 'edit'

      const { evaluationSubject, ...rest } = params
      const newParams = {
        ...rest,
        evaluationSubjectId: evaluationSubject.value,
        evaluationSubjectName: evaluationSubject.label,
        projReviewId,
      }
      if (isEdit) {
        await debtRatApi.postAmountUpdate({
          id: initial?.id,
          ...newParams,
        })
        message.success('更新成功')
        this.createModal.close()
        this.table.search()
        return
      }
      const { exist } = await debtRatApi.postAmountAccessCheck(newParams)
      const addNew = async () => {
        const { id } = await debtRatApi.postAmountAdd(newParams)
        message.success('新增成功')
        this.table.search()
        this.createModal.close()
        history.push(`/customer/debtRat/detail/${id}`)
        this.afterClose?.()
      }

      if (exist) {
        Modal.confirm({
          title: '提示',
          content: `当前客户存在正在生效的评级，是否对当前评级进行更新？`,
          okText: '更新',
          onOk: async () => {
            await addNew()
          },
          cancelText: '不更新',
          onCancel: () => {
            this.createModal.close()
          },
        })
      } else {
        await addNew()
      }
    },
  })
  lesseeOption = []
}
export default Store
