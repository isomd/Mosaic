## 文件结构： <br>

src/pages/ <br>
├── users/ <br>
│ ├── [id].vue <br>
│ └── index.vue <br>
└── users.vue

## 生成路由： <br>

`[
{
"path": "/users",
"component": "/src/pages/users.vue",
"children": [
{
"path": "",
"component": "/src/pages/users/index.vue",
"name": "users"
},
{
"path": ":id",
"component": "/src/pages/users/[id].vue",
"name": "users-id"
}
]
}
]`
