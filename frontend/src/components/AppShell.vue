<template>
  <div class="app-shell" :class="transitionName">
    <div class="status-bar">
      <span>9:41</span>
      <span>校园社区</span>
    </div>

    <router-view />

    <nav v-if="showNav" class="floating-nav" :class="[`nav-active-${currentScreen}`, { 'highlight-fading': highlightFading, 'nav-entered': navEntered }]" aria-label="底部导航">
      <button v-for="item in navItems" :key="item[0]" class="nav-item" :class="`nav-${item[0]} ${currentScreen === item[0] ? 'active' : ''} ${navPulse === item[0] ? 'nav-animating' : ''}`" @click="handleNavPress(item[0])" @animationend="navPulse === item[0] && (navPulse = '')" :aria-label="item[1]">
        <template v-if="item[2]">
          <component :is="getNavIcon(item[2])" />
          <span v-if="item[0] !== 'publish'">{{ item[1] }}</span>
        </template>
        <template v-else>{{ item[1] }}</template>
      </button>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, h, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const currentScreen = ref('feed')
const transitionName = ref('')
const navPulse = ref('')
const highlightFading = ref(false)
const navEntered = ref(false)

const navItems = [
  ['feed', '首页', 'home'],
  ['shopping', '购物', 'cart'],
  ['publish', '发布', 'plus'],
  ['messages', '私信', 'chat'],
  ['auth', '我的', 'face']
]

const showNav = computed(() => route.name !== 'Detail')

watch(() => route.path, (newPath) => {
  const path = newPath.split('/')[1]
  if (path && ['feed', 'shopping', 'publish', 'messages', 'auth'].includes(path)) {
    currentScreen.value = path
  }
}, { immediate: true })

function getNavIcon(icon) {
  const icons = {
    home: TreeOutlineIcon,
    cart: CartOutlineIcon,
    plus: PlusPublishIcon,
    chat: ChatBubblesIcon,
    face: FaceOutlineIcon
  }
  return icons[icon] || null
}

function TreeOutlineIcon() {
  return h('svg', { class: 'nav-tree-icon', viewBox: '0 0 24 24', 'aria-hidden': 'true' }, [
    h('g', { class: 'tree-leaves' }, [
      h('path', { d: 'M12 3.5c-1.4 0-2.6.9-3.1 2.1A3.6 3.6 0 0 0 5.7 9.2c0 .6.1 1.1.4 1.6A3.8 3.8 0 0 0 8.7 17h6.6a3.8 3.8 0 0 0 2.6-6.2c.3-.5.4-1 .4-1.6a3.6 3.6 0 0 0-3.2-3.6A3.4 3.4 0 0 0 12 3.5Z' })
    ]),
    h('g', { class: 'tree-trunk' }, [
      h('path', { d: 'M12 12.7v7' }),
      h('path', { d: 'M8.9 19.7h6.2' }),
      h('path', { d: 'M12 16.1 9.7 13.9' }),
      h('path', { d: 'M12 15.2l2.4-2.3' })
    ])
  ])
}

function CartOutlineIcon() {
  return h('svg', { class: 'nav-cart-icon', viewBox: '0 0 24 24', 'aria-hidden': 'true' }, [
    h('path', { d: 'M4.2 5.2h2.1l1.7 9.4a2 2 0 0 0 2 1.7h6.8a2 2 0 0 0 1.9-1.5l1.1-5.3H7.1' }),
    h('path', { d: 'M9.7 19.2h.1' }),
    h('path', { d: 'M17 19.2h.1' })
  ])
}

function PlusPublishIcon() {
  return h('svg', { class: 'nav-plus-icon', viewBox: '0 0 24 24', 'aria-hidden': 'true' }, [
    h('path', { d: 'M12 6.2v11.6' }),
    h('path', { d: 'M6.2 12h11.6' })
  ])
}

function ChatBubblesIcon() {
  return h('svg', { class: 'nav-chat-icon', viewBox: '0 0 24 24', 'aria-hidden': 'true' }, [
    h('g', { class: 'chat-bubble-left' }, [
      h('path', { d: 'M5.6 4.8h4.9a4.4 4.4 0 0 1 4.4 4.4v.5a4.4 4.4 0 0 1-4.4 4.4H8.3l-3.5 2.8v-3.1A4.4 4.4 0 0 1 1.8 9.6v-.4a4.4 4.4 0 0 1 3.8-4.4Z' }),
      h('path', { d: 'M6.2 9.5h3.9' })
    ]),
    h('g', { class: 'chat-bubble-right' }, [
      h('path', { d: 'M15.1 9.3h2.2a4.4 4.4 0 0 1 4.4 4.4v.4a4.4 4.4 0 0 1-3.7 4.3v2.7l-3.4-2.7h-2.1a4.3 4.3 0 0 1-3.8-2.3' }),
      h('path', { d: 'M15.1 13.9h2.8' })
    ])
  ])
}

function FaceOutlineIcon() {
  return h('svg', { class: 'nav-face-icon', viewBox: '0 0 28 28', 'aria-hidden': 'true' }, [
    h('rect', { x: '4', y: '6', width: '20', height: '16', rx: '6' }),
    h('path', { class: 'face-eye face-eye-left', d: 'M9.2 13.5H12.8' }),
    h('path', { class: 'face-eye face-eye-right', d: 'M15.2 13.5H18.8' })
  ])
}

function handleNavPress(key) {
  navPulse.value = ''
  requestAnimationFrame(() => {
    navPulse.value = key
  })
  
  if (key !== currentScreen.value) {
    if (key === 'publish') {
      highlightFading.value = true
      setTimeout(() => {
        highlightFading.value = false
        currentScreen.value = 'publish'
        transitionName.value = ''
        requestAnimationFrame(() => {
          transitionName.value = 'route-tab-switch'
          router.push(`/${key}`)
        })
      }, 200)
    } else {
      transitionName.value = ''
      requestAnimationFrame(() => {
        transitionName.value = 'route-tab-switch'
        router.push(`/${key}`)
      })
    }
  }
}

onMounted(() => {
  requestAnimationFrame(() => {
    navEntered.value = true
  })
})
</script>

<style lang="scss">
.app-shell {
  width: 100%;
  min-height: 100vh;
  background: #0c0e13;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-shell.route-to-detail {
  .page,
  .detail-actionbar {
    animation: detailFadeIn 0.34s cubic-bezier(0.2, 0.8, 0.2, 1) both;
  }
}

.app-shell.route-from-detail {
  .page,
  .topbar,
  .bottom-nav {
    animation: feedReturnIn 0.36s cubic-bezier(0.2, 0.8, 0.2, 1) both;
  }
}

.app-shell.route-tab-switch {
  .page,
  .publish-content,
  .topbar {
    animation: tabScreenIn 0.32s cubic-bezier(0.2, 0.72, 0.2, 1) both;
  }
  
  .bottom-nav {
    animation: navBarSettle 0.32s cubic-bezier(0.2, 0.72, 0.2, 1) both;
  }
}

@keyframes detailFadeIn {
  from { opacity: 0; transform: translateY(10px) scale(0.985); filter: blur(8px); }
  to { opacity: 1; transform: translateY(0) scale(1); filter: blur(0); }
}

@keyframes feedReturnIn {
  from { opacity: 0; transform: translateX(-18px); filter: blur(6px); }
  to { opacity: 1; transform: translateX(0); filter: blur(0); }
}

@keyframes tabScreenIn {
  from { opacity: 0; transform: translateY(12px) scale(0.985); filter: blur(8px); }
  to { opacity: 1; transform: translateY(0) scale(1); filter: blur(0); }
}

@keyframes navBarSettle {
  0% { transform: translateY(8px); }
  100% { transform: translateY(0); }
}

.status-bar {
  height: 42px;
  padding: 14px 20px 0;
  display: flex;
  justify-content: space-between;
  color: rgba(228, 230, 234, 0.48);
  font-size: 12px;
  font-weight: 700;
  position: sticky;
  top: 0;
  z-index: 9;
  background: rgba(12, 14, 19, 0.82);
  backdrop-filter: blur(14px);
}

.floating-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 12;
  width: 100%;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 4px;
  padding: 8px 8px calc(10px + env(safe-area-inset-bottom));
  background: rgba(22, 24, 30, 0.82);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(18px) saturate(1.1);
  min-height: 78px;
  overflow: hidden;
}

.floating-nav::before {
  content: '';
  position: absolute;
  left: var(--nav-highlight-left, 8px);
  top: 50%;
  transform: translateY(-50%);
  width: calc((100% - 32px) / 5);
  height: 50px;
  border-radius: 20px;
  background: rgba(92, 201, 183, 0.12);
  opacity: var(--nav-highlight-opacity, 1);
  transition: left 0.34s cubic-bezier(0.2, 0.72, 0.2, 1), transform 0.34s cubic-bezier(0.2, 0.72, 0.2, 1), opacity 0.34s cubic-bezier(0.2, 0.72, 0.2, 1);
  pointer-events: none;
}

.floating-nav.highlight-fading::before {
  opacity: 0;
  transform: translateY(30px);
}

.floating-nav:not(.nav-entered)::before {
  opacity: 0;
  transform: translateY(30px);
}

.floating-nav.nav-active-feed { --nav-highlight-left: 8px; }
.floating-nav.nav-active-shopping { --nav-highlight-left: calc(8px + ((100% - 32px) / 5) + 4px); }
.floating-nav.nav-active-messages { --nav-highlight-left: calc(8px + ((100% - 32px) / 5) + 4px + ((100% - 32px) / 5) + 4px + ((100% - 32px) / 5) + 4px); }
.floating-nav.nav-active-auth { --nav-highlight-left: calc(8px + ((100% - 32px) / 5) + 4px + ((100% - 32px) / 5) + 4px + ((100% - 32px) / 5) + 4px + ((100% - 32px) / 5) + 4px); }
.floating-nav.nav-active-publish { --nav-highlight-opacity: 0; }

.nav-item {
  position: relative;
  z-index: 1;
  border: 0;
  background: transparent;
  display: grid;
  grid-auto-rows: max-content;
  align-content: center;
  justify-items: center;
  gap: 0;
  color: #5f636e;
  font-size: 11px;
  min-height: 48px;
  border-radius: 14px;
  line-height: 1;
  transition: color 0.22s ease, transform 0.22s ease;
  cursor: pointer;
}

.nav-item span {
  display: block;
  max-height: 0;
  opacity: 0;
  overflow: hidden;
  line-height: 1;
  transform: translateY(-3px);
  transition: max-height 0.24s ease, opacity 0.2s ease, transform 0.24s ease;
}

.nav-item.active {
  color: #5cc9b7;
}

.nav-item.active span {
  max-height: 12px;
  opacity: 1;
  transform: translateY(2px);
}

.nav-item.active .nav-tree-icon,
.nav-item.active .nav-cart-icon,
.nav-item.active .nav-chat-icon,
.nav-item.active .nav-face-icon {
  transform: translateY(-1px);
}

.nav-tree-icon, .nav-cart-icon, .nav-plus-icon, .nav-chat-icon, .nav-face-icon {
  width: 22px;
  height: 22px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
  margin-bottom: 0;
  transition: transform 0.24s ease;
}

.nav-chat-icon {
  width: 23px;
  height: 23px;
  stroke-width: 1.75;
}

.nav-tree-icon .tree-leaves {
  transform-box: fill-box;
  transform-origin: 50% 88%;
}

.nav-feed.nav-animating .tree-leaves {
  animation: treeLeavesWind 0.62s cubic-bezier(0.36, 0.07, 0.19, 0.97);
}

@keyframes treeLeavesWind {
  0%, 100% { transform: translateX(0) rotate(0deg); }
  18% { transform: translateX(1.4px) rotate(3deg); }
  36% { transform: translateX(-1px) rotate(-2.4deg); }
  54% { transform: translateX(1px) rotate(1.8deg); }
  72% { transform: translateX(-0.5px) rotate(-1deg); }
}

.nav-chat-icon .chat-bubble-left,
.nav-chat-icon .chat-bubble-right {
  transform-box: fill-box;
  transform-origin: 50% 50%;
}

.nav-messages.nav-animating .chat-bubble-left {
  animation: chatBubblePop 0.48s cubic-bezier(0.36, 0.07, 0.19, 0.97);
}

.nav-messages.nav-animating .chat-bubble-right {
  animation: chatBubblePop 0.48s cubic-bezier(0.36, 0.07, 0.19, 0.97) 0.08s;
}

@keyframes chatBubblePop {
  0% { transform: scale(0.85); opacity: 0.6; }
  50% { transform: scale(1.08); }
  100% { transform: scale(1); opacity: 1; }
}

.nav-face-icon .face-eye {
  transform-box: fill-box;
  transform-origin: 50% 50%;
}

.nav-auth.nav-animating .face-eye-left {
  animation: eyeBlink 0.52s cubic-bezier(0.36, 0.07, 0.19, 0.97);
}

.nav-auth.nav-animating .face-eye-right {
  animation: eyeBlink 0.52s cubic-bezier(0.36, 0.07, 0.19, 0.97) 0.06s;
}

@keyframes eyeBlink {
  0%, 100% { transform: scaleY(1); }
  40% { transform: scaleY(0.1); }
}

.nav-cart-icon {
  transform-box: fill-box;
  transform-origin: 50% 100%;
}

.nav-shopping.nav-animating .nav-cart-icon {
  animation: cartBounce 0.56s cubic-bezier(0.36, 0.07, 0.19, 0.97);
}

@keyframes cartBounce {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-2px) rotate(-4deg); }
  50% { transform: translateY(0) rotate(0deg); }
  75% { transform: translateY(-1px) rotate(2deg); }
}

.nav-plus-icon {
  width: 28px;
  height: 28px;
}

.nav-publish {
  position: relative;
}

.nav-publish::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background: linear-gradient(135deg, #5cc9b7, #3daa96);
  box-shadow: 0 8px 20px rgba(92, 201, 183, 0.35);
  transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.2s ease;
}

.nav-publish .nav-plus-icon {
  position: relative;
  z-index: 1;
  stroke: #0c0e13;
  stroke-width: 2.2;
  transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.nav-publish:active::before {
  transform: translate(-50%, -50%) scale(0.88);
  box-shadow: 0 4px 12px rgba(92, 201, 183, 0.25);
}

.nav-publish:active .nav-plus-icon {
  transform: scale(0.9);
}

.nav-publish.nav-animating::before {
  animation: publishPulse 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes publishPulse {
  0% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.12); }
  100% { transform: translate(-50%, -50%) scale(1); }
}

.nav-publish.nav-animating .nav-plus-icon {
  animation: publishIconSpin 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes publishIconSpin {
  0% { transform: rotate(0deg) scale(1); }
  50% { transform: rotate(90deg) scale(1.1); }
  100% { transform: rotate(180deg) scale(1); }
}
</style>
