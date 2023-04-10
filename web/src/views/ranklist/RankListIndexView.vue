<template>
  <ContentField>
    <table class="table table-striped table-hover" tyle="text-align: center;">
      <thead>
        <tr>
          <th>玩家</th>
          <th>天梯分</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id" s>
          <td>
            <img :src="user.photo" alt="" class="user-user-photo">
            &nbsp;
            <span class="user-user-username">{{ user.username }}</span>
          </td>
          <td>
            {{ user.rating }}
          </td>
        </tr>
      </tbody>
    </table>
    <nav aria-label="...">
      <ul class="pagination" style="float: right;">
        <li class="page-item" @click="click_page(-2)">
          <a class="page-link" href="#">Previous</a>
        </li>
        <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number"
          @click="click_page(page.number)">
          <a class="page-link" href="#">{{ page.number }}</a>
        </li>
        <li class="page-item" @click="click_page(-1)">
          <a class="page-link" href="#">Next</a>
        </li>
      </ul>
    </nav>
  </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let users = ref([]);
    let current_page = 1;
    let total_users = 0;
    let pages = ref([]);

    const click_page = page => {
      if (page === -2) page = current_page - 1;
      else if (page === -1) page = current_page + 1;
      let max_pages = parseInt(Math.ceil(total_users / 10));

      if (page < 1 || page > max_pages) return;
      pull_page(page);
    }

    const update_pages = () => {
      let max_pages = parseInt(Math.ceil(total_users / 10));
      let new_pages = [];
      let start_page = Math.max(1, current_page - 2);
      let end_page = Math.min(max_pages, current_page + 2);
      for (let i = start_page; i <= end_page; i++) {
        new_pages.push({
          number: i,
          is_active: i === current_page ? "active" : "",
        });
      }
      pages.value = new_pages;
    }


    const pull_page = page => {
      current_page = page;
      $.ajax({
        url: "https://app2800.acapp.acwing.com.cn/api/rank/getList/",
        data: {
          page,
        },
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          users.value = resp.users;
          total_users = resp.users_count;
          update_pages();
        },
        error(resp) {
          console.log(resp);
        }
      });
    }

    pull_page(current_page);

    return {
      users,
      pages,
      click_page,
    }
  }

}
</script>

<style scoped>
img.user-user-photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}
</style>