import { makeAutoObservable } from '@zswl/admin'
import { message, Modal } from 'antd'
import { TableStore } from '@zswl/components'
import { downFile } from '@/utils'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/afterLease/checkPlanReportApi'

const businessType = 'NEW_AFTER_LEASE_CHECK_PLAN'
const materialsType = 'CHECK_PLAN_SUMMARY_REPORT'

class Store {
  constructor({ planId }) {
    this.planId = planId
    makeAutoObservable(this)
  }

  $table = new TableStore({
    request: ({ id }) => {
      return Api.getReportList({ id })
    },
  })

  preview = async (id, editType) => {
    // editType 1 预览 ，2 编辑
    window.open(`/preview/reportPreview/${id}?editType=${editType}`)
  }

  upload = async (files) => {
    const { fileList } = DataUpload.classify(files)
    await Api.uploadFile({
      file: fileList[0],
      belongId: this.planId,
      materialsType,
      businessType,
    })
    message.success('上传成功')
    this.$table.search({ id: this.planId })
  }

  download = async (ids, filename) => {
    const res = await Api.downFile({ ids, filename })
    downFile(res)
  }

  remove = async (record) => {
    Modal.confirm({
      title: `您将删除文件 '${record.fileName}'，请确认！`,
      onOk: async () => {
        await Api.removeFile({
          ids: [record.fileId],
          businessType,
        })
        message.success('删除成功')
        this.$table.search({ id: this.planId })
      },
    })
  }
}
export default Store
