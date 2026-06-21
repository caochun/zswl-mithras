import React, { useState } from 'react'
import { Table, Modal, Button } from 'antd'
import styles from './styles.less'
import { observer } from '@zswl/admin'

const formatAmount = (amount) => {
  const value = Number(amount / 10000)
  return !isNaN(value) ? value.toLocaleString() : '0'
}

function CreditClassification({ store }) {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const showModal = () => {
        setIsModalOpen(true);
    }
    const handleOk = () => {
        setIsModalOpen(false);
    };
    const handleCancel = () => {
        setIsModalOpen(false);
    };
    return (
        <div className={styles['class-summary-container']}>
            <div className={styles['credit-summary-header']}>
                <span className={styles['class-summary-detail']} onClick={showModal}>查看详情&gt;</span>
                <div className={styles['credit-summary-icon']} />
                <div className={styles['credit-summary-info']}>
                <div className={styles['class-summary-row']}>
                    <div className={styles['credit-summary-label']}>五级分类</div>
                    <div className={styles['credit-summary-value']}>
                        {store?.classic?.classification}
                    </div>
                </div>
                </div>
            </div>

            <div className={styles['credit-summary-divider']} />
            <div className={styles['class-summary-content']}>
                <div className={styles['data-container']}>
                <div className={styles['data-item']}>
                    <div className={styles['data-label']}>
                        最大逾期天数
                    </div>
                    <div className={styles['data-value']}>
                    {store?.classic?.maxOverdueDayCount || 0}
                    </div>
                </div>
                <div className={styles['data-item']}>
                    <div className={styles['data-label']}>
                        逾期总金额
                    </div>
                    <div className={styles['data-value']}>
                    {formatAmount(store?.classic?.totalAmountOverdue) || 0}
                    </div>
                </div>
                </div>
            </div>
            <Modal
                title="客户预期详情"
                open={isModalOpen}
                onCancel={handleCancel}
                width={800}
                footer={<Button onClick={handleCancel} type='primary'>关闭</Button>}
            >
                <Table
                    dataSource={store?.classic.contarctRSPList||[]}
                    columns={[
                        {
                            title: '合同编号',
                            dataIndex: 'contractCode',
                            key: 'contractCode',
                            width: 270
                        },{
                            title: '当前逾期期次',
                            dataIndex: 'phase',
                            key: 'phase',
                        },{
                            title: '当前逾期天数',
                            dataIndex: 'overdueDays',
                            key: 'overdueDays',
                        },{
                            title: '当前逾期总金额',
                            dataIndex: 'overdueAmount',
                            key: 'overdueAmount',
                            render:val => formatAmount(val)
                        },
                    ]}
                    pagination={false}
                />
            </Modal>
        </div>
    )
}

export default observer(CreditClassification)
