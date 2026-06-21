import { useMemo, useState } from 'react'
import { EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { List, Input, Tooltip } from 'antd'
import { Page, Table, Modal, Form, Select, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import {
  InputNumberEditable,
  AmountFormat,
  InputColumn,
  AmountColumn,
  MatchOptionColumn,
} from '@/components/Format'
import Store from './store'
import { rules } from '@/utils'
import styles from './index.less'
import { saveServer } from '@/utils'

const EditSelect = (props) => {
  const { data, store, ...rest } = props
  return (
    <div className={styles.fileType}>
      <Select {...rest} options={data} defaultValue={data?.[0]} />
      <Tooltip title="类型管理">
        <EditOutlined className={styles.editIcon} onClick={store.editModal.open} />
      </Tooltip>
    </div>
  )
}

function BaseDataFileTemplate() {
  const store = useMemo(() => new Store(), [])
  const { editIndex } = store
  const [templateName, setTemplateName] = useState(null)
  const { data } = store.page.getData()
  const _data = data?.templateTypes.map((item) => {
    return { label: item, value: item }
  })
  const { templateType } = store.list.getParams()
  const isContractTemplate = templateType?.startsWith('合同')

  const columns = [
    InputColumn({
      title: '模版文件类型',
      dataIndex: 'templateType',
      search: {
        element: <EditSelect store={store} data={_data} />,
      },
    }),
    InputColumn({
      title: '模版文件名称',
      dataIndex: 'filename',
      search: true,
      width: 460,
    }),
    MatchOptionColumn({
      title: '合同面签是否需要展示',
      dataIndex: 'faceSignShowFlag',
      matchOption: 'yesOrNo',
      editable: (record, rowIndex) => {
        return editIndex === rowIndex
          ? {
              rules: [rules.required('请选择')],
              element: <Select options="yesOrNo"></Select>,
            }
          : false
      },
    }),
    InputColumn({
      title: '更新人',
      dataIndex: 'createByName',
    }),
    InputColumn({
      title: '更新时间',
      dataIndex: 'createTime',
    }),
    // {
    //   title: '签约人角色',
    //   dataIndex: 'roles',
    //   editable: (record) => {
    //     return {
    //       element: (
    //         <Select
    //           options={'LPRTypeEnum'}
    //           mode="multiple"
    //           onChange={(e) => store.handleRoleChange(e, record)}
    //         ></Select>
    //       ),
    //       rules: [rules.required('请选择')],
    //     }
    //   },
    // },
    // {
    //   title: '签约位置',
    //   dataIndex: 'position',
    // },
  ]
  return (
    <Page access="filetemplatetypelist" store={store.page}>
      <Table
        columnsFilter={'baseData_template_1'}
                onFilter={(key,val) => saveServer('baseData_template_1',val)}
        
        editable={editIndex != -1}
        scroll={{ x: true }}
        access="filetemplatelist"
        store={store.list}
        actions={[
          {
            name: '新增文件',
            type: 'primary',
            onClick: store.newModal.open,
            access: 'filetemplatetypeadd',
          },
        ]}
        columns={[
          ...columns,
          {
            title: '操作',
            tooltip: false,
            width: 180,
            actions(record, rowIndex) {
              const { id, fileId, filename } = record
              return [
                editIndex !== rowIndex && {
                  name: '编辑',
                  key: 'edit',
                  onClick: () => store.editItem({ rowIndex }),
                },
                editIndex === rowIndex && {
                  name: '取消',
                  key: 'cancel',
                  onClick: () => store.cancelEdit({ rowIndex }),
                },
                editIndex === rowIndex && {
                  name: '确定',
                  key: 'confirm',
                  onClick: () => store.confirmEdit({ record, rowIndex }),
                },
                {
                  name: '下载',
                  onClick: store.download,
                },
                {
                  name: '预览',
                  onClick() {
                    window.open(`/preview/reportPreview/${fileId}`)
                  },
                },
                {
                  name: '替换',
                  onClick() {
                    store.replaceModal.open({ id })
                    setTemplateName(filename)
                  },
                },
                {
                  name: '历史',
                  onClick() {
                    store.historyModal.open({ id })
                  },
                },
              ]
            },
          },
        ]}
      />
      <Modal title="新增模版文件" store={store.newModal} destroyOnClose width={400}>
        <Form>
          <Form.Item
            label="模版文件"
            name="file"
            rules={[{ required: true, message: '请上传文件' }]}
          >
            <DataUpload accept="*" maxCount={1} />
          </Form.Item>
          <Form.Item
            label="模版文件类型"
            name="templateType"
            rules={[{ required: true, message: '请输入模版文件类型' }]}
          >
            <Select placeholder="请选择" options={_data} />
          </Form.Item>
        </Form>
      </Modal>
      <Modal title={`${templateName}替换`} store={store.replaceModal} destroyOnClose width={600}>
        <Form>
          <Form.Item
            label="模版文件"
            name="file"
            rules={[{ required: true, message: '请上传文件' }]}
          >
            <DataUpload accept="*" maxCount={1} />
          </Form.Item>
        </Form>
      </Modal>
      <Modal title="历史" store={store.historyModal} destroyOnClose width={700} footer={null}>
        <Table
          columnsFilter={'baseData_template_2'}
          onFilter={(key,val) => saveServer('baseData_template_2',val)}

          store={store.historyList}
          autoRequest={false}
          scroll={{ x: null }}
          columns={[
            {
              title: '创建时间',
              dataIndex: 'createTime',
            },
            {
              title: '创建人',
              dataIndex: 'createByName',
            },
            {
              title: '操作',
              tooltip: false,
              actions({ id, fileId }) {
                return [
                  {
                    name: '下载',
                    onClick: store.download,
                  },
                  {
                    name: '预览',
                    onClick() {
                      window.open(`/preview/reportPreview/${fileId}`)
                    },
                  },
                  {
                    name: '回滚',
                    onClick() {
                      store.rollback(id)
                    },
                  },
                ]
              },
            },
          ]}
        />
      </Modal>
      <Modal store={store.editModal} title="模版文件类型" footer={null}>
        <div className={styles.newTemplateType}>
          <Form store={store.form}>
            <Form.Item label="模版文件类型" name="name">
              <Input placeholder="请输入..." />
            </Form.Item>
          </Form>
          <Button type="primary" onClick={store.addType}>
            新增
          </Button>
        </div>
        <List bordered itemLayout="horizontal">
          {data?.templateTypes.map((item) => (
            <List.Item key={item}>
              <div className={styles.editModalContent}>
                <span>{item}</span>
                <DeleteOutlined className={styles.deleteIcon} onClick={() => store.remove(item)} />
              </div>
            </List.Item>
          ))}
        </List>
      </Modal>
    </Page>
  )
}

export default observer(BaseDataFileTemplate)
