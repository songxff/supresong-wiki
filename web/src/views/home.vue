<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <router-link :to="'/'">
            <MailOutlined />
            <span>欢迎</span>
          </router-link>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="isShowWelcome">
        <h1>欢迎使用supresong's知识库</h1>
      </div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px"/>
            {{ text }}
          </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover"/>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {StarOutlined, LikeOutlined, MessageOutlined} from '@ant-design/icons-vue';

// const listData: Record<string, string>[] = [];
// for (let i = 0; i < 23; i++) {
//   listData.push({
//     href: 'https://www.antdv.com/',
//     title: `ant design vue part ${i}`,
//     avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
//     description:
//         'Ant Design, a design language for background applications, is refined by Ant UED Team.',
//     content:
//         'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
//   });
// }

export default defineComponent({
  name: 'Home',
  setup() {
    const ebooks = ref()
    const actions: Record<string, string>[] = [
      {type: 'StarOutlined', text: '156'},
      {type: 'LikeOutlined', text: '156'},
      {type: 'MessageOutlined', text: '2'},
    ];

    /**
     * 查询所有分类
     **/
    const level1 = ref()
    let categories: any
    const handleQueryCategory = () => {
      axios.get("/category/all").then((resp) => {
        const data = resp.data
        if (data.success) {
          categories = data.content
          level1.value = []
          level1.value = Tool.array2Tree(categories, 0)
        } else {
          message.error(data.message);
        }
      })
    }

    const handleQueryEbook = (category2Id: any) => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          category2Id: category2Id
        }
      }).then((resp) => {
        const data = resp.data
        ebooks.value = data.content.list
      })
    }

    const isShowWelcome = ref(true)

    const handleClick = (value: any) => {
      if (value.key === 'welcome') {
        isShowWelcome.value = true
      } else {
        isShowWelcome.value = false
        handleQueryEbook(value.key)
      }
    }

    onMounted(() => {

      handleQueryCategory()
    })

    return {
      ebooks,
      actions,

      level1,
      handleQueryCategory,

      isShowWelcome,
      handleClick
    }
  }
});
</script>

<style scoped>
.undefined-has-sider {
  display: flex;
  flex-direction: row;
}
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>