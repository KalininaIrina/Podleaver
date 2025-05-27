<template>
  <div class="accounts-container">
    <h1>Управление счетами</h1>

    <div v-if="!isEditing" class="view-mode">
      <div class="controls">
        <input
          v-model="searchQuery"
          placeholder="Поиск по названию..."
          class="search-input"
          type="search"
        />
        <button @click="startCreate" class="action-button primary">
          <i class="fas fa-plus"></i> Добавить счет
        </button>
      </div>

      <div v-if="filteredAccounts.length" class="accounts-list">
        <div
          v-for="account in filteredAccounts"
          :key="account.id"
          class="account-item"
          @click="startEdit(account)"
        >
          <div class="account-icon">
            <i class="fas fa-wallet"></i>
          </div>
          <div class="account-details">
            <div class="account-name">{{ account.name }}</div>
            <div class="account-balance">
              Баланс: {{ formatCurrency(account.startBalance) }}
            </div>
            <div class="account-currency" v-if="account.currency?.code">
              Валюта: {{ account.currency.code }}
            </div>
          </div>
          <button @click.stop="confirmDelete(account)" class="delete-btn">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>Нет счетов для отображения</p>
        <button @click="startCreate" class="action-button">
          <i class="fas fa-plus"></i> Создать первый счет
        </button>
      </div>
    </div>

    <div v-else class="edit-mode">
      <h2>{{ editingAccount.id ? 'Редактирование счета' : 'Новый счет' }}</h2>

      <form @submit.prevent="submitForm" class="account-form">
        <div class="form-group">
          <label for="accountName">Название счета</label>
          <input
            id="accountName"
            v-model="editingAccount.name"
            type="text"
            required
            placeholder="Название"
          />
        </div>

        <div class="form-group">
          <label for="startBalance">Начальный баланс</label>
          <input
            id="startBalance"
            v-model.number="editingAccount.startBalance"
            type="number"
            step="0.01"
            required
            placeholder="0.00"
          />
        </div>

        <div class="form-group">
          <label for="currencySelect">Валюта</label>
          <select
            id="currencySelect"
            v-model="editingAccount.currencyId"
            required
          >
            <option value="" disabled>Выберите валюту</option>
            <option
              v-for="currency in currencies"
              :key="currency.id"
              :value="currency.id"
            >
              {{ currency.code }} - {{ currency.name }}
            </option>
          </select>
        </div>

        <div class="form-actions">
          <button
            type="button"
            @click="cancelEdit"
            class="action-button secondary"
          >
            Отмена
          </button>
          <button
            v-if="editingAccount.id"
            type="button"
            @click="confirmDelete(editingAccount)"
            class="action-button danger"
          >
            Удалить
          </button>
          <button type="submit" class="action-button primary">
            {{ editingAccount.id ? 'Обновить' : 'Создать' }}
          </button>
        </div>
      </form>
    </div>

    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Подтверждение удаления</h3>
        <p>Вы уверены, что хотите удалить этот счет?</p>
        <div class="modal-actions">
          <button
            @click="showDeleteModal = false"
            class="action-button secondary"
          >
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
const BASE_URL = 'http://localhost:8080/api/v1/account';

export default {
  data() {
    return {
      accounts: [],
      currencies: [],
      searchQuery: '',
      isEditing: false,
      editingAccount: this.createEmptyAccount(),
      showDeleteModal: false,
      accountToDelete: null,
    };
  },

  computed: {
    filteredAccounts() {
      return this.accounts.filter((account) =>
        account.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
  },

  methods: {
    createEmptyAccount() {
      return {
        id: null,
        name: '',
        startBalance: '',
        currencyId: null,
        currency: null, // для отображения валюты в списке
      };
    },

    async loadCurrencies() {
      try {
        const res = await fetch('http://localhost:8080/api/v1/currencies');
        this.currencies = await res.json();
      } catch (e) {
        console.error('Ошибка загрузки валют:', e);
      }
    },

    async loadAccounts() {
      try {
        const res = await fetch(BASE_URL);
        this.accounts = await res.json();
      } catch (e) {
        console.error('Ошибка загрузки счетов:', e);
      }
    },

    async handleRequest(url, method, data) {
  try {
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: method !== 'GET' && method !== 'DELETE' ? JSON.stringify(data) : undefined
    });

    if (!response.ok) {
      // Если есть тело с ошибкой — парсим его, иначе кидаем по статусу
      let errorMessage = `HTTP error! status: ${response.status}`;
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch (_) {
        // тело пустое — ничего не делаем
      }
      throw new Error(errorMessage);
    }

    // Если статус 204 (No Content) или тело пустое — ничего не возвращаем
    if (response.status === 204) {
      return null;
    }

    // Пробуем распарсить JSON
    const text = await response.text();
    return text ? JSON.parse(text) : null;

  } catch (err) {
    console.error(`Ошибка ${method} запроса:`, err);
    throw err;
  }
},

    validateAccountInput() {
      if (!this.editingAccount.name.trim()) {
        alert('Введите название счета');
        return false;
      }
      if (isNaN(parseFloat(this.editingAccount.startBalance))) {
        alert('Введите корректную сумму');
        return false;
      }
      if (!this.editingAccount.currencyId) {
        alert('Выберите валюту');
        return false;
      }
      return true;
    },

    async createAccount() {
      if (!this.validateAccountInput()) return;

      // Формируем payload с вложенным объектом currency
      const accountData = {
        name: this.editingAccount.name,
        startBalance: parseFloat(this.editingAccount.startBalance),
        currency: { id: this.editingAccount.currencyId },
      };

      try {
        const newAccount = await this.handleRequest(BASE_URL, 'POST', accountData);
        this.accounts.push(newAccount);
        this.cancelEdit();
      } catch (err) {
        alert(`Ошибка: ${err.message}`);
      }
    },

    async updateAccount() {
      if (!this.validateAccountInput()) return;

      const accountData = {
        name: this.editingAccount.name,
        startBalance: parseFloat(this.editingAccount.startBalance),
        currency: { id: this.editingAccount.currencyId },
      };

      try {
        const updatedAccount = await this.handleRequest(
          `${BASE_URL}/${this.editingAccount.id}`,
          'PUT',
          accountData
        );

        const idx = this.accounts.findIndex((a) => a.id === updatedAccount.id);
        if (idx !== -1) this.accounts.splice(idx, 1, updatedAccount);
        this.cancelEdit();
      } catch (err) {
        alert(`Ошибка: ${err.message}`);
      }
    },

    async deleteAccount(accountId) {
      try {
        await this.handleRequest(`${BASE_URL}/${accountId}`, 'DELETE');
        this.accounts = this.accounts.filter((a) => a.id !== accountId);
        this.showDeleteModal = false;
        if (this.isEditing && this.editingAccount.id === accountId) {
          this.cancelEdit();
        }
      } catch (err) {
        alert(`Ошибка удаления: ${err.message}`);
      }
    },

    startCreate() {
      this.editingAccount = this.createEmptyAccount();
      this.isEditing = true;
    },

    startEdit(account) {
      this.editingAccount = {
        ...account,
        currencyId: account.currency?.id || null,
      };
      this.isEditing = true;
    },

    cancelEdit() {
      this.editingAccount = this.createEmptyAccount();
      this.isEditing = false;
    },

    submitForm() {
      if (this.editingAccount.id) {
        this.updateAccount();
      } else {
        this.createAccount();
      }
    },

    confirmDelete(account) {
      this.accountToDelete = account;
      this.showDeleteModal = true;
    },

    confirmDeleteAction() {
      if (this.accountToDelete?.id) {
        this.deleteAccount(this.accountToDelete.id);
      }
    },

    formatCurrency(value) {
      return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
      }).format(value);
    },
  },

  mounted() {
    this.loadAccounts();
    this.loadCurrencies();
  },
};
</script>

<style scoped>
.accounts-container {
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

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  flex-grow: 1;
  max-width: 300px;
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

.accounts-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.account-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.account-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.account-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2196F3;
  color: white;
  margin-right: 15px;
  flex-shrink: 0;
}

.account-details {
  flex-grow: 1;
}

.account-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: #666;
}

.account-balance {
  color: #666;
  font-size: 0.9em;
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
  color: #666;
}

.account-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
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

.form-group input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
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
