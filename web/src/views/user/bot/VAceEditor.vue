<template>
  <div>
    <div class="slecet  float-end" style="width: 150px; margin-bottom: 20px;">
      <select class="form-select" aria-label="Default select example" @change="getvaluemethod($event)">
        <option value="java" selected>Java</option>
        <option value="python">Python</option>
        <option value="javascript">Javascript</option>
        <option value="c_cpp">C++</option>
      </select>
    </div>
    <!--  v-bind="$attrs" 传入父组件的属性-->
    <v-ace-editor v-bind="$attrs" @init="editorInit" :lang="aceConfig.lang" :options="{
      enableBasicAutocompletion: true,
      enableSnippets: true,
      enableLiveAutocompletion: true,
      fontSize: 14,
      tabSize: 2,
      showPrintMargin: false,
      highlightActiveLine: true,
    }" :theme="aceConfig.theme" :readonly="aceConfig.readOnly"
      style="height: 400px; width: 100%; background: #000; color: #fff">
    </v-ace-editor>
  </div>
</template>

<script>
import {
  defineComponent,
  reactive,
  toRefs,
  ref,
  onMounted,
  nextTick,
} from "vue";
import * as ace from 'ace-builds';
import { VAceEditor } from "vue3-ace-editor";
export default defineComponent({
  name: "CodeEditor",
  components: {
    VAceEditor,
  },
  setup() {
    ace.config.set(
      "basePath",
      "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")
    const sqlContent = ref("");
    const state = reactive({
      test: "",
    });
    const aceConfig = reactive({
      lang: "java",
      theme: "monokai",
      readOnly: false,
      options: {
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: true,
        fontSize: 14,
        tabSize: 2,
        showPrintMargin: false,
        highlightActiveLine: true,
      }
    });
    function editorInit() {
      require("ace-builds/src-noconflict/ext-language_tools");
      require("ace-builds/src-noconflict/snippets/sql");
      require("ace-builds/src-noconflict/mode-sql");
      require("ace-builds/src-noconflict/mode-java");
      require("ace-builds/src-noconflict/mode-c_cpp");
      require("ace-builds/src-noconflict/theme-monokai");
      require("ace-builds/src-noconflict/mode-html");
      require("ace-builds/src-noconflict/mode-html_elixir");
      require("ace-builds/src-noconflict/mode-html_ruby");
      require("ace-builds/src-noconflict/mode-javascript");
      require("ace-builds/src-noconflict/mode-python");
      require("ace-builds/src-noconflict/snippets/less");
      require("ace-builds/src-noconflict/theme-chrome");
      require("ace-builds/src-noconflict/ext-static_highlight");
      require("ace-builds/src-noconflict/ext-beautify");
    }
    onMounted(() => {
      nextTick(() => {
        editorInit();
      });
    });

    const getvaluemethod = (event) => {
      aceConfig.lang = event.target.value;
    }
    return {
      sqlContent,
      ...toRefs(state),
      editorInit,
      aceConfig,
      getvaluemethod
    };
  },
});
</script>

<style>
/* 修改光标颜色 */
.ace_cursor {
  color: #fff !important;
}
</style>