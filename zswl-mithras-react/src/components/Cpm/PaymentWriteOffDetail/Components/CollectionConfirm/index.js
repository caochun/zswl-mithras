
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { getQuery, observer } from '@zswl/admin'
import { useEffect, useRef, useState } from 'react'
import { message, Modal } from 'antd'
import Api from '../../api'
import { DatePicker } from 'antd'
import moment from 'moment';

function Index({ detail, canEdit = true, paymentId }) {
  const isZhiZu = detail.leaseTypeCode === 'zhi_zu'
  const [date,setDate] = useState('')
  // 财务节点
  const ref = useRef()
  const saveData = async (values) => {
    if(values.retentionMoney && parseFloat(values.retentionMoney) > 0 && !date){
      message.error('请填写厂商质保金退还日期！')
      throw new Error('请填写厂商质保金退还日期！')
    }
    return new Promise((resolve, reject) => {
      Modal.confirm({
        title: `系统提示`,
        content: '您所填写收款的信息将影响起租时实际IRR与折现率的计算，请准确填写！',
        onOk: async () => {
          await Api.postPayMentCollectionAdd({
            ...values,
            warrantyReturnDate:date ? date.format('YYYY-MM-DD') : paymentDetail.warrantyReturnDate,
            warrantyPayWay:paymentDetail.warrantyPayWay,
            paymentId
          })
          getDetail(paymentId)
          resolve()
        },
        onCancel: () => {
          reject()
        },
      })
    })
  }
  const [paymentDetail, setPaymentDetail] = useState({})
  const [columns, setColumns] = useState([])
  const getDetail = async (id, ) => {
    const res = await Api.getPayMentCollectionDetail({ paymentId: id })
    setPaymentDetail(res)
    setColumns(getDescColumns(ALL_COLUMNS({ isZhiZu, retentionMoney:res.retentionMoney }, false)))
  }
  useEffect(() => {
    paymentId && getDetail(paymentId, false)
  }, [paymentId])
  return (
    <EditDescription
      style={{ marginTop: 20 }}
      title="收款确认"
      detail={paymentDetail}
      saveData={saveData}
      canEdit={canEdit}
      columns={columns}
      ref={ref}
      column={2}
      contentStyle={{
        width: 200,
      }}
      onEditStatusChan
      onEditStatusChange={val => setColumns(pre => pre.map(item => {
        if(item.dataIndex === 'warrantyReturnDate'){
          if(!val){
            delete item.render
            return item
          }
          return {
            ...item,
            render: (v) => {
              return <ZDatePicker setDate={setDate} value={v}/>
            }
          }
        } else if(item.dataIndex === 'warrantyPayWay'){
          return {
            ...item,
            render: (val) => {
              const label = val === 1 ? '不内扣' : val === 0 ? '内扣' : '-';
              return label
            },
          }
        }
        return item
      }))}
    />
  )
}

const ZDatePicker = ({ value, onChange, setDate, ...props}) => {
  const [ val, setVal ] = useState(value)
  return (
    <DatePicker
      {...props}
      style={{ width: '100%' }}
      disabledDate={(current)=>current && current < moment().startOf('day')}
      value={val ? moment(val) : null}
      onChange={(date) => {
        setVal(date)
        setDate(date)
        // onChange(date ? date.format('YYYY-MM-DD') : null)
      }}
    />
  )
}

export default observer(Index)
