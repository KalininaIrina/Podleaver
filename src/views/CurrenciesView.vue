<template>
  <div>
    <h1>Currencies</h1>
    <button @click="getAll">🔍 Получить все</button>
    <button @click="getById">🔍 Получить по ID</button>
    <button @click="create">➕ Добавить</button>
    <button @click="update">✏️ Обновить</button>
    <button @click="remove">🗑 Удалить</button>
  </div>
</template>

<script>
export default {
  name: 'CurrenciesView',
  methods: {
    async getAll() {
      const res = await fetch('http://localhost:8000/api/v1/currency');
      const data = await res.json();
      console.log('Все валюты:', data);
    },
    async getById() {
      const id = 1;
      const res = await fetch(`http://localhost:8000/api/v1/currency/${id}`);
      const data = await res.json();
      console.log(`Валюта #${id}:`, data);
    },
    async create() {
      const newData = { name: 'USD', symbol: '$' };
      const res = await fetch('http://localhost:8000/api/v1/currency', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newData)
      });
      const data = await res.json();
      console.log('Создана валюта:', data);
    },
    async update() {
      const id = 1;
      const updateData = { name: 'EUR', symbol: '€' };
      const res = await fetch(`http://localhost:8000/api/v1/currency/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateData)
      });
      const data = await res.json();
      console.log('Обновлено:', data);
    },
    async remove() {
      const id = 1;
      const res = await fetch(`http://localhost:8000/api/v1/currency/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) console.log(`Удалена валюта #${id}`);
    }
  }
}
</script>
