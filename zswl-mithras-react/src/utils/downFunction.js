import { message } from 'antd'

export async function downFile(res, needMessage = false) {
  if (typeof res === 'string') {
    window.location.href = res
  }
  if (typeof res === 'object' && !(res instanceof Blob)) {
    const { fileUrl, data } = res
    window.location.href = fileUrl || data
  }
}
