import fundTransferApi from '@/api/financial/fundTransfer'
import { downLoadExcel } from '@/components/Excel'
import { saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import {
  App,
  Button,
  Input,
  Modal,
  ModalStore,
  SearchBar,
  Select,
  Table,
  TableStore,
} from '@zswl/components'
import moment from 'moment'
import { useEffect, useMemo } from 'react'

const { Item } = SearchBar

const DepositDetailModal = () => {
  const { optionsType } = App.getData()
  const $depositModal = useMemo(() => new ModalStore(), [])

  const settingTimeOptions = useMemo(() => {
    return [{ label: '全部', value: '' }, ...(optionsType.settingTimeEnum || [])]
  }, [optionsType.settingTimeEnum])

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const { settingTime, accountBank, ...rest } = params || {}
        const result = await fundTransferApi.postcurrentDailytList({
          currentDate: moment().format('YYYY-MM-DD'),
          accountBank,
          settingTime,
          ...rest,
        })
        return {
          list: result?.list || [],
          total: result?.total || 0,
        }
      },
    })
  }, [])

  useEffect(() => {
    if ($depositModal.visible) {
      $table.search()
    }
  }, [$depositModal.visible])

  const handleOpenModal = () => {
    $depositModal.open()
  }

  const columns = [
    {
      title: '#',
      dataIndex: 'index',
      width: 60,
      render: (text, record, index) => {
        const params = $table.getParams()
        const page = params?.page || 1
        const pageSize = params?.pageSize || 10
        return (page - 1) * pageSize + index + 1
      },
    },
    {
      title: '沉淀时间（工作日）',
      dataIndex: 'settingTime',
      width: 150,
      render: (text) => App.matchOption('settingTimeEnum', text)?.label || '-',
    },
    {
      title: '银行名称',
      dataIndex: 'accountBank',
      width: 240,
      render: (text) => text || '-',
    },
    {
      title: '银行账号',
      dataIndex: 'accountNumber',
      width: 200,
      render: (text) => text || '-',
    },
    {
      title: '沉淀金额（元）',
      dataIndex: 'depositedAmount',
      width: 150,
      align: 'right',
      render: (text) => {
        if (!text) return '0.00'
        const amount = text / 10000
        return amount.toLocaleString('zh-CN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })
      },
    },
  ]

  const handleExport = async () => {
    try {
      const params = $table.getParams()

      const postParams = {
        ...params,
        currentDate: moment().format('YYYY-MM-DD'),
        pageSize: 5000,
        page: 1,
      }

      const res = await fundTransferApi.postcurrentDailytList(postParams)

      const exportColumns = columns.filter((col) => col.dataIndex !== 'index')

      downLoadExcel({
        fileName: `沉淀资金明细列表_${moment().format('YYYYMMDD')}`,
        dataSource: res?.list || [],
        columns: exportColumns,
      })
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '8px' }}>
        <Button type="primary" onClick={handleOpenModal}>
          查看明细
        </Button>
      </div>
      <Modal
        title="沉淀资金明细列表"
        store={$depositModal}
        width={1100}
        destroyOnClose
        footer={null}
      >
        <Table
          columnsFilter="FundTransferDaily_DepositDetail"
          onFilter={(key, val) => saveServer('FundTransferDaily_DepositDetail', val)}
          store={$table}
          columns={columns}
          columnWidth={120}
          searchbar={{
            items: [
              <Item key="settingTime" label="沉淀时间（工作日）" name="settingTime">
                <Select
                  options={settingTimeOptions}
                  getPopupContainer={(trigger) => trigger.parentNode}
                />
              </Item>,
              <Item key="accountBank" label="银行名称" name="accountBank">
                <Input allowClear placeholder="请输入银行名称" />
              </Item>,
            ],
          }}
          extra={[
            <Button key="export" type="primary" onClick={handleExport}>
              导出
            </Button>,
          ]}
        />
      </Modal>
    </>
  )
}

export default observer(DepositDetailModal)
