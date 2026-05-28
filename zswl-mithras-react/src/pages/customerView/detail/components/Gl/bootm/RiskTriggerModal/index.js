import { observer } from '@zswl/admin'
import { Table, Button, Page, Modal } from '@zswl/components'
import { QuestionCircleOutlined } from '@ant-design/icons'
import styles from './index.less'
import { Tag, Tooltip } from 'antd'
import { saveServer } from '@/utils'

function Index({ store, isSingle, getRiskScore }) {
  const colorObj = {
    重要风险: 'error',
    次要风险: 'warning',
  }
  const columns = [
    {
      title: '展业机构',
      dataIndex: 'ascriptionOrg',
    },

    {
      title: '风险中类',
      dataIndex: 'middleRisk',
    },
    {
      title: '风险小类',
      dataIndex: 'minorRisk',
    },
    {
      title: '标签等级',
      dataIndex: 'tagLevel',
      width: 120,
      render(val) {
        return <Tag color={colorObj[val]}>{val}</Tag>
      },
    },
    {
      title: '标签名称',
      dataIndex: 'tagName',
      width: 240,
      render(val, record) {
        return (
          <div className={styles.renderValue}>
            <span>{val}</span>
            <Tooltip title={<div dangerouslySetInnerHTML={{ __html: record?.tagDescribe }}></div>}>
              <span className={styles.iconDesc}>
                <QuestionCircleOutlined />
              </span>
            </Tooltip>
          </div>
        )
      },
    },
    {
      title: '触发说明',
      dataIndex: 'triggerDescribe',
      width: 240,
      render(val, record) {
        console.log(val)

        return (
          <div className={styles.renderValue}>
            <Tooltip title={<div dangerouslySetInnerHTML={{ __html: val }}></div>}>
              <span dangerouslySetInnerHTML={{ __html: val?.substring(0, 100) + '...' }}></span>
            </Tooltip>
          </div>
        )
      },
      // render: (val) => {
      //   return (
      //     (val?.length > 20 ? (
      //       <div className={styles.renderValue}>
      //         <Tooltip
      //           color="rgba(12, 38, 77, 0.8)"
      //           overlayInnerStyle={{
      //             border: '1px solid rgba(255,255,255,0.10)',
      //             borderTop: '2px solid #2A73AC',
      //             fontSize: '12px',
      //             whiteSpace: 'nowrap',
      //           }}
      //           title={val}
      //         >
      //           {val?.substring(0, 5) + '...'}
      //         </Tooltip>
      //       </div>
      //     ) : (
      //       val
      //     )) || '/'
      //   )
      // },
    },
    {
      title: '触发时间',
      dataIndex: 'triggerDate',
      width: 200,
    },
    {
      title: '模型名称',
      dataIndex: 'modelName',
      width: 160,
    },
  ]
  return (
    <Modal
      title={
        <div className={styles.header}>
          <span>风险触发详情</span>
          <span className={styles.headerDesc}>数据时点:{store.riskTriggerTableData?.date}</span>
        </div>
      }
      store={store.riskTriggerModal}
      width={1100}
      footer={false}
      destroyOnClose
    >
      <Table
              columnsFilter={'bootm_RiskTriggerModal_1'}
              onFilter={(key,val) => saveServer('bootm_RiskTriggerModal_1',val)}
        serial
        columnWidth={120}
        columns={columns}
        // columns={!isSingle ? columns : [columns[2]]}
        store={store.riskTriggerTable}
      />
    </Modal>
  )
}

export default observer(Index)
