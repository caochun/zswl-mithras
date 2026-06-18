import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message, Modal } from 'antd'
import Api from '@/api/process/flowFile'
import fileList from '@/api/common/fileList'
import { downFile, toHump } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 上传
  uploadApproval = async (data, params) => {
    await Api.uploadApprovalList(data)
    message.success('上传成功！')
    this.listApprovalTable.search(params)
  }

  listApprovalTable = new TableStore({
    request: async (param) => {
      const data = await Api.getApprovalList(param)
      return data
    },
    pagination: false,
  })
  //删除
  deleteReport = async (record, params) => {
    const name = record.fileName ?? record.name
    const id = record.id
    Modal.confirm({
      title: `您将删除文件 '${name}'，请确认！`,
      onOk: async () => {
        await Api.removeApprovalList({ id, processInstanceId: params.processInstanceId })
        message.success('删除成功')
        this.listApprovalTable.search(params)
      },
    })
  }
  // 下载
  postReportDownload = async (id, params) => {
    const functionCode = `${toHump(params.moduleType)}FileDownload`
    const { businessKey, ...rest } = params
    const res = await fileList.getFileDownload(
      { ...rest, mainId: businessKey, fileId: id },
      functionCode
    )
    downFile(res)
  }
}
export default Store
