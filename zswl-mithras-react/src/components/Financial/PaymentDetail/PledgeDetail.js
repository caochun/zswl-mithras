import { ModalStore, Table, TableStore } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from '@/components/Financial/PaymentColumns'
import { useMemo } from 'react'
import fundReceiptRepayBaseInfoApi from '@/api/financial/fundReceiptRepayBaseInfoApi'
import Api from '@/api/financial/fundApi'
import CreateModal from './CreateModal'
import moment from 'moment'
import { saveServer } from '@/utils'

const getDetail = async (pledgeId) => {
  const res = await Api.postPledgeDetail({ pledgeId })
  return res
}
function Index({ path, id, businessVersion, detail, isFormApproval }) {
  const openModal = useMemo(
    () =>
      new ModalStore({
        onOpen: async (record) => {
          if (record) {
            const pledgeId = record.id.value ?? record.id
            const res = await getDetail(pledgeId)
            return {
              ...res,
              contractStartDate: res.contractStartDate && moment(res.contractStartDate),
              contractEndDate: res.contractEndDate && moment(res.contractEndDate),
              projReviewId: {
                label: res.projName,
                value: res.projReviewId,
              },
              contractId: {
                label: res.contractCode,
                value: res.contractCode,
              },
            }
          }
        },
      }),
    []
  )
  const nameColumns = [
    // 质押编号,项目名称,合同编号,合同金额（元）,合同期限,剩余未还本金（元）
    {
      title: '质押编号',
      dataIndex: 'pledgeCode',
      actions: (record) => [{ name: record.pledgeCode, onClick: () => openModal.open(record) }],
    },
    {
      title: '项目名称',
      dataIndex: 'projName',
      width: 300,
      actions: ({ projName: name, projReviewId }) => [
        { name, to: `/project/review/detail/${projReviewId}?typeId=review` },
      ],
    },
    {
      title: '合同编号',
      width: 300,
      actions: ({ contractCode: name, contractId }) => [
        { name, to: `/contract/list/detail/${contractId}` },
      ],
    },
    '合同金额（元）',
    '合同期限',
    { title: '剩余未还本金（元）', dataIndex: 'remainingUnpaidPrincipal' },
  ]

  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  const table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        if (detail) return Promise.resolve({ list: formatData(detail) })
        if (isFormApproval) {
          const res = await fundReceiptRepayBaseInfoApi.postPledgeDetailCompare({
            id,
            businessVersion,
            ...params,
          })

          const newList = formatData(res)
          return newList
        }
        const res = await fundReceiptRepayBaseInfoApi.postPledgeDetail({
          id,
          ...params,
        })
        return res
      },
    })
  }, [])
  return (
    <div>
      <h3>质押明细</h3>
      <Table         columnsFilter={'payment_detail_PledgeDetail'}
        onFilter={(key,val) => saveServer('payment_detail_PledgeDetail',val)} store={table} scroll={{ x: 1500 }} columns={columns} />
      <CreateModal store={openModal} />
    </div>
  )
}

export default observer(Index)
function formatData(res) {
  return res.map(
    ({ pledgeCode, id: itemId, projName, projReviewId, contractCode, contractId, ...item }) => {
      return {
        ...item,
        pledgeCode: pledgeCode?.value ?? pledgeCode,
        id: itemId?.value ?? itemId,
        projName: projName?.value ?? projName,
        projReviewId: projReviewId?.value ?? projReviewId,
        contractCode: contractCode?.value ?? contractCode,
        contractId: contractId?.value ?? contractId,
      }
    }
  )
}
