/**
 * API 基础配置与封装
 * 所有后端 RESTful 接口调用集中在此文件
 */
const API_BASE = 'http://localhost:8080/api';

/** 获取本地存储的 Token */
function getToken() {
    return localStorage.getItem('token');
}

/** 获取当前登录用户信息 */
function getUser() {
    const s = localStorage.getItem('user');
    return s ? JSON.parse(s) : null;
}

/** 保存登录信息 */
function saveAuth(data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data));
}

/** 退出登录 */
function logout() {
    const user = getUser();
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    location.href = user && user.role === 1 ? 'admin.html' : 'login.html';
}

/** 上传图片文件 */
async function uploadFile(file) {
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
        throw new Error('图片大小超过5MB限制，请选择更小的图片');
    }
    const formData = new FormData();
    formData.append('file', file);
    const headers = {};
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    const resp = await fetch(API_BASE + '/upload', { method: 'POST', headers, body: formData });
    const data = await resp.json();
    if (resp.status === 401) { logout(); throw new Error('登录已过期'); }
    if (data.code && data.code !== 200) throw new Error(data.message || '上传失败');
    return data.data;
}

/** 通用 Ajax 请求 */
async function request(url, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const resp = await fetch(API_BASE + url, { ...options, headers });
    const data = await resp.json();

    if (resp.status === 401) {
        logout();
        throw new Error('登录已过期');
    }
    if (data.code && data.code !== 200) {
        throw new Error(data.message || '请求失败');
    }
    return data;
}

/** GET 请求 */
function get(url) {
    return request(url);
}

/** POST 请求 */
function post(url, body) {
    return request(url, { method: 'POST', body: JSON.stringify(body) });
}

/** PUT 请求 */
function put(url, body) {
    return request(url, { method: 'PUT', body: JSON.stringify(body) });
}

// ========== 认证 API ==========
const AuthAPI = {
    register: (data) => post('/auth/register', data),
    login: (data) => post('/auth/login', data)
};

// ========== 用户 API ==========
const UserAPI = {
    profile: () => get('/users/profile'),
    updateProfile: (data) => put('/users/profile', data),
    getById: (id) => get('/users/' + id),
    pending: (page, size) => get(`/users/pending?page=${page}&size=${size}`),
    students: (page, size) => get(`/users/admin/students?page=${page}&size=${size}`),
    all: (page, size) => get(`/users/admin/list?page=${page}&size=${size}`),
    audit: (id, approved, reason) => post(`/users/${id}/audit`, { approved, reason }),
    deleteStudent: (id) => request(`/users/${id}/admin`, { method: 'DELETE' }),
    resetPassword: (id, newPassword) => post(`/users/${id}/reset-password`, { newPassword })
};

// ========== 物品 API ==========
const ItemAPI = {
    publish: (data) => post('/items', data),
    search: (params) => {
        const qs = new URLSearchParams(params).toString();
        return get('/items?' + qs);
    },
    detail: (id) => get('/items/' + id),
    update: (id, data) => put('/items/' + id, data),
    offShelf: (id) => post('/items/' + id + '/off-shelf', {}),
    pending: (page, size) => get(`/items/pending?page=${page}&size=${size}`),
    audit: (id, approved, reason) => post(`/items/${id}/audit`, { approved, reason })
};

// ========== 订单 API ==========
const OrderAPI = {
    create: (itemId) => post('/orders?itemId=' + itemId, {}),
    list: (page, size) => get(`/orders?page=${page}&size=${size}`),
    detail: (id) => get('/orders/' + id),
    updateStatus: (id, status) => post(`/orders/${id}/status?status=${status}`, {}),
    review: (data) => post('/orders/review', data),
    reviews: (id) => get('/orders/' + id + '/reviews')
};

// ========== 互助 API ==========
const HelpAPI = {
    publish: (data) => post('/help', data),
    update: (id, data) => put('/help/' + id, data),
    updateOrderNote: (id, orderNote) => put('/help/' + id + '/order-note', { orderNote }),
    list: (params) => {
        const qs = new URLSearchParams(params).toString();
        return get('/help?' + qs);
    },
    detail: (id) => get('/help/' + id),
    accept: (id) => post('/help/' + id + '/accept', {}),
    complete: (id) => post('/help/' + id + '/complete', {}),
    cancel: (id) => post('/help/' + id + '/cancel', {}),
    pending: (page, size) => get(`/help/pending?page=${page}&size=${size}`),
    audit: (id, approved, reason) => post(`/help/${id}/audit`, { approved, reason }),
    myOrders: (page, size) => get(`/help/my-orders?page=${page}&size=${size}`),
    getOrder: (id) => get('/help/' + id + '/order'),
    review: (orderId, data) => post('/help/order/' + orderId + '/review', data)
};

// ========== 通知 API ==========
const NotificationAPI = {
    list: (page, size) => get(`/notifications?page=${page}&size=${size}`),
    markRead: (id) => post('/notifications/' + id + '/read', {}),
    markAllRead: () => post('/notifications/read-all', {})
};

// ========== 举报 API ==========
const ReportAPI = {
    submit: (data) => post('/reports', data),
    detail: (id) => get('/reports/' + id),
    pending: (page, size) => get(`/reports/pending?page=${page}&size=${size}`),
    handle: (id, action) => post(`/reports/${id}/handle`, { action })
};

// ========== 私信 API ==========
const MessageAPI = {
    send: (data) => post('/messages', data),
    contacts: () => get('/messages/contacts'),
    conversation: (peerId, page, size) => get(`/messages/conversation/${peerId}?page=${page}&size=${size}`),
    unreadCount: () => get('/messages/unread-count'),
    readAll: () => post('/messages/read-all', {})
};

// ========== 工具函数 ==========
const STATUS_MAP = {
    user: { 0: '待审核', 1: '已通过', 2: '已驳回' },
    item: { 0: '待审核', 1: '已上架', 2: '已驳回', 3: '已售出', 4: '已下架' },
    order: { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' },
    /** 0待审核 1待接单 2进行中 3已完成 4已取消 5已下架/已驳回 */
    help: { 0: '待审核', 1: '待接单', 2: '进行中', 3: '已完成', 4: '已取消', 5: '已下架' }
};

const CATEGORIES = ['书籍', '电子产品', '生活用品', '服饰', '运动器材', '其他'];

function showMsg(el, msg, isError) {
    el.innerHTML = `<div class="msg-box ${isError ? 'msg-error' : 'msg-success'}">${msg}</div>`;
}

async function renderNav() {
    const user = getUser();
    const nav = document.getElementById('nav-links');
    if (!nav) return;
    let html = '<a href="index.html">首页</a>';
    if (!user || user.role !== 1) {
        html += '<a href="items.html">二手市场</a><a href="help.html">互助服务</a>';
    }
    if (user) {
        if (user.role !== 1) {
            html += `<a href="orders.html">我的订单</a>`;
        }
        let unreadCount = '';
        try {
            const res = await MessageAPI.unreadCount();
            if (res.data > 0) {
                unreadCount = `<span class="unread-badge">${res.data}</span>`;
            }
        } catch (e) {}
        html += `<a href="messages.html" id="msg-link">私信${unreadCount}</a><a href="profile.html">个人中心</a>`;
        if (user.role === 1) html += '<a href="admin-panel.html">管理后台</a>';
        html += `<a href="#" onclick="logout()">退出(${user.nickname})</a>`;
    } else {
        html += '<a href="login.html">登录</a><a href="register.html">注册</a>';
    }
    nav.innerHTML = html;
}

function requireLogin() {
    if (!getToken()) {
        location.href = 'login.html';
        return false;
    }
    return true;
}

function getFirstImage(images) {
    if (!images) return 'https://via.placeholder.com/300x200?text=No+Image';
    return images.split(',')[0];
}
