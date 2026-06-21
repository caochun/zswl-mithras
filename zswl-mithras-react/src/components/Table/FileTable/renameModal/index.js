import { Input, Space, Row, Col, message } from 'antd'
import { useEffect, useState,useMemo} from 'react'
import moment from 'moment'
import { Select, Form, App, Table, Button, Modal,ModalStore } from '@zswl/components'
import { http } from '@zswl/admin'
import { saveServer } from '@/utils'
import { observer } from '@zswl/admin'

function FileRenameModal({ record,form,modalStore ,handleOk}) {
    return (
        <Modal       
            // onCancel={handleCancel}
            onOk={handleOk}
        title={'文件重命名'} store={modalStore} destroyOnClose>
            <Form form={form} >
            <Form.Item rules={[{ required: true, message: '请输入文件名' }]} label="文件名" name="filename">
                <Input placeholder="请输入文件名" />
            </Form.Item>
            <Form.Item rules={[{ required: true, message: '请输入文件后缀' }]} label="文件后缀" name="suffix" hidden>
                <Input placeholder="请输入文件后缀"  />
            </Form.Item>
            </Form>
        </Modal>
    )
}

export default observer(FileRenameModal)
