import React, { useEffect, useRef, useState, cloneElement } from 'react'
import cls from 'classnames'
import styles from './index.less'

export const throttle = (fn, wait = 100) => {
  let timer
  let time = Date.now()

  return (params) => {
    clearTimeout(timer)
    if (time + wait - Date.now() < 0) {
      fn(params)
      time = Date.now()
    } else {
      timer = setTimeout(fn, wait / 5)
    }
  }
}

function Index({ navContent, onClickNav, navContainerStyle, offset = 0 }) {
  const contentEl = useRef(null)
  const navEl = useRef(null)
  const [navHeight, setNavHeight] = useState(0)
  const [currentIndex, setCurrentIndex] = useState(0)

  const onScroll = throttle(() => {
    const scrollElement =
      contentEl?.current || document.scrollingElement || document.documentElement
    const outerHeight = window.innerHeight
    const scrollTop = scrollElement.scrollTop
    const scrollHeight = scrollElement.scrollHeight
    const firstEl = contentEl.current.children[0]

    const isNotStart = firstEl && firstEl.offsetTop > scrollTop
    const isEnd = scrollTop + outerHeight >= scrollHeight
    if (isNotStart) {
      setCurrentIndex(0)
    } else if (isEnd) {
      setCurrentIndex(navContent.length - 1)
    } else {
      navContent.forEach((el, index) => {
        const target = contentEl.current.children[index]
        if (target) {
          const visibleVertical =
            target.offsetTop >= 0 &&
            scrollElement.scrollTop + navHeight >= target.offsetTop - offset &&
            scrollElement.scrollTop + navHeight < target.offsetTop + target.offsetHeight - offset
          if (visibleVertical) {
            setCurrentIndex(index)
          }
        }
      })
    }
  })

  const handleClick = (index) => {
    const offsetTop = contentEl.current.children[index]?.offsetTop || 0
    contentEl?.current?.scrollTo({ top: offsetTop - navHeight - offset, behavior: 'smooth' })
    onClickNav && onClickNav(index)
  }

  useEffect(() => {
    setNavHeight(navEl?.current?.clientHeight || 0)
    contentEl?.current?.addEventListener('scroll', onScroll)
    return () => {
      contentEl?.current?.removeEventListener('scroll', onScroll)
    }
  }, [onScroll, contentEl])

  const contentElOffsetTop = contentEl?.current?.offsetTop
  return (
    <div>
      <nav
        ref={navEl}
        className={styles.nav}
        style={{
          top: offset - 1,
          ...navContainerStyle,
        }}
      >
        {navContent.map((item, index) => (
          <div
            className={cls(styles.nav_item, currentIndex === index ? styles.nav_item_active : '')}
            onClick={() => handleClick(index)}
            key={`${index}`}
          >
            {item.title}
          </div>
        ))}
      </nav>
      <div
        ref={contentEl}
        style={{
          height: `calc(100% - ${contentElOffsetTop}px)`,
          overflowY: 'scroll',
        }}
      >
        {navContent.map((item) => {
          return cloneElement(item.component, {
            title: item.title,
            style: {
              marginBottom: 20,
              background: '#fff',
              borderRadius: 14,
            },
          })
        })}
      </div>
    </div>
  )
}

export default Index
