import { Table, Page, SearchBar } from '@zswl/components'
import styles from './index.less'
import store from './store'
import { observer, history } from '@zswl/admin'
import { ClientSelect } from '@/components/Select'
import { Tooltip } from 'antd'
import CheckLetter from './CheckLetter'
import { saveServer } from '@/utils'

const { Item } = SearchBar

const PaymentApplication = () => {
  let arr1 = [
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      fixed: 'left',
      key: 'contractCode',
      width: 290,
      render: (v, t) => {
        return (
          <a onClick={() => history.push(`/cpm/contractCpm/detail/${t.id}`)}>
            <Tooltip title={t.contractCode}>{t.contractCode}</Tooltip>
          </a>
        )
      },
    },
    {
      title: '基本信息一',
      fixed: 'left',
      children: [
        {
          title: '项目名称',
          dataIndex: 'projName',
          key: 'projName',
          width: 240,
          render: (v) => (
            <Tooltip title={v} placement="topLeft">
              <div className={styles.projName}>{v}</div>
            </Tooltip>
          ),
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          key: 'clientName',
          width: 260,
          render: (v) => (
            <Tooltip title={v}>
              {' '}
              <div className={styles.projName}>{v}</div>
            </Tooltip>
          ),
        },
        {
          title: '合同状态',
          dataIndex: 'contractStatus',
          key: 'contractStatus',
          width: 160,
          render: (v) => <Tooltip title={v}>{v}</Tooltip>,
        },
      ],
    },
    //
  ]
  const arr2 = [
    {
      title: '基本信息二',
      key: 'jbxxe',
      children: [
        {
          title: '类别',
          dataIndex: 'bizType',
          key: 'bizType',
          width: 160,
          render: (v) => <Tooltip title={v}>{v}</Tooltip>,
        },
        {
          title: '项目主办',
          dataIndex: 'projSponsorUser',
          key: 'projSponsorUser',
          width: 160,
          render: (v) => <Tooltip title={v}>{v}</Tooltip>,
        },
        {
          title: '业务部门',
          dataIndex: 'bizDept',
          key: 'bizDept',
          width: 160,
          render: (v) => <Tooltip title={v}>{v}</Tooltip>,
        },
      ],
    },
    {
      title: '已收租金',
      key: 'yszj',
      children: [
        {
          title: '已收本金(元)',
          dataIndex: 'receivedPrincipal',
          key: 'receivedPrincipal',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
        {
          title: '已收利息(元)',
          dataIndex: 'receivedInterest',
          key: 'receivedInterest',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
        {
          title: '合计(元)',
          dataIndex: 'receivedSum',
          key: 'receivedSum',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
      ],
    },
    {
      title: '剩余金额',
      key: 'syje',
      children: [
        {
          title: '剩余本金(元)',
          dataIndex: 'lastPrincipal',
          key: 'lastPrincipal',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
        {
          title: '剩余利息(元)',
          dataIndex: 'lastInterest',
          key: 'lastInterest',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
        {
          title: '名义价款(元)',
          dataIndex: 'nominalLoanPrice',
          key: 'nominalLoanPrice',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
        {
          title: '合计(元)',
          dataIndex: 'lastSum',
          key: 'lastSum',
          width: 160,
          align: 'right',
          render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
        },
      ],
    },
  ]

  return (
    <Page>
      <div className={styles.contractCpm}>
        <Table
          columnsFilter="收付款管理_合同收付款"
          onFilter={(key,val) => saveServer('收付款管理_合同收付款',val)}
          bordered
          store={store.table}
          actions={[
            {
              name: '对账函',
              type: 'primary',
              access: 'collectionletterlist',
              onClick: store.$checkLetter.open,
            },
            {
              name: '租金支付通知书',
              type: 'primary',
              access: 'collectionletterlist',
              onClick: store.pushRentNotify,
            },
            {
              name: '导出',
              onClick: store.exportList,
              type: 'primary',
            },
          ]}
          extra={[]}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              <Item label="客户名称" name="clientId" key="clientId">
                <ClientSelect canJump={false} functionCode="clientlist-5"></ClientSelect>
              </Item>,
              {
                label: '项目名称',
                name: 'projName',
              },
              {
                label: '合同状态',
                name: 'contractStatus',
                options: 'contractStatusCP',
              },
              {
                label: '合同编号',
                name: 'contractCode',
              },
            ],
          }}
          scroll={{
            x: 1200,
          }}
          columns={[...arr1, ...arr2]}
        />
      </div>
      <CheckLetter></CheckLetter>
    </Page>
  )
}

export default observer(PaymentApplication)
