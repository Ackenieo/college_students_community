<template>
  <div class="conversation-list">
    <div
      v-for="conv in conversations"
      :key="conv.id"
      class="conversation-item glass-card"
    >
      <div class="avatar">{{ conv.user.name[0] }}</div>
      <div class="conversation-info">
        <div class="conversation-header">
          <span class="user-name">{{ conv.user.name }}</span>
          <span class="time">{{ conv.time }}</span>
        </div>
        <div class="conversation-footer">
          <span class="last-message">{{ conv.lastMessage }}</span>
          <span class="unread-badge" v-if="conv.unread > 0">{{ conv.unread }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useMessageStore } from '@/store/message'

const messageStore = useMessageStore()
const conversations = computed(() => messageStore.conversations)
</script>

<style lang="scss" scoped>
.conversation-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.conversation-item {
  padding: 12px;
  display: flex;
  gap: 12px;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.07);
  background: rgba(22, 24, 30, 0.62);
  border-radius: 24px;
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.32);
  backdrop-filter: blur(14px);
}

.avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #5cc9b7, #4a9e8f);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: white;
}

.conversation-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #e4e6ea;
}

.time {
  font-size: 12px;
  color: #8b8f98;
}

.conversation-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  font-size: 13px;
  color: #8b8f98;
}

.unread-badge {
  min-width: 20px;
  height: 20px;
  background: #ff4757;
  border-radius: 10px;
  font-size: 11px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}
</style>
