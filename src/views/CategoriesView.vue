<template>
  <div class="categories-container">
    <h1>Управление категориями</h1>

    <!-- View Mode -->
    <div v-if="!isEditing" class="view-mode">
      <div class="controls">
        <input v-model="searchQuery" placeholder="Поиск по названию..." class="search-input">
        
        <button @click="startCreate" class="action-button primary">
          <i class="fas fa-plus"></i> Добавить категорию
        </button>
      </div>

      <!-- Categories List -->
      <div v-if="filteredCategories.length > 0" class="categories-list">
        <div 
          v-for="category in filteredCategories" 
          :key="category.id" 
          class="category-item" 
          @click="startEdit(category)"
        >
          <div class="category-details">
            <div class="category-name">{{ category.name }}</div>
            <div class="category-type">{{ category.type === 'INCOME' ? 'Доход' : 'Расход' }}</div>
          </div>
          <button @click.stop="confirmDelete(category)" class="delete-btn">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>Нет категорий для отображения</p>
        <button @click="startCreate" class="action-button">
          <i class="fas fa-plus"></i> Создать первую категорию
        </button>
      </div>
    </div>

    <!-- Edit/Create Mode -->
    <div v-else class="edit-mode">
      <h2>{{ editingCategory.id ? 'Редактирование категории' : 'Новая категория' }}</h2>

      <form @submit.prevent="submitForm" class="category-form">
        <div class="form-group">
          <label>Название категории</label>
          <input 
            v-model="editingCategory.name" 
            type="text" 
            required 
            placeholder="Например: Продукты"
          >
        </div>

        <div class="form-group">
          <label>Тип</label>
          <select v-model="editingCategory.type" required>
            <option value="EXPENSE">Расход</option>
            <option value="INCOME">Доход</option>
          </select>
        </div>

        <div class="form-actions">
          <button type="button" @click="cancelEdit" class="action-button secondary">
            Отмена
          </button>
          <button 
            v-if="editingCategory.id"
            type="button" 
            @click="confirmDelete(editingCategory)" 
            class="action-button danger"
          >
            Удалить
          </button>
          <button type="submit" class="action-button primary">
            {{ editingCategory.id ? 'Обновить' : 'Создать' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Подтверждение удаления</h3>
        <p>Вы уверены, что хотите удалить категорию "{{ categoryToDelete?.name }}"?</p>
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
  data() {
    return {
      categories: [],
      searchQuery: '',
      isEditing: false,
      editingCategory: this.createEmptyCategory(),
      showDeleteModal: false,
      categoryToDelete: null
    }
  },
  
  computed: {
    filteredCategories() {
      return this.categories.filter(cat => 
        cat.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      ).sort((a, b) => a.name.localeCompare(b.name));
    }
  },
  
  async created() {
    await this.loadCategories();
  },
  
  methods: {
    createEmptyCategory() {
      return {
        id: null,
        name: '',
        type: 'EXPENSE'
      };
    },
    
    async loadCategories() {
      try {
        const response = await fetch('http://localhost:8080/api/v1/categories');
        if (!response.ok) throw new Error('Ошибка загрузки категорий');
        this.categories = await response.json();
      } catch (err) {
        console.error('Ошибка загрузки категорий:', err);
        alert('Не удалось загрузить список категорий');
      }
    },
    
    async loadCategory(id) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/categories/${id}`);
        if (!response.ok) throw new Error('Ошибка загрузки категории');
        return await response.json();
      } catch (err) {
        console.error('Ошибка загрузки категории:', err);
        alert('Не удалось загрузить данные категории');
        return null;
      }
    },
    
    startCreate() {
      this.editingCategory = this.createEmptyCategory();
      this.isEditing = true;
    },
    
    async startEdit(category) {
      const fullCategory = await this.loadCategory(category.id);
      this.editingCategory = fullCategory || category;
      this.isEditing = true;
    },
    
    cancelEdit() {
      this.isEditing = false;
      this.editingCategory = this.createEmptyCategory();
    },
    
    async submitForm() {
      try {
        const url = this.editingCategory.id 
          ? `http://localhost:8080/api/v1/categories/${this.editingCategory.id}`
          : 'http://localhost:8080/api/v1/categories';
          
        const method = this.editingCategory.id ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.editingCategory)
        });
        
        if (!response.ok) {
          const error = await response.text();
          throw new Error(`Ошибка ${response.status}: ${error}`);
        }
        
        const data = await response.json();
        
        if (this.editingCategory.id) {
          // Update existing category
          this.categories = this.categories.map(cat => 
            cat.id === data.id ? data : cat
          );
        } else {
          // Add new category
          this.categories.push(data);
        }
        
        this.cancelEdit();
      } catch (err) {
        console.error('Ошибка сохранения:', err);
        alert('Не удалось сохранить категорию');
      }
    },

    confirmDelete(category) {
      this.categoryToDelete = category;
      this.showDeleteModal = true;
    },
    
    async confirmDeleteAction() {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/categories/${this.categoryToDelete.id}`, 
          { method: 'DELETE' }
        );
        
        if (!res.ok) throw new Error(res.statusText);
        
        this.categories = this.categories.filter(
          c => c.id !== this.categoryToDelete.id
        );
        
        this.showDeleteModal = false;
        this.categoryToDelete = null;
        
        // If we're deleting the currently edited category, exit edit mode
        if (this.editingCategory.id === this.categoryToDelete?.id) {
          this.cancelEdit();
        }
      } catch (err) {
        console.error('Ошибка удаления:', err);
        alert('Не удалось удалить категорию');
      }
    }
  }
}
</script>

<style scoped>
.categories-container {
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

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.category-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.category-details {
  flex-grow: 1;
}

.category-name {
  font-weight: 500;
  font-size: 1.1em;
}

.category-type {
  color: #666;
  font-size: 0.9em;
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

.category-form {
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

.form-group input, 
.form-group select {
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