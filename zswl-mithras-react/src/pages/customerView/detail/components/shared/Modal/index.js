import { observer } from '@zswl/admin'
import { Form, Modal as ZModal } from '@zswl/components'
import style from './index.less'
const Modal = ({
  labelCol,
  modal,
  items,
  width,
  title,
  formStore,
  disabled,
  column = 1,
  layout = 'vertical',
}) => {
  return (
    <ZModal
      width={width}
      store={modal}
      destroyOnClose
      propsBy={(data) => {
        return {
          title: title + '详情',
        }
      }}
    >
      <Form
        disabled={disabled}
        className={style['modal-wrap']}
        store={formStore}
        labelCol={labelCol}
        layout={layout}
        column={column}
        items={items.map((item) => ({
          ...item,
          element: {
            ...item.element,
            style:
              item?.element?.type === 'textArea' || item?.element?.type === 'input'
                ? {
                    color: '#000000',
                    '&[disabled]': {
                      color: '#000000',
                    },
                  }
                : undefined,
          },
        }))}
      />
    </ZModal>
  )
}

export default observer(Modal)
