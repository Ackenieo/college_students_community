<template>
  <div v-if="!isLoggedIn" class="auth-container">
    <div class="auth-header">
      <h1 class="auth-title">校园社区</h1>
      <p class="auth-subtitle">登录以享受完整功能</p>
    </div>

    <div class="auth-tabs">
      <button
        class="auth-tab"
        :class="{ active: authMode === 'login' }"
        @click="authMode = 'login'"
      >
        登录
      </button>
      <button
        class="auth-tab"
        :class="{ active: authMode === 'register' }"
        @click="authMode = 'register'"
      >
        注册
      </button>
    </div>

    <div class="auth-form">
      <input
        class="form-input"
        v-model="formData.username"
        placeholder="用户名"
      />
      <input
        v-if="authMode === 'register'"
        class="form-input"
        v-model="formData.email"
        placeholder="邮箱"
        type="email"
      />
      <input
        class="form-input"
        v-model="formData.password"
        placeholder="密码"
        type="password"
      />
      <button class="submit-btn" @click="handleSubmit">
        {{ authMode === 'login' ? '登录' : '注册' }}
      </button>
    </div>

    <div class="social-login">
      <p class="social-title">其他登录方式</p>
      <div class="social-buttons">
        <button class="social-btn">微信</button>
        <button class="social-btn">QQ</button>
        <button class="social-btn">手机</button>
      </div>
    </div>
  </div>

  <div v-else class="profile-container">
    <div class="profile-header glass-card">
      <div class="profile-avatar">{{ currentUser.username[0] }}</div>
      <h2 class="profile-name">{{ currentUser.username }}</h2>
      <p class="profile-email">{{ currentUser.email }}</p>
    </div>

    <div class="stats-grid">
      <div class="stat-item glass-card">
        <span class="stat-value">128</span>
        <span class="stat-label">帖子</span>
      </div>
      <div class="stat-item glass-card">
        <span class="stat-value">1.2k</span>
        <span class="stat-label">粉丝</span>
      </div>
      <div class="stat-item glass-card">
        <span class="stat-value">256</span>
        <span class="stat-label">关注</span>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item glass-card">
        <span>我的帖子</span>
        <span class="menu-arrow">›</span>
      </div>
      <div class="menu-item glass-card">
        <span>我的收藏</span>
        <span class="menu-arrow">›</span>
      </div>
      <div class="menu-item glass-card">
        <span>设置</span>
        <span class="menu-arrow">›</span>
      </div>
      <div class="menu-item glass-card" @click="handleLogout">
        <span class="logout-text">退出登录</span>
        <span class="menu-arrow">›</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const authMode = ref('login')
const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUser = computed(() => userStore.currentUser)

const formData = reactive({
  username: '',
  email: '',
  password: ''
})

function handleSubmit() {
  if (!formData.username || !formData.password) {
    alert('请填写完整信息')
    return
  }
  userStore.login({
    username: formData.username,
    email: formData.email || `${formData.username}@campus.com`
  })
}

function handleLogout() {
  userStore.logout()
  formData.username = ''
  formData.email = ''
  formData.password = ''
}
</script>

<style lang="scss" scoped>
.auth-container {
  padding: 40px 24px;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-title {
  font-size: 28px;
  font-weight: 700;
  color: #5cc9b7;
  margin-bottom: 8px;
}

.auth-subtitle {
  font-size: 14px;
  color: #8b8f98;
}

.auth-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.auth-tab {
  flex: 1;
  padding: 12px;
  background: rgba(22, 24, 30, 0.78);
  border: none;
  border-radius: 8px;
  color: #8b8f98;
  font-size: 15px;
  cursor: pointer;
}

.auth-tab.active {
  background: #5cc9b7;
  color: #0c0e13;
  font-weight: 600;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 32px;
}

.form-input {
  padding: 14px;
  background: rgba(22, 24, 30, 0.78);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  color: #e4e6ea;
  font-size: 15px;
}

.submit-btn {
  padding: 14px;
  background: linear-gradient(135deg, #5cc9b7, #4a9e8f);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.social-login {
  text-align: center;
}

.social-title {
  font-size: 13px;
  color: #8b8f98;
  margin-bottom: 16px;
}

.social-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.social-btn {
  padding: 10px 20px;
  background: rgba(22, 24, 30, 0.78);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  color: #e4e6ea;
  font-size: 13px;
  cursor: pointer;
}

.profile-container {
  padding: 16px;
}

.profile-header {
  padding: 32px 16px;
  text-align: center;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(22, 24, 30, 0.62);
  border-radius: 24px;
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.32);
  backdrop-filter: blur(14px);
}

.profile-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #5cc9b7, #4a9e8f);
  border-radius: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
  color: white;
  margin: 0 auto 16px;
}

.profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #e4e6ea;
  margin-bottom: 4px;
}

.profile-email {
  font-size: 13px;
  color: #8b8f98;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.stat-item {
  padding: 16px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(22, 24, 30, 0.62);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(10px);
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #5cc9b7;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8b8f98;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(22, 24, 30, 0.62);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(10px);
}

.menu-arrow {
  font-size: 20px;
  color: #8b8f98;
}

.logout-text {
  color: #ff4757;
}
</style>
