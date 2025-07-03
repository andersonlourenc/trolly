# Implementação da Foto do Perfil

## Funcionalidades Implementadas

### 1. **Upload de Imagem**
- ✅ Seleção de imagem da galeria
- ✅ Upload para Firebase Storage
- ✅ Atualização automática do perfil do usuário

### 2. **Interface do Usuário**
- ✅ Ícone de lápis destacado na tela de edição
- ✅ Preview da imagem selecionada
- ✅ Indicador de carregamento durante upload
- ✅ Tratamento de erros com mensagens

### 3. **Arquitetura**
- ✅ Classe `ImageUploadManager` para gerenciar uploads
- ✅ Integração com Firebase Storage e Auth
- ✅ Tratamento assíncrono com Coroutines

## Como Funciona

### 1. **Seleção da Imagem**
- Usuário clica no ícone de lápis
- Abre seletor de imagens da galeria
- Preview imediato da imagem selecionada

### 2. **Upload e Salvamento**
- Ao clicar em "Salvar":
  - Upload da imagem para Firebase Storage
  - Atualização do perfil do usuário no Firebase Auth
  - Atualização do nome se modificado
  - Navegação de volta à tela de perfil

### 3. **Estrutura de Arquivos**
```
profile_images/
└── {userId}/
    └── {uuid}.jpg
```

## Dependências Adicionadas

```kotlin
// Firebase Storage
implementation("com.google.firebase:firebase-storage-ktx")

// Permissões no AndroidManifest.xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## Fluxo de Dados

1. **Seleção**: `ActivityResultContracts.GetContent()` → `Uri`
2. **Upload**: `Uri` → Firebase Storage → `String (URL)`
3. **Atualização**: `String (URL)` → Firebase Auth Profile
4. **Exibição**: Firebase Auth → ProfileScreen

## Tratamento de Erros

- ✅ Erro no upload da imagem
- ✅ Erro na atualização do perfil
- ✅ Erro geral de salvamento
- ✅ Indicadores visuais de carregamento

## Segurança

- ✅ Imagens organizadas por usuário
- ✅ Nomes únicos com UUID
- ✅ Validação de usuário autenticado
- ✅ Tratamento de exceções

A funcionalidade está completa e pronta para uso!

# Configuração do Firebase Storage para Fotos de Perfil

## Problema Identificado
O upload de fotos de perfil não está funcionando. Isso pode ser devido a:

1. **Regras do Firebase Storage não configuradas**
2. **Permissões de rede**
3. **Configuração do Firebase Storage**

## Soluções

### 1. Configurar Regras do Firebase Storage

Acesse o [Console do Firebase](https://console.firebase.google.com/project/trolly-e1755/storage/rules) e configure as seguintes regras:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Permitir acesso às imagens de perfil apenas para usuários autenticados
    match /profile_images/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Regra padrão - negar tudo
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

### 2. Verificar Configuração do Firebase Storage

1. No Console do Firebase, vá para **Storage**
2. Se não estiver habilitado, clique em **Get Started**
3. Escolha **Start in test mode** (para desenvolvimento)
4. Selecione a região mais próxima (ex: us-central1)

### 3. Verificar Dependências

Certifique-se de que as dependências do Firebase Storage estão no `build.gradle.kts`:

```kotlin
implementation("com.google.firebase:firebase-storage-ktx:20.0.0")
```

### 4. Testar Upload

Após configurar, teste o upload de foto novamente. Os logs detalhados ajudarão a identificar onde está o problema.

### 5. Logs para Debug

O app agora tem logs detalhados em:
- `ImageUploadManager.kt` - logs do upload
- `EditProfileScreen.kt` - logs da interface

### 6. Permissões

As permissões necessárias já estão no `AndroidManifest.xml`:
- `READ_EXTERNAL_STORAGE`
- `READ_MEDIA_IMAGES`
- `INTERNET`

## Próximos Passos

1. Configure as regras do Firebase Storage
2. Teste o upload novamente
3. Verifique os logs no Android Studio
4. Se ainda houver problemas, verifique se o Firebase Storage está habilitado no projeto 

# Documentação Técnica - Trolly App

## 1. Modelo de Dados

### 1.1 Entidades do Banco de Dados

#### **ListaCompra Entity**
```kotlin
@Entity(tableName = "lista_compra")
data class ListaCompra(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,                    // Nome da lista de compras
    val descricao: String = "",          // Descrição opcional
    val dataCriacao: Long = System.currentTimeMillis(), // Timestamp de criação
    val totalEstimado: Double = 0.0,     // Valor total estimado
    val fotoCapa: String? = null,        // URL da foto de capa (opcional)
    val status: String = "ATIVA"         // Status: "ATIVA" ou "CONCLUIDA"
)
```

**Campos e Propósitos:**
- `id`: Chave primária auto-incrementada
- `name`: Nome identificador da lista
- `descricao`: Descrição detalhada da lista
- `dataCriacao`: Data/hora de criação (timestamp)
- `totalEstimado`: Soma dos valores dos itens
- `fotoCapa`: Imagem representativa da lista
- `status`: Controle de estado da lista

#### **ItemLista Entity**
```kotlin
@Entity(
    tableName = "item_lista",
    foreignKeys = [ForeignKey(
        entity = ListaCompra::class,
        parentColumns = ["id"],
        childColumns = ["idLista"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idLista")]
)
data class ItemLista(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idLista: Int,                    // Chave estrangeira para ListaCompra
    val name: String,                    // Nome do produto
    val quantidade: Int,                 // Quantidade a comprar
    val unidade: String,                 // Unidade de medida (kg, L, un, etc.)
    val precoUnitario: Double,           // Preço por unidade
    val comprado: Boolean = false        // Status de compra
)
```

**Campos e Propósitos:**
- `id`: Chave primária auto-incrementada
- `idLista`: Relacionamento com ListaCompra
- `name`: Nome do produto/item
- `quantidade`: Quantidade desejada
- `unidade`: Unidade de medida
- `precoUnitario`: Preço por unidade
- `comprado`: Flag de status de compra

### 1.2 Relacionamentos

**Relacionamento 1:N (Um para Muitos):**
- Uma `ListaCompra` pode ter múltiplos `ItemLista`
- Um `ItemLista` pertence a uma única `ListaCompra`
- Configurado com `ForeignKey` e `CASCADE` para exclusão automática

### 1.3 Configuração do Banco de Dados

```kotlin
@Database(
    entities = [ListaCompra::class, ItemLista::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listaCompraDao(): ListaCompraDao
    abstract fun itemListaDao(): ItemListaDao
}
```

## 2. Código de Implementação

### 2.1 Camada de Acesso a Dados (DAO)

#### **ListaCompraDao**
```kotlin
@Dao
interface ListaCompraDao {
    @Insert
    suspend fun insert(lista: ListaCompra)

    @Update
    suspend fun update(lista: ListaCompra)

    @Delete
    suspend fun delete(lista: ListaCompra)

    @Query("SELECT * FROM lista_compra ORDER BY dataCriacao DESC")
    fun getAll(): Flow<List<ListaCompra>>

    @Query("SELECT * FROM lista_compra WHERE status = 'ATIVA' ORDER BY dataCriacao DESC")
    fun getListasAtivas(): Flow<List<ListaCompra>>

    @Query("SELECT * FROM lista_compra WHERE status = 'CONCLUIDA' ORDER BY dataCriacao DESC")
    fun getListasConcluidas(): Flow<List<ListaCompra>>

    @Query("SELECT * FROM lista_compra WHERE id = :id")
    suspend fun getListaById(id: Int): ListaCompra?

    @Query("UPDATE lista_compra SET status = :status WHERE id = :listaId")
    suspend fun updateStatus(listaId: Int, status: String)
}
```

#### **ItemListaDao**
```kotlin
@Dao
interface ItemListaDao {
    @Insert
    suspend fun inserir(item: ItemLista)

    @Update
    suspend fun atualizar(item: ItemLista)

    @Delete
    suspend fun deletar(item: ItemLista)

    @Query("SELECT * FROM item_lista WHERE idLista = :idLista")
    suspend fun getItensPorLista(idLista: Int): List<ItemLista>
    
    @Query("SELECT * FROM item_lista WHERE idLista = :idLista AND name = :nome LIMIT 1")
    suspend fun getItemPorNome(idLista: Int, nome: String): ItemLista?
}
```

### 2.2 Camada de Repositório

#### **ListaCompraRepositoryImpl**
```kotlin
class ListaCompraRepositoryImpl(private val listaDao: ListaCompraDao) : ListaCompraRepository {

    override suspend fun insertLista(lista: ListaCompra) {
        try {
            listaDao.insert(lista)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getAllListas(): Flow<List<ListaCompra>> {
        return listaDao.getAll()
    }

    override fun getListasAtivas(): Flow<List<ListaCompra>> {
        return listaDao.getListasAtivas()
    }

    override fun getListasConcluidas(): Flow<List<ListaCompra>> {
        return listaDao.getListasConcluidas()
    }

    override suspend fun updateStatus(listaId: Int, status: String) {
        listaDao.updateStatus(listaId, status)
    }
}
```

### 2.3 Camada de Domínio (Use Cases)

#### **ListaCompraUseCaseImpl**
```kotlin
class ListaCompraUseCaseImpl(
    private val listaRepository: ListaCompraRepository,
    private val itemRepository: ItemListaRepository,
    private val factory: ListaCompraFactory = ListaCompraFactoryImpl()
) : ListaCompraUseCase {

    override suspend fun createLista(lista: ListaCompra): ListaCompraResult {
        return try {
            listaRepository.insertLista(lista)
            ListaCompraResult.ListaSuccess(lista)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao criar lista: ${e.message}", e)
        }
    }

    override suspend fun updateStatus(listaId: Int, status: String): ListaCompraResult {
        return try {
            listaRepository.updateStatus(listaId, status)
            ListaCompraResult.Success("Status atualizado com sucesso")
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao atualizar status: ${e.message}", e)
        }
    }

    override suspend fun calcularGastoMensal(month: Int, year: Int): ListaCompraResult {
        return try {
            val listas = listaRepository.getListasConcluidas().first()
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1, 0, 0, 0)
            val startDate = calendar.timeInMillis
            
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.MILLISECOND, -1)
            val endDate = calendar.timeInMillis
            
            val listasConcluidasDoMes = listas.filter { lista ->
                val dataCriacao = Calendar.getInstance().apply { timeInMillis = lista.dataCriacao }
                val mesCriacao = dataCriacao.get(Calendar.MONTH)
                val anoCriacao = dataCriacao.get(Calendar.YEAR)
                mesCriacao == month && anoCriacao == year
            }
            
            var gastoTotal = 0.0
            for (lista in listasConcluidasDoMes) {
                val total = itemRepository.calcularTotalLista(lista.id)
                gastoTotal += total
            }
            
            ListaCompraResult.GastoMensalSuccess(gastoTotal)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao calcular gasto mensal: ${e.message}", e)
        }
    }
}
```

### 2.4 Camada de Apresentação (ViewModel)

#### **ListaCompraViewModel**
```kotlin
class ListaCompraViewModel(
    private val listaUseCase: ListaCompraUseCase,
    private val itemUseCase: ItemListaUseCase
) : ViewModel() {

    // LiveData para observação de mudanças
    private val _listasAtivas = MutableLiveData<List<ListaCompra>>(emptyList())
    val listasAtivas: LiveData<List<ListaCompra>> = _listasAtivas
    
    private val _listasConcluidas = MutableLiveData<List<ListaCompra>>(emptyList())
    val listasConcluidas: LiveData<List<ListaCompra>> = _listasConcluidas
    
    private val _itensLista = MutableLiveData<List<ItemLista>>(emptyList())
    val itensLista: LiveData<List<ItemLista>> = _itensLista
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Métodos para gerenciar listas
    fun addLista(lista: ListaCompra) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.createLista(lista)) {
                is ListaCompraResult.ListaSuccess -> {
                    carregarListasAtivas()
                    carregarListasConcluidas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }

    fun marcarListaComoConcluida(listaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.updateStatus(listaId, "CONCLUIDA")) {
                is ListaCompraResult.Success -> {
                    carregarListasAtivas()
                    carregarListasConcluidas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }

    fun carregarItensLista(listaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = itemUseCase.getItensPorLista(listaId)) {
                is ItemListaResult.ItensSuccess -> {
                    _itensLista.value = result.itens
                }
                is ItemListaResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }
}
```

## 3. Funcionalidades Implementadas

### 3.1 Gestão de Listas de Compras

#### **✅ Criar Lista de Compras**
- **Implementação:** `ListaCompraUseCase.createLista()`
- **Interface:** Tela de criação com campos nome e descrição
- **Validação:** Nome obrigatório, descrição opcional
- **Persistência:** Salva automaticamente no banco local

#### **✅ Visualizar Listas**
- **Implementação:** `ListaCompraDao.getAll()`, `getListasAtivas()`, `getListasConcluidas()`
- **Interface:** Tela com abas para listas ativas e concluídas
- **Ordenação:** Por data de criação (mais recente primeiro)
- **Informações:** Nome, data, total estimado, status

#### **✅ Editar Lista**
- **Implementação:** `ListaCompraUseCase.updateLista()`
- **Interface:** Modal de edição inline
- **Campos editáveis:** Nome e descrição
- **Feedback:** Confirmação visual de alterações

#### **✅ Marcar como Concluída**
- **Implementação:** `ListaCompraUseCase.updateStatus()`
- **Interface:** Botão "Concluir Lista" na tela de detalhes
- **Lógica:** Muda status de "ATIVA" para "CONCLUIDA"
- **Atualização:** Lista aparece na aba de concluídas

#### **✅ Excluir Lista**
- **Implementação:** `ListaCompraUseCase.deleteLista()`
- **Interface:** Swipe-to-delete ou botão de exclusão
- **Confirmação:** Dialog de confirmação antes da exclusão
- **Cascade:** Remove automaticamente todos os itens

### 3.2 Gestão de Itens da Lista

#### **✅ Adicionar Produto**
- **Implementação:** `ItemListaUseCase.adicionarOuIncrementarItem()`
- **Interface:** Tela de busca de produtos com sugestões
- **Funcionalidades:** Busca por nome, produtos predefinidos
- **Campos:** Nome, quantidade, unidade, preço unitário

#### **✅ Editar Item**
- **Implementação:** `ItemListaUseCase.atualizarItem()`
- **Interface:** Modal de edição com campos editáveis
- **Campos:** Quantidade, preço unitário, unidade
- **Cálculo:** Total da lista atualizado automaticamente

#### **✅ Marcar como Comprado**
- **Implementação:** `ItemListaUseCase.marcarItemComoComprado()`
- **Interface:** Checkbox em cada item da lista
- **Visual:** Item riscado quando marcado
- **Progresso:** Barra de progresso da lista

#### **✅ Remover Item**
- **Implementação:** `ItemListaUseCase.removerItem()`
- **Interface:** Botão de exclusão em cada item
- **Confirmação:** Dialog de confirmação
- **Atualização:** Total recalculado automaticamente

#### **✅ Incremento Automático**
- **Implementação:** Lógica no `ItemListaUseCase.adicionarOuIncrementarItem()`
- **Funcionalidade:** Detecta produtos duplicados
- **Ação:** Incrementa quantidade automaticamente
- **Feedback:** Notificação de incremento

### 3.3 Funcionalidades Avançadas

#### **✅ Cálculo Automático de Totais**
- **Implementação:** `ItemListaRepository.calcularTotalLista()`
- **Lógica:** Soma (quantidade × preço unitário) de todos os itens
- **Atualização:** Em tempo real ao modificar itens
- **Exibição:** Formatação em moeda brasileira

#### **✅ Filtros e Busca**
- **Implementação:** `ItemListaRepository.getProdutosPredefinidos()`
- **Funcionalidade:** Lista de produtos comuns
- **Busca:** Filtro por nome em tempo real
- **Sugestões:** Produtos mais utilizados

#### **✅ Estatísticas Básicas**
- **Implementação:** `ListaCompraUseCase.calcularGastoMensal()`
- **Funcionalidade:** Cálculo de gastos por mês
- **Filtro:** Apenas listas concluídas
- **Exibição:** Valor total gasto no período

## 4. Arquitetura e Padrões

### 4.1 Clean Architecture
- **Data Layer:** DAOs, Repositories, Entities
- **Domain Layer:** Use Cases, Models, Results
- **Presentation Layer:** ViewModels, UI Screens

### 4.2 Padrões de Design
- **Repository Pattern:** Abstração do acesso a dados
- **Use Case Pattern:** Regras de negócio isoladas
- **MVVM:** Separação de responsabilidades na UI
- **Builder Pattern:** Construção complexa de objetos
- **Factory Pattern:** Criação de objetos especializados

### 4.3 Tecnologias Utilizadas
- **Room Database:** Persistência local
- **Jetpack Compose:** UI declarativa
- **LiveData:** Observação de mudanças
- **Coroutines:** Programação assíncrona
- **Hilt:** Injeção de dependências

## 5. Fluxo de Dados

### 5.1 Criação de Lista 