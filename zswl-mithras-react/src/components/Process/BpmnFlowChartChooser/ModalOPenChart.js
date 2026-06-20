import { observer } from '@zswl/admin'
import React, { useRef } from 'react'
import BpmnFlowChart from './BpmnFlowChart'
import { Modal } from 'antd'

const ModalOPenChart = ({ processInstanceId, visible, setVisible, callBack }) => {
    const dataRef = useRef(null)

    const handleCancel = () => {
        dataRef.current = null
        setVisible(false)
    }
    const handleOk = async () => {
        callBack && callBack(dataRef.current)
        setVisible(false)
        dataRef.current = null
    }
    const childrenCallBack = (value) => {
        dataRef.current = value
    }
    return (
        <Modal
            title={'跳转'}
            open={visible}
            onCancel={handleCancel}
            onOk={handleOk}
            width={'60%'}
            destroyOnClose
        >
            <BpmnFlowChart height={500} processInstanceId={processInstanceId} childrenCallBack={childrenCallBack} />
        </Modal>
    )
}

export default observer(ModalOPenChart)
