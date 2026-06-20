import { useEffect, useState } from 'react'
import { Table, Form, Input, Button, Space, Page, App, Select } from '@zswl/components'
import styles from './style.less'
import { AmountColumn, DateColumn, FounderColumn, MatchOptionColumn } from '@/components/Format'
import baseInfoApi from '@/api/financial/liquidity/baseInfoApi'
import { observer } from '@zswl/admin'
import { PageListDownloadAction as PageListDown } from '@/components/Actions'
import { message, Tooltip, TreeSelect } from 'antd'
import dayjs from 'dayjs'
import ourRepaymentAccountApi from '@/api/financial/fund/ourRepaymentAccountApi'
import { saveServer } from '@/utils'

/**
 * 团队账户组件
 * @returns {ReactElement} 团队账户表单和表格
 */
const TeamAccount = observer(() => {
  const [editable, setEditable] = useState(false)
  const table = Table.useStore({
    request: async (params) => {
      const res = await baseInfoApi.postAccountSettingList(params)
      return res
    },
  })
  const { rows, keys } = table.getSelected()
  const accountSettingRestore = async () => {
    await baseInfoApi.postAccountSettingRestore({
      idList: keys,
    })
    table.search()
    message.success('账户还原成功')
  }
  const [bankOptions, setBankOptions] = useState([])
  const [flatBankOptions, setFlatBankOptions] = useState([])
  const getBankOptions = async () => {
    const res = await ourRepaymentAccountApi.postBankInfo({})
    const newBankOptions = res.reduce((acc, { accountNumber, accountBank, ...item }) => {
      const find = acc.find((accItem) => accItem.value === accountBank)
      if (!find) {
        acc.push({
          title: accountBank,
          disabled: true,
          value: accountBank,
          children: [{ ...item, title: accountNumber, value: accountNumber, accountBank }],
        })
      } else {
        find.children.push({ ...item, title: accountNumber, value: accountNumber, accountBank })
      }
      return acc
    }, [])
    const flatBankOptions = newBankOptions.flatMap(({ children }) => children)
    setFlatBankOptions(flatBankOptions)
    setBankOptions(newBankOptions)
  }

  useEffect(() => {
    getBankOptions()
  }, [])
  // 表格列配置
  const columns = [
    {
      title: '融资机构',
      dataIndex: 'organizationName',
      width: 200,
      search: true,
      editable: false,
    },
    {
      title: '融资编号',
      dataIndex: 'financingCode',
      search: true,
      width: 180,
      editable: false,
    },
    AmountColumn({ title: '融资金额', dataIndex: 'financingAmount', editable: false }),
    MatchOptionColumn({
      title: '账户用途',
      dataIndex: 'accountCategory',
      matchOption: 'fundFinancingAccountTypeEnum',
      width: 120,
      editable: false,
    }),
    {
      title: '还款银行/还款帐号',
      dataIndex: 'accountNumber',
      width: 480,
      editable: {
        element: <TreeSelect treeData={bankOptions} showSearch treeDefaultExpandAll />,
      },
      render: (text, { accountBank, accountNumber, isEdit }) => (
        <Tooltip title={`${accountBank}-${accountNumber}`}>
          <div
            style={{ color: isEdit ? 'red' : undefined }}
          >{`${accountBank}/${accountNumber}`}</div>
        </Tooltip>
      ),
      search: false,
    },
    // {
    //   title: '还款账号',
    //   dataIndex: 'accountNumber',
    //   width: 180,
    //   editable: (record, index) => {
    //     return {
    //       element: (
    //         <Select
    //           options={() => getAccountNumberOptions({ accountBank: record.accountBank })}
    //           allowClear
    //         />
    //       ),
    //     }
    //   },
    //   render: (text, record) => (
    //     <Tooltip title={text}>
    //       <div style={{ color: record.isEdit ? 'red' : undefined }}>{text}</div>
    //     </Tooltip>
    //   ),
    //   search: true,
    // },
    {
      title: '账户性质',
      dataIndex: 'accountType',
      matchOption: 'baseDataBankAccountTypeEnum',
      editable: false,
      render: (text, record) => {
        const title = App.matchOption('baseDataBankAccountTypeEnum', text).label
        return (
          <Tooltip title={title}>
            <div style={{ color: record.isEdit ? 'red' : undefined }}>{title}</div>
          </Tooltip>
        )
      },
      width: 100,
    },
    MatchOptionColumn({
      title: '是否模拟结清',
      dataIndex: 'simulateSettle',
      matchOption: 'trueOrFalse',
      search: true,
    }),
    DateColumn({ title: '模拟结清日期', dataIndex: 'settleTime', editable: true }),
    AmountColumn({
      title: '模拟结清金额',
      dataIndex: 'settleAmount',
      editable: true,
      initFormat: 1,
    }),
    {
      title: '资金经理',
      dataIndex: 'fundManagerName',
      width: 150,
      editable: false,
    },
  ]

  const save = async () => {
    const { list } = await table.submit()
    const isValidate = list.every((item) => {
      if (item.simulateSettle) {
        return item.settleTime && item.settleAmount
      }
      return true
    })
    if (!isValidate) {
      message.error('选择模拟结清的时，请填写模拟结清日期和金额')
      return
    }
    await baseInfoApi.postAccountSettingModify({
      list: list.map(({ settleTime, settleAmount, accountNumber, ...item }) => {
        const { bankAccountId: accountId, accountBank } =
          flatBankOptions.find(({ value }) => value === accountNumber) ?? {}
        return {
          ...item,
          accountId,
          accountBank,
          accountNumber,
          settleAmount: settleAmount ? +settleAmount : undefined,
          settleTime: settleTime ? dayjs(settleTime).format('YYYY-MM-DD') : undefined,
        }
      }),
    })
    table.search()
    setEditable(false)
    message.success('保存成功')
  }
  return (
    <Page noStyle className={styles.teamAccount}>
      <Table
        columnsFilter={'liquidity_predictionParameters_TeamAccount'}
        onFilter={(key, val) => saveServer('liquidity_predictionParameters_TeamAccount', val)}
        columns={columns}
        store={table}
        scroll={{ x: 1500 }}
        columnWidth={120}
        selectable
        editable={editable}
        searchbar={[
          {
            title: '还款银行',
            dataIndex: 'accountBank',
          },
          {
            title: '还款帐号',
            dataIndex: 'accountNumber',
          },
        ]}
        actions={[
          <Button.Reset type="primary" onClick={accountSettingRestore}>
            账户还原
          </Button.Reset>,

          <PageListDown module="accountSetting" table={table}>
            <Button.Download type="primary">导出</Button.Download>
          </PageListDown>,
          !editable && (
            <Button.Edit
              type="primary"
              onClick={() => {
                setEditable(true)
              }}
            >
              编辑
            </Button.Edit>
          ),
          editable && (
            <Button.Save type="primary" onClick={save}>
              保存
            </Button.Save>
          ),
          editable && (
            <Button.Withdraw
              onClick={() => {
                setEditable(false)
              }}
            >
              取消
            </Button.Withdraw>
          ),
        ]}
      />
    </Page>
  )
})

export default TeamAccount
