import { useMemo } from 'react'
import { Button, Page, Descriptions, Form, Upload } from '@zswl/components'
import Store from './store'
import { observer } from '@zswl/admin'
import { BlackGrayColumns as ALL_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import { getDescColumns, getTableColumns } from '@/utils'
import { ApprovalOperation } from '@/components/BlackGray/BlackGrayEntries'
import styles from './styles.less'
import MainTable from './MainTable'
import SubmitRadio from './SubmitRadio'
import { Card } from 'antd'
import SingleModal from './SingleModal'
import DataUpload from '@/components/DataUpload'
import { NoEnumFileTable } from '@/components/Table'

const MODEL_KEY = 'BLACK_GRAY_WAREHOUSE_TASK'
function Id({ params, query, path, props: { sub } }) {
  const store = useMemo(() => new Store(), [])
  const detail = store.page.getData()
  const { type, view } = query
  const { id } = params

  const columns = useMemo(() => {
    const nameColumns = [
      '任务编号',
      '数据时点',
      '所属机构',
      '创建时间',
      '更新时间',
      type !== 'add' && '审批状态',
    ].filter(Boolean)
    return getDescColumns(ALL_COLUMNS, nameColumns)
  }, [id, type, detail])
  const isEdit = ['add', 'edit'].includes(type)
  const isApproval = ['approval'].includes(type)
  const tableData = store.table.getList()
  return (
    <Page
      current="报送"
      params={{ sub, ...params }}
      store={store.page}
      header={{
        extra: [
          ['edit', 'approval'].includes(type) && (
            <Button.Submit
              type="primary"
              onClick={() => store.submit({})}
              disabled={tableData.length === 0 || isApproval}
              key="submit"
            />
          ),
          isEdit && (
            <Button type="primary" onClick={() => store.save()} key="save">
              保存
            </Button>
          ),
        ],
      }}
    >
      <Descriptions
        dataSource={detail}
        items={columns}
        bordered
        labelStyle={{ width: '160px' }}
        contentStyle={{ width: 230 }}
      />
      <Form store={store.form} initialValues={detail}>
        {isEdit && (
          <div className={styles.fileDiv}>
            <Form.Item
              name={'uploadFile'}
              label="黑灰名单信息报送"
              className={styles.uploadFile}
              rules={[{ required: true, message: '请上传信息报送excel' }]}
              transform={(values) => values?.length && Upload.transform(values)}
              accept=".xlsx,.xls"
            >
              <SubmitRadio downloadApi={store.download} onChange={store.checkFile} store={store} />
            </Form.Item>
          </div>
        )}
      </Form>

      {/* 主任务列表 */}
      <MainTable store={store} path={path} className={styles.detailTable} />

      {['approval'].includes(type) && (
        <Card title="操作意见" size={'small'} className={styles.detailCard}>
          <Form store={store.approvalForm} cache={false}>
            <ApprovalOperation operation form={store.approvalForm} />
          </Form>
        </Card>
      )}
      <NoEnumFileTable
        title={'附件'}
        canEdit={isEdit}
        params={{
          mainId: id,
          moduleType: 'BLACK_GRAY',
          businessType: 'BLACK_GRAY_WAREHOUSE',
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
      <SingleModal modal={store.singleModalStore} />
    </Page>
  )
}

export default observer(Id)
