import { getInputNumberValueFromEvent } from '@/utils'
import { observer } from '@zswl/admin'
import { App, Button, Form, Modal, Page, Select, Table } from '@zswl/components'
import { DatePicker, Input, Space, Upload } from 'antd'
import store from './store'
import styles from './index.less'
import { useEffect, useMemo, useState } from 'react'
import { DownloadTemplateAction as DownloadTemplate } from '@/components/Actions'
import { saveServer } from '@/utils'

const LPR = () => {
  const [fileList, setFileList] = useState([])

  const uploadProps = {
    name: 'file',
    beforeUpload: (file) => {
      setFileList([...fileList, file])
      return false
    },
    accept: '.xlsx',
    fileList: [],
  }

  useEffect(() => {
    if (fileList.length === 0) {
      return
    }

    const formData = new FormData()
    formData.append('file', fileList[0])
    store.upload(formData, () => {
      setFileList([])
    })
    return () => {
      setFileList([])
    }
  }, [fileList])
  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: 'LPR报价日',
        width: 120,
        dataIndex: 'lprDate',
        render: (val) => val,
      },
      {
        title: '1年期LPR报价(%)',
        width: 120,
        dataIndex: 'oneYear',
      },
      {
        title: '5年期LPR报价(%)',
        width: 180,
        dataIndex: 'fiveYear',
      },
      {
        title: '操作',
        width: 100,
        fixed: 'right',
        dataIndex: 'contractCode',
        render: (_, val) => {
          return (
            <div className={styles.control}>
              <div className={styles.item} onClick={() => store.itemEdit(val)}>
                编辑
              </div>
              <div className={styles.item} onClick={() => store.itemDelete(val)}>
                删除
              </div>
            </div>
          )
        },
      },
    ]

    return baseColumns
  }, [])
  return (
    <Page>
      <div className={styles.btnWrap}>
        <Space>
          <DownloadTemplate
            params={{
              templateName: 'TEMPLATE_OSS_BASE_DATA_LPR',
              functionCode: 'baseDateLprFileDownloadTemplate',
            }}
          />

          <Upload {...uploadProps}>
            <Button style={{ marginRight: 8 }}>数据导入</Button>
          </Upload>
          <Button type="primary" onClick={() => store.editModal.open()}>
            新增
          </Button>
        </Space>
      </div>
      <Table
        onFilter={(key, val) => saveServer('budget_lpr_1', val)}
        columnsFilter="budget_lpr_1"
        store={store.list}
        columns={columns}
      />

      <EditModal />
    </Page>
  )
}
const EditModal = () => {
  const [form] = Form.useForm()
  const layout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 17 },
  }
  return (
    <Modal
      store={store.editModal}
      propsBy={(data) => {
        return {
          title: (!data ? '新增' : data?.noEdit ? '' : '编辑') + 'lpr',
          footer: data?.noEdit ? <></> : undefined,
        }
      }}
      destroyOnClose
    >
      <Form form={form} {...layout}>
        <Form.Item
          label="LPR报价日"
          name="lprDate"
          rules={[
            {
              required: true,
              message: '请选择LPR报价日！',
            },
          ]}
        >
          <DatePicker style={{ width: '100%' }} placeholder="请选择LPR报价日！" />
        </Form.Item>
        <Form.Item
          getValueFromEvent={getInputNumberValueFromEvent}
          label="1年期LPR报价"
          name="oneYear"
          rules={[
            {
              required: true,
              message: '请输入1年期LPR报价！',
            },
          ]}
        >
          <Input placeholder="请输入1年期LPR报价！" suffix="%" />
        </Form.Item>
        <Form.Item
          getValueFromEvent={getInputNumberValueFromEvent}
          label="5年期LPR报价"
          name="fiveYear"
          rules={[
            {
              required: true,
              message: '请输入5年期LPR报价！',
            },
          ]}
        >
          <Input placeholder="请输入5年期LPR报价！" suffix="%" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
export default observer(LPR)
