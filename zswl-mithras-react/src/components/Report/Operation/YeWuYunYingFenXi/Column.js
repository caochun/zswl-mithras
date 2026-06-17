import { AmountColumn } from '@/components/Format'
const INIT_FORMAT = 10000 * 10000

export const ALL_COLUMNS = [
  {
    title: '立项创建',
    children: [
      AmountColumn({
        title: '数量',
        dataIndex: 'projEstablishCreateQuantity',
        initFormat: 1,
      }),
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'projEstablishCreateAmount',
        initFormat: INIT_FORMAT,
      }),
    ],
  },
  {
    title: '评审创建',
    children: [
      AmountColumn({
        title: '数量',
        dataIndex: 'projReviewCreateQuantity',
        initFormat: 1,
      }),
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'projReviewCreateAmount',
        initFormat: INIT_FORMAT,
      }),
    ],
  },
  {
    title: '租赁物创建',
    children: [
      AmountColumn({
        title: '数量',
        dataIndex: 'leaseItemCreateQuantity',
        initFormat: 1,
      }),
      // AmountColumn({
      //   title: '金额(万元)',
      //   dataIndex: 'leaseItemCreateAmount',
      //   initFormat: INIT_FORMAT,
      // }),
    ],
  },
  {
    title: '合同付款',
    children: [
      AmountColumn({
        title: '数量',
        dataIndex: 'paymentCreateQuantity',
        initFormat: 1,
      }),
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'paymentCreateAmount',
        initFormat: INIT_FORMAT,
      }),
    ],
  },
  {
    title: '合同创建',
    children: [
      AmountColumn({
        title: '数量',
        dataIndex: 'contractCreateQuantity',
        initFormat: 1,
      }),
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'contractCreateAmount',
        initFormat: INIT_FORMAT,
      }),
    ],
  },
  {
    title: '合同投放',
    children: [
      AmountColumn({
        title: '数量',
        dataIndex: 'paymentActualPayQuantity',
        initFormat: 1,
      }),
      AmountColumn({
        title: '金额(万元)',
        dataIndex: 'paymentActualPayAmount',
        initFormat: INIT_FORMAT,
      }),
    ],
  },
]
