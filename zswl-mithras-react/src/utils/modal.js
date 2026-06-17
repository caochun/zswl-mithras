import { Modal } from '@zswl/components'

export const validateModal = (modalProps, needConfirm = true) => {
  const { title = '提示', content, onOk, onCancel, ...rest } = modalProps
  return new Promise(async (resolve, reject) => {
    if (needConfirm) {
      Modal.confirm({
        title,
        content,
        onOk: async () => {
          const res = await onOk?.()
          resolve(res)
        },
        onCancel: async () => {
          await onCancel?.()
          reject()
        },
        ...rest,
      })
    } else {
      resolve()
    }
  })
}
