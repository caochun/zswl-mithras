import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import DataUpload from '@/components/DataUpload'
import { requestQueue, toHump } from '@/utils'

const uploadFile = async (files, uploadParams) => {
  const { fileList } = DataUpload.classify(files)
  const functionCode = `${toHump(uploadParams.moduleType)}FileUpload`
  const { version, businessVersion, ...restParams } = uploadParams

  const maxConcurrentRequests = 4
  const requestManager = requestQueue(maxConcurrentRequests)
  if (fileList.length === 0) return Promise.resolve()
  for (let i = 0; i < fileList.length; i++) {
    requestManager.enqueue(
      async () =>
        await customerRatApi.postClientSupplementFileUpload(
          { materialsType: 'DEFAULT', file: fileList[i], ...restParams },
          functionCode
        )
    )
  }
  return await requestManager.queueEmptyPromise
    .then((succeed) => {
      return { fileList, succeed }
    })
    .catch((error) => {
      console.error('队列执行出错:', error)
    })
}

export default uploadFile
