<template>
  <div class="publish-content">
    <input class="title-input" v-model="title" placeholder="标题" />
    <textarea class="content-input" v-model="content" placeholder="分享你的想法..." rows="10"></textarea>

    <div class="publish-options">
      <div class="option-item">
        <span>分类</span>
        <select v-model="category">
          <option>笔记</option>
          <option>长文</option>
          <option>视频</option>
        </select>
      </div>
    </div>

    <button class="publish-btn" @click="handlePublish">发布</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const title = ref('')
const content = ref('')
const category = ref('笔记')

function handlePublish() {
  if (!title.value || !content.value) {
    alert('请填写标题和内容')
    return
  }
  alert('发布成功！')
  router.push('/feed')
}
</script>

<style lang="scss" scoped>
.publish-content {
  padding: 16px;
}

.title-input {
  width: 100%;
  padding: 12px;
  background: rgba(22, 24, 30, 0.78);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  color: #e4e6ea;
  font-size: 16px;
  margin-bottom: 12px;
}

.content-input {
  width: 100%;
  padding: 12px;
  background: rgba(22, 24, 30, 0.78);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  color: #e4e6ea;
  font-size: 14px;
  margin-bottom: 16px;
  resize: none;
}

.publish-options {
  margin-bottom: 24px;
}

.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: rgba(22, 24, 30, 0.78);
  border-radius: 8px;
  color: #e4e6ea;
}

.publish-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #5cc9b7, #4a9e8f);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}
</style>
