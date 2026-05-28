export function scrollToAnchor(anchorId) {
  const anchorElement = document.getElementById(anchorId)
  if (anchorElement) {
    const scrollY = window.scrollY
    const anchorPosition = anchorElement.getBoundingClientRect().top + scrollY
    window.scrollTo({ top: anchorPosition, behavior: 'smooth' })
  }
}
