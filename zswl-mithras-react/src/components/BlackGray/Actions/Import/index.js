import { observer, http } from '@zswl/admin'
import { Button, Form, Modal, Access } from '@zswl/components'
import { Upload, message } from 'antd'
import { useImperativeHandle, forwardRef } from 'react'

/**
 * 表格中导入操作，渲染一个导入按钮
 * @param store 表格的store，必传
 * @param api 导出的api
 * @param template 下载模版
 * @param templateName 模版名称
 * @param templateAccess  下载模版的权限标识
 * @param access 按钮权限标识
 * @param title Modal的标题
 */
function Index(
  {
    store,
    api,
    title = '批量新增',
    template,
    templateName,
    templateAccess,
    access,
    buttonName = '导入',
    accept = '*',
    needMessage = true,
    ...rest
  },
  ref
) {
  const modalStore = Modal.useStore(
    {
      onFinish: async (values) => {
        const { originFileObj: file } = values.file[0]

        if (typeof api === 'function') {
          await api({ file })
        } else if (typeof api === 'string') {
          await http.post(api, { file }, { type: 'upload' })
        }
        needMessage && message.success(`${buttonName}成功`)
        modalStore.close()
        if (store) {
          store.search()
        }
      },
    },
    [api]
  )
  const downloadTemplate = () => {
    if (typeof template === 'function') {
      return template()
    }
    if (typeof template === 'string') {
      return http.get(template, { type: 'download', fileName: templateName })
    }
  }
  useImperativeHandle(ref, () => {
    return {
      open: modalStore.open,
    }
  })
  return (
    <>
      <Modal title={title} store={modalStore} destroyOnClose>
        <Form>
          <Form.Item
            valuePropName={'fileList'}
            name={'file'}
            getValueFromEvent={(e) => {
              if (Array.isArray(e)) {
                return e
              }
              return e?.fileList
            }}
            rules={[{ required: true, message: '请上传文件' }]}
            label={'导入说明：1.在模版中填写好文件;2.上传填写好的文件'}
          >
            <Upload beforeUpload={() => false} accept={accept}>
              <Button.Upload>点击上传</Button.Upload>
            </Upload>
          </Form.Item>
          <Form.Item hidden={!Access.validate(templateAccess)}>
            <div>
              <Button type={'link'} onClick={downloadTemplate}>
                点此
              </Button>
              下载示例模版
            </div>
          </Form.Item>
        </Form>
      </Modal>
      <Button.Upload onClick={modalStore.open} access={access} {...rest}>
        {buttonName}
      </Button.Upload>
    </>
  )
}

export default observer(forwardRef(Index))
