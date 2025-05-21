<template>
  <div>
    <h1 class="accounts-title">Accounts</h1>
    <button @click="getAll">🔍 Получить все</button>
    <button @click="getById">🔍 Получить по ID</button>
    <button @click="create">➕ Добавить</button>
    <button @click="update">✏️ Обновить</button>
    <button @click="remove">🗑 Удалить</button>
  </div>
</template>

<script>
export default {
  name: 'AccountsView',
  methods: {
    async getAll() {
      const res = await fetch('http://localhost:8080/ape/v1/account');
      const data = await res.json();
      console.log('Все аккаунты:', data);
    },
    async getById() {
      const id = 1;
      const res = await fetch(`http://localhost:8080/ape/v1/account/${id}`);
      const data = await res.json();
      console.log(`Аккаунт #${id}:`, data);
    },
    async create() {
      const newData = { name: 'Новый аккаунт', balance: 1000 };
      const res = await fetch('http://localhost:8080/ape/v1/account', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newData)
      });
      const data = await res.json();
      console.log('Создан аккаунт:', data);
    },
    async update() {
      const id = 1;
      const updateData = { name: 'Обновлённый аккаунт', balance: 999 };
      const res = await fetch(`http://localhost:8080/ape/v1/account/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateData)
      });
      const data = await res.json();
      console.log('Обновлено:', data);
    },
    async remove() {
      const id = 1;
      const res = await fetch(`http://localhost:8080/ape/v1/account/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) console.log(`Удалён аккаунт #${id}`);
    }
  }
}
</script>

<style scoped>
.accounts-title {
  font-size: 28px;
  text-align: center;
  border: 2px solid orange;
  padding: 5px;
  border-radius: 8px;
}
</style>
