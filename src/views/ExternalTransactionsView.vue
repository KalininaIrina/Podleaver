<template>
  <div>
    <h1>External Transactions</h1>
    <button @click="getAll">🔍 Получить все</button>
    <button @click="getById">🔍 Получить по ID</button>
    <button @click="create">➕ Добавить</button>
    <button @click="update">✏️ Обновить</button>
    <button @click="remove">🗑 Удалить</button>
  </div>
</template>

<script>
export default {
  name: 'ExternalTransactionsView',
  methods: {
    async getAll() {
      const res = await fetch('http://localhost:8080/api/v1/externaltransaction');
      const data = await res.json();
      console.log('Все внешние транзакции:', data);
    },
    async getById() {
      const id = 1;
      const res = await fetch(`http://localhost:8080/api/v1/externaltransaction/${id}`);
      const data = await res.json();
      console.log(`Внешняя транзакция #${id}:`, data);
    },
    async create() {
      const newData = { name: 'Новая внешняя транзакция', amount: 1000 };
      const res = await fetch('http://localhost:8080/api/v1/externaltransaction', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newData)
      });
      const data = await res.json();
      console.log('Создано:', data);
    },
    async update() {
      const id = 1;
      const updateData = { name: 'Обновлено', amount: 888 };
      const res = await fetch(`http://localhost:8080/api/v1/externaltransaction/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateData)
      });
      const data = await res.json();
      console.log('Обновлено:', data);
    },
    async remove() {
      const id = 1;
      const res = await fetch(`http://localhost:8080/api/v1/externaltransaction/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) console.log(`Удалено #${id}`);
    }
  }
}
</script>
