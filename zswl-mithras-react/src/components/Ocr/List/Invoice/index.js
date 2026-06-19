import { Button, Page, Table } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import store from './store'
import { getTableColumns } from '@/utils'
import styles from '../styles.less'
import ALL_COLUMNS from '../Column'
import { useMemo } from 'react'
import classNames from 'classnames'
import InvoiceBatchModal from './BatchModal'
import IconFont from '@/components/Icon'
import { Card, Space, Tag, Tooltip } from 'antd'
import CompareModal from '../CompareModal'
import FileUploadModal from '../../FileUploadModal'
import { QuestionCircleOutlined } from '@ant-design/icons'
import { saveServer } from '@/utils'

const childrenColumns = getTableColumns(ALL_COLUMNS, [
  '内容',
  '型号规格',
  '单位',
  '数量',
  '税额',
  '金额',
  '金额（不含税）',
  '税率',
  '车架号',
])

export const InvoiceTable = ({ dataSource, ...rest }) => {
  return (
    <Table
      columnsFilter={'list_Invoice_1'}
      onFilter={(key, val) => saveServer('list_Invoice_1', val)}
      columns={childrenColumns}
      dataSource={dataSource}
      columnWidth={120}
      pagination={false}
      {...rest}
    ></Table>
  )
}
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
        width: 200,
        actions: (record) => [
          {
            name: (
              <div>
                {record.fileName}
                {record.locked && <Tag style={{ marginLeft: 8 }}>已锁定</Tag>}
              </div>
            ),
            onClick: () => store.compareModal.open({ selected: [record] }),
          },
        ],
      },
      '发票号码',
      '开票日期',
      '购买方',
      '销售方',
      '验真结果',
      '发票状态',
      '是否盖章',
      '备注',
    ]

    return [
      ...getTableColumns(ALL_COLUMNS, nameColumns, true),
      ...childrenColumns,
      {
        title: '操作',
        width: 300,
        actions: (item) => {
          if (item.invoiceId) return null
          if (!vatInvoiceCount.canLock && item.locked) {
            return [<div style={{ color: '#ffb572' }}>已锁定，不支持修改</div>]
          }
          return [
            {
              name: '重新上传',
              onClick: () =>
                store.fileModal.open({ uploadType: 'reRowUpload', rowId: item.id, id }),
            },
            {
              name: '重新验真',
              onClick: () => store.fileModal.open({ uploadType: 'reVerify', rowId: item.id, id }),
            },
            {
              name: '编辑',
              onClick: () =>
                store.batchModal.open({ modalType: 'singe', vatInvoiceIds: [item.id], ...item }),
            },
            { name: '删除', onClick: () => store.delete(item), confirm: true },
            item.locked && { name: '解除锁定', onClick: () => store.lock(item) },
          ]
        },
      },
    ]
  }, [vatInvoiceCount.canLock])
  const cardList = [
    {
      title: '本次合计识别发票(张)',
      icon: 'icon-hejishibie',
      dataIndex: 'total',
    },
    {
      title: '识别成功(张)',
      icon: 'icon-shibiechenggong1',
      dataIndex: 'succeed',
      option: 'IDENTIFY_SUCCESS',
    },
    {
      title: '验真通过(张)',
      icon: 'icon-yanzhentongguo1',
      dataIndex: 'pass',
      option: 'VERIFICATION_PASSED',
    },
    {
      title: '验真不通过(张)',
      icon: 'icon-yanzhenshibai2',
      dataIndex: 'noPass',
      option: 'VERIFICATION_NOT_PASSED',
    },
    {
      title: '识别失败(张)',
      icon: 'icon-shibieshibai1',
      dataIndex: 'fail',
      option: 'IDENTIFY_FAILED',
    },
  ]
  return (
    <Page params={{ id }} store={store.page} noStyle>
      <Card style={{ marginBottom: 12 }}>
        <Space>
          <div>
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
            </Space>
            {store.amountSymbol && (
              <div>
                金额校验：租赁物清单账面原值（元）合计值
                <span style={{ color: 'red' }}>{store.amountSymbol}</span>
                ocr识别发票金额（元）合计值
              </div>
            )}
          </div>
          <div>
            <div>
              <Button.Upload
                type="primary"
                onClick={() => store.fileModal.open({ uploadType: 'reUpload', id })}
              >
                替换发票
              </Button.Upload>
              <Tooltip title="替换发票：全部替换为新上传的发票">
                <QuestionCircleOutlined style={{ marginLeft: 12 }} />
              </Tooltip>
            </div>
            <div>
              <Button.Upload
                type="primary"
                style={{ marginTop: 12 }}
                onClick={() => store.fileModal.open({ id })}
              >
                追加发票
              </Button.Upload>
              <Tooltip title="追加发票:只需上传新增的发票，系统会把新上传的发票数据增添到原数据后">
                <QuestionCircleOutlined style={{ marginLeft: 12 }} />
              </Tooltip>
            </div>
            <div style={{ paddingRight: 26 }}>
              <Button
                type="primary"
                onClick={() => store.amountCheck()}
                style={{ marginTop: 12 }}
                block
              >
                金额校验
              </Button>
            </div>
          </div>
        </Space>
      </Card>

      <Table
        columnsFilter={'list_Invoice_2'}
        onFilter={(key, val) => saveServer('list_Invoice_2', val)}
        store={store.table}
        editable={false}
        selectable
        serial={{
          width: 80,
          render: (d, r, i) => {
            if (r.invoiceId) return null
            // if (r.invoiceId) return <span style={{ textAlign: 'right' }}>{i + 1}</span>
            return i + 1
          },
        }}
        columnWidth={120}
        expandable={{
          childrenColumnName: 'invoiceProductList',
          rowExpandable: (record) => record.invoiceProductList,
          indentSize: 12,
          expandedRowKeys: store.defaultExpandedRowKeys,
          onExpand: store.onExpand,
        }}
        actions={[
          <Button.Download onClick={store.batchDownload} key="add">
            导出发票列表
          </Button.Download>,
          <Button disabled={!canEdit} onClick={() => store.compareModal.open({ selected: rows })}>
            预览
          </Button>,
          <Button.Delete onClick={() => store.delete()} key="delete" disabled={!canDelete} confirm>
            删除
          </Button.Delete>,
          vatInvoiceCount.canLock && (
            <Button disabled={!canEdit} onClick={() => store.batchLock()}>
              锁定
            </Button>
          ),
          <Button.Edit
            key="batch"
            onClick={() => store.batchModal.open({ modalType: 'batch', vatInvoiceIds: keys })}
            disabled={!canDelete}
          >
            批量修改
          </Button.Edit>,
        ]}
        scroll={{
          x: 1500,
        }}
        columns={columns}
      />
      <InvoiceBatchModal store={store.batchModal} />
      <CompareModal store={store.compareModal} mainId={id} />
      <FileUploadModal store={store.fileModal} type={'invoice'} tableStore={store.table} />
    </Page>
  )
}

export default observer(Index)
