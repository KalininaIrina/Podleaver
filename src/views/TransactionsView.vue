<template>
  <div class="transactions-container">
    <h1>Управление транзакциями</h1>

    <!-- Режимы просмотра/редактирования -->
    <div v-if="!isEditing" class="view-mode">
      <!-- Фильтры и поиск -->
      <div class="controls">
        <div class="filters">
          <input v-model="searchQuery" placeholder="Поиск..." class="search-input">
          <select v-model="selectedCategory" class="filter-select">
            <option value="">Все категории</option>
            <option v-for="cat in categories" :value="cat.id">{{ cat.name }}</option>
          </select>
          <select v-model="selectedAccount" class="filter-select">
            <option value="">Все счета</option>
            <option v-for="acc in accounts" :value="acc.id">{{ acc.name }}</option>
          </select>
        </div>

        <button @click="startCreate" class="action-button primary">
          <i class="fas fa-plus"></i> Добавить транзакцию
        </button>
      </div>

      <!-- Список транзакций -->
      <div v-if="filteredTransactions.length > 0" class="transactions-list">
        <div v-for="tx in filteredTransactions" :key="tx.id" class="transaction-item" @click="startEdit(tx)">
          <div class="tx-icon" :style="{ backgroundColor: tx.category.color }">
            <i :class="tx.category.icon"></i>
          </div>
          <div class="tx-details">
            <div class="tx-header">
              <span class="tx-category">{{ tx.category.name }}</span>
              <span class="tx-account">{{ tx.account.name }}</span>
            </div>
            <div class="tx-amount" :class="{ negative: tx.amount < 0, positive: tx.amount >= 0 }">
              {{ formatCurrency(tx.amount) }}
            </div>
            <div class="tx-date">{{ formatDate(tx.timestamp) }}</div>
          </div>
          <button @click.stop="confirmDelete(tx)" class="delete-btn">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>Нет транзакций для отображения</p>
        <button @click="startCreate" class="action-button">
          <i class="fas fa-plus"></i> Создать первую транзакцию
        </button>
      </div>
    </div>

    <!-- Режим редактирования/создания -->
    <div v-else class="edit-mode">
      <h2>{{ editingTransaction.id ? 'Редактирование' : 'Новая транзакция' }}</h2>

      <form @submit.prevent="submitForm" class="transaction-form">
        <div class="form-group">
          <label>Сумма</label>
          <input 
            v-model.number="editingTransaction.amount" 
            type="number" 
            step="0.01" 
            required 
            placeholder="0.00"
          >
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Категория</label>
            <select v-model="editingTransaction.category.id" required>
              <option value="" disabled>Выберите категорию</option>
              <option v-for="cat in categories" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>

          <div class="form-group">
            <label>Счёт</label>
            <select v-model="editingTransaction.account.id" required>
              <option value="" disabled>Выберите счёт</option>
              <option v-for="acc in accounts" :value="acc.id">{{ acc.name }}</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label>Дата</label>
          <input 
            v-model="editingTransaction.timestamp" 
            type="datetime-local" 
            required
          >
        </div>

        <div class="form-group">
          <label>Описание</label>
          <textarea 
            v-model="editingTransaction.description" 
            placeholder="Необязательное описание"
          ></textarea>
        </div>

        <div class="form-actions">
          <button type="button" @click="cancelEdit" class="action-button secondary">
            Отмена
          </button>
          <button 
            v-if="editingTransaction.id"
            type="button" 
            @click="confirmDelete(editingTransaction)" 
            class="action-button danger"
          >
            Удалить
          </button>
          <button type="submit" class="action-button primary">
            {{ editingTransaction.id ? 'Обновить' : 'Создать' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Модальное окно подтверждения удаления -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Подтверждение удаления</h3>
        <p>Вы уверены, что хотите удалить эту транзакцию?</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="action-button secondary">
            Отмена
          </button>
          <button @click="confirmDeleteAction" class="action-button danger">
            Удалить
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
  formattedTimestamp: {
    get() {
      if (!this.editingTransaction.timestamp) return '';
      const d = new Date(this.editingTransaction.timestamp);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      const hours = String(d.getHours()).padStart(2, '0');
      const minutes = String(d.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day}T${hours}:${minutes}`;
    },
    set(value) {
      this.editingTransaction.timestamp = value;
    }
  }
},

  data() {
    return {
      transactions: [],
      categories: [],
      accounts: [],
      searchQuery: '',
      selectedCategory: '',
      selectedAccount: '',
      isEditing: false,
      editingTransaction: this.createEmptyTransaction(),
      showDeleteModal: false,
      transactionToDelete: null
    }
  },
  
  computed: {
    filteredTransactions() {
      return this.transactions.filter(tx => {
        const matchesSearch = tx.description?.toLowerCase().includes(this.searchQuery.toLowerCase()) || 
                             tx.category.name.toLowerCase().includes(this.searchQuery.toLowerCase());
        const matchesCategory = !this.selectedCategory || tx.category.id == this.selectedCategory;
        const matchesAccount = !this.selectedAccount || tx.account.id == this.selectedAccount;
        
        return matchesSearch && matchesCategory && matchesAccount;
      }).sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
    }
  },
  
  async created() {
    await this.loadData();
  },
  
  methods: {
    createEmptyTransaction() {
      return {
        id: null,
        amount: 0,
        timestamp: new Date().toISOString().slice(0, 16),
        description: '',
        account: { id: '', name: '' },
        category: { id: '', name: '', icon: '', color: '' }
      };
    },
    
    async loadData() {
      try {
        const [txRes, catRes, accRes] = await Promise.all([
          fetch('http://localhost:8080/api/v1/transaction'),
          fetch('http://localhost:8080/api/v1/categories'),
          fetch('http://localhost:8080/api/v1/account')
        ]);
        
        this.transactions = await txRes.json();
        this.categories = await catRes.json();
        this.accounts = await accRes.json();
      } catch (err) {
        console.error('Ошибка загрузки данных:', err);
        alert('Не удалось загрузить данные');
      }
    },
    
    startCreate() {
      this.editingTransaction = this.createEmptyTransaction();
      this.isEditing = true;
    },
    
    startEdit(tx) {
      this.editingTransaction = {
        ...tx,
        timestamp: tx.timestamp.slice(0, 16) // Форматирование для datetime-local
      };
      this.isEditing = true;
    },
    
    cancelEdit() {
      this.isEditing = false;
      this.editingTransaction = this.createEmptyTransaction();
    },
    
    async submitForm() {
      try {
        console.log('Данные транзакции:', this.editingTransaction);
        console.log('ID категории:', this.editingTransaction.category.id);
        console.log('ID счёта:', this.editingTransaction.account.id);
        
        if (this.editingTransaction.category.id && this.editingTransaction.account.id) {
          this.saveTransaction();
        } else {
          console.error('Ошибка: категории или счёта не выбраны!');
        }
      } catch (err) {
        console.error('Ошибка сохранения:', err);
      }
    },
    
    async saveTransaction() {
      try {
        const response = await fetch('http://localhost:8080/api/v1/transaction', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            amount: this.editingTransaction.amount,
            timestamp: this.editingTransaction.timestamp,
            account: { id: this.editingTransaction.account.id },
            category: { id: this.editingTransaction.category.id },
            description: this.editingTransaction.description
          })
        });
        
        if (!response.ok) {
          const error = await response.text();
          throw new Error(`Ошибка ${response.status}: ${error}`);
        }
        
        const data = await response.json();
        console.log('Успешно сохранено:', data);
        this.transactions.push(data);
        this.cancelEdit();  // Закрываем форму после сохранения
      } catch (err) {
        console.error('Ошибка сохранения:', err);
      }
    },

    confirmDelete(tx) {
      this.transactionToDelete = tx;
      this.showDeleteModal = true;
    },
    
    async confirmDeleteAction() {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/transaction/${this.transactionToDelete.id}`, 
          { method: 'DELETE' }
        );
        
        if (!res.ok) throw new Error(res.statusText);
        
        this.transactions = this.transactions.filter(
          t => t.id !== this.transactionToDelete.id
        );
        
        this.showDeleteModal = false;
        this.transactionToDelete = null;
        
      } catch (err) {
        console.error('Ошибка удаления:', err);
        alert('Не удалось удалить транзакцию');
      }
    },
    
    formatDate(dateStr) {
      const options = { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      };
      return new Date(dateStr).toLocaleDateString('ru-RU', options);
    },
    
    formatCurrency(amount) {
      return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 2
      }).format(amount);
    }
  }
}
</script>

<style scoped>
.transactions-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.filters {
  display: flex;
  gap: 10px;
  flex-grow: 1;
}

.search-input, .filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.filter-select {
  min-width: 150px;
}

.action-button {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.action-button i {
  font-size: 14px;
}

.action-button.primary {
  background-color: #4CAF50;
  color: white;
}

.action-button.secondary {
  background-color: #f0f0f0;
  color: #333;
}

.action-button.danger {
  background-color: #f44336;
  color: white;
}

.action-button:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.transaction-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.tx-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
  flex-shrink: 0;
}

.tx-details {
  flex-grow: 1;
}

.tx-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.tx-category {
  font-weight: 500;
}

.tx-account {
  color: #666;
  font-size: 0.9em;
}

.tx-amount {
  font-weight: bold;
  font-size: 1.1em;
}

.tx-amount.positive {
  color: #4CAF50;
}

.tx-amount.negative {
  color: #f44336;
}

.tx-date {
  color: #666;
  font-size: 0.85em;
  margin-top: 2px;
}

.delete-btn {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 5px;
  margin-left: 10px;
}

.delete-btn:hover {
  color: #f44336;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.edit-mode {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.transaction-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-weight: 500;
  color: #555;
}

.form-group input, 
.form-group select, 
.form-group textarea {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>