<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
      >
        <a-menu-item key="welcome">
          <router-link :to="'/'">
            <MailOutlined />
            <span>欢迎</span>
          </router-link>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-putlined />{{item.name}}</span>
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
      <a-list item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
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
import {defineComponent, onMounted, ref, reactive, toRef} from 'vue';
import axios from 'axios'
import {Tool} from "@/util/tool";
import {message} from "ant-design-vue";
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
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  setup() {
    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      pageSize: 3,
    };
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


    const ebooks = ref();
    const ebooks1 = reactive({books: []});

    onMounted(() => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000
        }
      }).then(function (response) {
        // 把响应里的data拿出来
        const data = response.data;
        ebooks.value = data.content.list;//content是电子书列表
        // ebooks1.books = data.content;
        console.log(response);
      });
      handleQueryCategory()
    });
    return{
      ebooks,
      // ebooks2: toRef(ebooks1,"books"),
      pagination,
      actions,
      level1,
      handleQueryCategory
    };
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