import { observer } from '@zswl/admin'
import styles from './index.less'
import { Table, App, Select } from '@zswl/components'
import { Radio, Tooltip } from 'antd'
import { MatchOptionColumn, InputColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const ContractSignDetailUnSigned = ({ store }) => {
  const { signingWayEnum } = App.getData().optionsType
  return (
    <div className={styles.fileWrap}>
      <div className={styles.title}>合同相关材料</div>
      <div className={styles.header}>
        <div style={{ display: 'flex' }}>
          <div className={'z-sub-title'} style={{ marginRight: 20 }}>
            待签约
          </div>
          选择默认的签约方式：
          <Radio.Group onChange={store.changeAllSignWay} value={store.signedValue}>
            {signingWayEnum?.map((item) => {
              return <Radio value={item.value}>{item.label}</Radio>
            })}
          </Radio.Group>
        </div>
      </div>
      <Table
        columnsFilter={'sign_detail_UnSigned_1'}
        onFilter={(key, val) => saveServer('sign_detail_UnSigned_1', val)}
        className={styles.myTable}
        scroll={{ x: false }}
        // scroll={{
        //   y: 800,
        //   x: false,
        // }}
        pagination={false}
        resizable
        extra={[
          {
            name: '批量用印',
            type: 'primary',
            onClick: store.onBatchSign,
          },
          {
            name: '批量下载',
            onClick: store.onBatchDownload,
          },
        ]}
        store={store.unSignedTable}
        selectable={{
          getCheckboxProps: (record) => {
            return {
              disabled: false,
            }
          },
        }}
        columns={[
          InputColumn({
            title: '资料名称',
            dataIndex: 'materialName',
            render: (value, record) => {
              return (
                <a onClick={() => store.previewFile(record)}>
                  <Tooltip title={value}>{value}</Tooltip>
                </a>
              )
            },
          }),
          MatchOptionColumn({
            title: '文本签约方式',
            dataIndex: 'textSigningWay',
            matchOption: 'signingWayEnum',
            width: 160,
            editable: (record) => {
              return {
                element: (
                  <Select
                    options="signingWayEnum"
                    onChange={(value) => {
                      store.changeSignWay({
                        id: record.id,
                        textSigningWay: value,
                      })
                    }}
                  ></Select>
                ),
              }
            },
            allowClear: false,
          }),
          MatchOptionColumn({
            title: '文本签约状态',
            dataIndex: 'textSigningStatus',
            matchOption: 'contractTextStatusEnum',
            width: 140,
          }),
          InputColumn({
            title: '推送时间',
            dataIndex: 'pushTime',
            width: 180,
          }),
          {
            title: '操作',
            width: 100,
            actions: (record) => {
              return [
                {
                  name: '下载',
                  onClick: () => store.downloadFile(record),
                },
              ]
            },
          },
        ]}
        expandable={{
          expandedRowRender: (record) => {
            const { signerList } = record
            // const childTable = new TableStore({
            //   pagination: false,
            //   request: async (params) => {
            //     return signerList ?? []
            //   },
            // })
            return (
              <div>
                <Table
                  columnsFilter={'sign_detail_UnSigned_2'}
                  onFilter={(key, val) => saveServer('sign_detail_UnSigned_2', val)}
                  // store={childTable}
                  pagination={false}
                  dataSource={signerList}
                  editable={false}
                  scroll={{ x: 1200 }}
                  columns={[
                    InputColumn({
                      title: '签约方名称',
                      dataIndex: 'signerName',
                    }),
                    MatchOptionColumn({
                      title: '签约方式',
                      dataIndex: 'signingWay',
                      matchOption: 'signingWayEnum',
                    }),
                    MatchOptionColumn({
                      title: '签约状态',
                      dataIndex: 'signingStatus',
                      matchOption: 'contractTextStatusEnum',
                    }),
                    InputColumn({
                      title: '签约完成时间',
                      dataIndex: 'signingCompleteTime',
                    }),
                    // MatchOptionColumn({
                    //   title: '实名认证状态',
                    //   dataIndex: 'realNameAuthStatus',
                    //   matchOption: 'realNameAuthStatus',
                    // }),
                    // {
                    //   title: '操作',
                    //   fixed: 'right',
                    //   width: 220,
                    //   actions(childrenRecord) {
                    //     return [
                    //       {
                    //         name: '发起实名认证',
                    //         key: 'view',
                    //         onClick: () => {},
                    //       },
                    //       {
                    //         name: '发起合同签约',
                    //         key: 'edit',
                    //         onClick: () => {},
                    //       },
                    //     ]
                    //   },
                    // },
                  ]}
                />
              </div>
            )
          },
        }}
      ></Table>
    </div>
  )
}

export default observer(ContractSignDetailUnSigned)
