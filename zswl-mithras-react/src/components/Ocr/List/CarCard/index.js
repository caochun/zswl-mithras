import { Button, Page, Table } from '@zswl/components'
import { getQuery, history, observer } from '@zswl/admin'
import store from './store'
import { getTableColumns } from '@/utils'
import styles from '../styles.less'
import ALL_COLUMNS from '../Column'
import { useMemo } from 'react'
import classNames from 'classnames'
import InvoiceBatchModal from './BatchModal'
import IconFont from '@/components/Icon'
import { Card, InputNumber, Space, Tag, Tooltip } from 'antd'
import CompareModal from './CompareModal'
import FileUploadModal from '../../FileUploadModal'
import { QuestionCircleOutlined } from '@ant-design/icons'
import { saveServer } from '@/utils'

function Index({ path }) {
  const { id } = getQuery()
  const { keys, rows } = store.table.getSelected()
  const canEdit = store.table.selectedRowKeys.length > 0
  const canDelete = rows.length > 0 && rows.every((item) => !item.locked)

  const { vatInvoiceCount } = store
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '文件名',
        rename: '车辆登记证编号',
        actions: (record) => [
          {
            name: (
              <div>
                <span style={{ color: record.registrationPageNo ? undefined : 'red' }}>
                  {record?.registrationPageNo ||
                    record?.fileName ||
                    record?.changeRecordRspList?.[0].fileName}
                </span>
                {record.locked && <Tag style={{ marginLeft: 8 }}>已锁定</Tag>}
              </div>
            ),
            onClick: () => store.compareModal.open({ selected: [record] }),
          },
        ],
        search: true,
      },
      {
        title: '图片张数',
        search: {
          element: <InputNumber />,
        },
      },
      '车牌号',
      '机动车所有人',
      { title: '车架号', dataIndex: 'vehicleVin', search: true },
      '制造商',
      '原始文件名',
    ]

    return [
      ...getTableColumns(ALL_COLUMNS, nameColumns, true),
      {
        title: '变更记录',
        dataIndex: 'changeRecordRspList',
        width: 100,
        render: (val) => {
          const flatList = []
          val?.forEach((v) => {
            flatList.push(...(v?.changeRecordList ?? []))
          })
          const title = flatList
            .sort((a, b) => new Date(a.changeDate) - new Date(b.changeDate))
            .map((item, i) => (
              <span>
                {i !== 0 ? ',' : undefined}
                <span style={{ color: !item.changeDate ? 'red' : undefined }}>{item.name}</span>
              </span>
            ))

          return (
            <div className="z-single-line" style={{ width: 200 }}>
              <Tooltip key={title} title={title}>
                <span>{title}</span>
              </Tooltip>
            </div>
          )
        },
      },
      {
        title: '操作',
        actions: (item) => {
          if (!vatInvoiceCount.canLock && item.locked) {
            return [<div style={{ color: '#ffb572' }}>已锁定，不支持修改</div>]
          }
          return [
            // item.id && {
            //   name: '重新上传',
            //   onClick: () =>
            //     store.fileModal.open({
            //       uploadType: 'reRowUpload',
            //       rowId: item.id,
            //       id,
            //     }),
            // },

            // item.id && {
            //   name: '编辑',
            //   onClick: () =>
            //     store.batchModal.open({ modalType: 'single', vehicleIds: [item.id], ...item }),
            // },
            { name: '删除', onClick: () => store.delete(item), confirm: true },
            item.locked && { name: '解除锁定', onClick: () => store.lock(true, [item.id]) },
          ]
        },
      },
    ]
  }, [vatInvoiceCount])
  const cardList = [
    { title: '本次合计识别(张)', icon: 'icon-hejishibie', dataIndex: 'total' },
    {
      title: '识别成功(张)',
      icon: 'icon-shibiechenggong1',
      dataIndex: 'succeed',
      option: 'success',
    },
    { title: '识别失败(张)', icon: 'icon-shibieshibai1', dataIndex: 'fail', option: 'failure' },
  ]
  return (
    <Page params={{ id }} store={store.page} noStyle>
      <Card style={{ marginBottom: 12 }}>
        <Space className={styles.processTypeList}>
          {cardList.map(({ dataIndex, icon, title, option }, index) => {
            return (
              <div
                key={index}
                className={classNames(styles.item, {
                  [styles.selected]: option === store.selectedType,
                })}
                onClick={() => {
                  store.setSelectedType(option)
                }}
              >
                <IconFont type={icon} className={styles.icon} />
                <div className={styles.desc}>
                  <div className={styles.text}>{title}</div>
                  <div className={styles.num}>{vatInvoiceCount[dataIndex] ?? 0}</div>
                </div>
              </div>
            )
          })}
          <div>
            <div>
              <Button.Upload
                type="primary"
                onClick={() => store.fileModal.open({ uploadType: 'reUpload', id })}
              >
                替换车证
              </Button.Upload>
              <Tooltip title="替换车证：全部替换为新上传的车证">
                <QuestionCircleOutlined style={{ marginLeft: 12 }} />
              </Tooltip>
            </div>
            <div>
              <Button.Upload
                type="primary"
                style={{ marginTop: 12 }}
                onClick={() => store.fileModal.open({ uploadType: 'upload', id })}
              >
                追加车证
              </Button.Upload>
              <Tooltip title="追加车证:只需上传新增的车证，系统会把新上传的车证数据增添到原数据后">
                <QuestionCircleOutlined style={{ marginLeft: 12 }} />
              </Tooltip>
            </div>
          </div>
        </Space>
      </Card>

      <Table
        columnsFilter={'list_CarCard_1'}
        onFilter={(key, val) => saveServer('list_CarCard_1', val)}
        store={store.table}
        serial
        editable={false}
        selectable
        rowKey={'id'}
        actions={[
          <Button.Download onClick={store.batchDownload} key="add">
            导出车证列表
          </Button.Download>,
          <Button disabled={!canEdit} onClick={() => store.compareModal.open({ selected: rows })}>
            预览
          </Button>,
          <Button.Delete onClick={() => store.delete()} key="delete" disabled={!canDelete} confirm>
            删除
          </Button.Delete>,
          vatInvoiceCount.canLock && (
            <Button.Delete onClick={() => store.batchLock()} key="lock" disabled={!canEdit}>
              锁定
            </Button.Delete>
          ),
          <Button.Edit
            key="batch"
            onClick={() =>
              store.batchModal.open({ modalType: 'batch', vehicleIds: rows.map((item) => item.id) })
            }
            disabled={!canDelete}
          >
            批量修改
          </Button.Edit>,
        ]}
        scroll={{
          x: 'auto',
        }}
        columns={columns}
      />
      <InvoiceBatchModal store={store.batchModal} />
      <CompareModal store={store} type="carCard" mainId={id} />
      <FileUploadModal store={store.fileModal} type="carCard" tableStore={store.table} />
    </Page>
  )
}

export default observer(Index)
