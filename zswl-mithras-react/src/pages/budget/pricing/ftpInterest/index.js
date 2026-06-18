import { Page, Table, TableStore, ModalStore, PageStore } from '@zswl/components'
import { useMemo, useRef } from 'react'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import Api from '@/api/pricing/ftpInterest'
import localApi from './priceDetail/api'
import { PageListDown } from '@/components'
import FtpRunModal from './FtpRunModal'
import { message } from 'antd'
import { BudgetFtpInterestPriceChangeModal as PriceChangeModal } from '@/components/Budget/PricingFtpInterestEntries'
import ftpInterestChangeApi from '@/api/budget/pricing/ftpInterestChangeApi'
import { saveServer } from '@/utils'

export const getDetail = async (params = {}) => {
  const res = await ftpInterestChangeApi.postApplyDetail(params)
  return res
}
const formNameColumns = ['借据编号', '客户名称', '合同编号', '业务部门', '项目主办']
const nameColumns = [
  '借据编号',
  '客户名称',
  '合同编号',
  'FTP价格(%)',
  {
    title: '当年累计计息(元)-列表',
    rename: '当年累计计息(元)',
  },
  '更新日期',
  '业务部门',
  '项目主办',
]
const Index = ({ pathname }) => {
  const columns = getTableColumns(ALL_COLUMNS({ pathname }), nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS(), formNameColumns)
  const FtpRunModalRef = useRef()

  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.postInterestPagelist(params)
        return data
      },
    })
  }, [])

  const $editModal = new ModalStore({
    onFinish: async (values) => {
      await localApi.postFtpInterestRecalculate({
        interestDate: values.interestDate,
      })
      message.success('操作成功')
      $editModal.close()
      $table.search()
    },
  })

  const priceChangeModal = useMemo(() => {
    return new ModalStore({
      onOpen: async () => {
        const res = await getDetail()
        return res
      },
      onFinish: () => {},
    })
  }, [])
  return (
    <Page>
      <Table
        columnsFilter="pricing_ftpInterest_1"
        onFilter={(key, val) => saveServer('pricing_ftpInterest_1', val)}
        actions={[
          // {
          //   name: 'FTP计息',
          //   type: 'primary',
          //   onClick: () => FtpRunModalRef.current.open(),
          // },
          {
            name: 'FTP计息变更',
            type: 'primary',
            onClick: () => priceChangeModal.open(),
          },
        ]}
        store={$table}
        editable={false}
        searchbar={{
          items: formColumns,
        }}
        scroll={{
          x: true,
        }}
        extra={[<PageListDown key="1" module="ftpInterest" table={$table} />]}
        columns={columns}
      />
      <FtpRunModal
        ref={FtpRunModalRef}
        onFinish={() => {
          $table.search()
        }}
      />
      <PriceChangeModal modal={priceChangeModal} />
    </Page>
  )
}
export default Index
