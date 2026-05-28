import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { router } from './router'
import Vant from 'vant'
import 'vant/lib/index.css'
import '@vant/touch-emulator'
import App from './App.vue'
import './assets/style/global.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Vant)

app.mount('#app')
