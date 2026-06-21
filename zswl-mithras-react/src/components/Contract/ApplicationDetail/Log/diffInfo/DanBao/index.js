import { RenderColumn } from '@/components/Format'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Tooltip } from 'antd'
import { hasValue } from '@/utils'
import mathjs from '@/utils/math'
import styles from './index.less'
import { saveServer } from '@/utils'

function ContractApplicationLogGuaranteeDiff({ detail, isLog }) {
  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>担保措施</div>
      </div>
      <Table
              columnsFilter={'diffInfo_ChengZuRen_1'}
              onFilter={(key,val) => saveServer('diffInfo_ChengZuRen_1',val)}
        scroll={{ x: 1200 }}
        rowKey={'guarantorType'}
        autoRequest={false}
        dataSource={detail}
        columns={[
          {
            title: '保证合同编号',
            dataIndex: 'guarantorContractCode',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '担保人类型',
            dataIndex: 'guarantorType',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="clientType"></RenderColumn>
            ),
          },
          {
            title: '指定联系人',
            dataIndex: 'contactName',
            render(val) {
              return <RenderColumn data={val} isCompare={isLog}></RenderColumn>
            },
          },
          {
            title: '担保人名称',
            dataIndex: 'guarantorInfo',
            width: 200,
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                formatText={(v) => {
                  return v?.map((item) => item.clientName).join(',')
                }}
              ></RenderColumn>
            ),
          },
          {
            title: '关联合同编号',
            dataIndex: 'relatContracts',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                formatText={(v) => {
                  return v?.join(',')
                }}
              ></RenderColumn>
            ),
          },
          {
            title: '担保方式',
            dataIndex: 'guaranteeMethod',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                selectEnum="guaranteeMethodEnum"
              ></RenderColumn>
            ),
          },
          {
            title: '联保标志',
            dataIndex: 'jointGuaranteeMark',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                selectEnum="jointGuaranteeMarkEnum"
              ></RenderColumn>
            ),
          },
          {
            title: '担保本金(元)',
            dataIndex: 'amountSingle',
            render: (val, t) => {
              if (isLog) {
                if (['SINGLE', 'JOINT'].includes(t.jointGuaranteeMark?.value)) {
                  return (
                    <span style={{ color: t.amountSingle?.isChange ? 'red' : undefined }}>
                      {hasValue(t.amountSingle?.value)
                        ? mathjs.toNonExponential(
                            mathjs.format(mathjs.divide(t.amountSingle?.value, 10000))
                          )
                        : '-'}
                    </span>
                  )
                } else if (['MULTIPLE_SEPARATE'].includes(t.jointGuaranteeMark?.value)) {
                  const StrNode = t.amountMultiple.value?.map((item, index) => {
                    return hasValue(item.amount?.value) ? (
                      <div key={index}>{`${item.clientName?.value}(${mathjs.toNonExponential(
                        mathjs.format(mathjs.divide(item.amount?.value, 10000))
                      )})`}</div>
                    ) : (
                      '-'
                    )
                  })
                  return (
                    <Tooltip title={StrNode || '-'} placement="topLeft">
                      <span style={{ color: t.amountMultiple?.isChange ? 'red' : undefined }}>
                        {StrNode || '-'}
                      </span>
                    </Tooltip>
                  )
                }

                return '-'
              }
              if (['SINGLE', 'JOINT'].includes(t.jointGuaranteeMark)) {
                return (
                  <span>
                    {hasValue(t.amountSingle)
                      ? mathjs.toNonExponential(mathjs.format(mathjs.divide(t.amountSingle, 10000)))
                      : '-'}
                  </span>
                )
              } else if (['MULTIPLE_SEPARATE'].includes(t.jointGuaranteeMark)) {
                const StrNode = t.amountMultiple?.map((item, index) => {
                  return hasValue(item.amount) ? (
                    <div key={index}>{`${item.clientName}(${mathjs.toNonExponential(
                      mathjs.format(mathjs.divide(item.amount, 10000))
                    )})`}</div>
                  ) : (
                    '-'
                  )
                })
                return (
                  <Tooltip title={StrNode || '-'} placement="topLeft">
                    {StrNode || '-'}
                  </Tooltip>
                )
              }

              return '-'
            },
          },
          {
            title: '是否上报征信',
            dataIndex: 'isReport',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="isConfirm"></RenderColumn>
            ),
          },
        ]}
      />
    </div>
  )
}

export default observer(ContractApplicationLogGuaranteeDiff)
