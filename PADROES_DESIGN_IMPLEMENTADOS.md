# Padrões de Design Implementados no Projeto Trolly

## Resumo dos Padrões Implementados

Este documento descreve os padrões de design implementados no projeto Trolly, melhorando a arquitetura e seguindo as melhores práticas de desenvolvimento Android.

## 1. Factory Pattern (Padrão Fábrica)

### Implementação:
- **`ListaCompraType`** - Sealed class com diferentes tipos de lista
- **`ListaCompraFactory`** - Interface para criação de listas
- **`ListaCompraFactoryImpl`** - Implementação da factory

### Benefícios:
- **Flexibilidade**: Criação de diferentes tipos de lista
- **Extensibilidade**: Fácil adição de novos tipos
- **Encapsulamento**: Lógica de criação isolada

### Uso:
```kotlin
// Criar lista com tipo específico
viewModel.createListaWithType(
    type = ListaCompraType.Weekly,
    nome = "Minha Lista Semanal",
    descricao = "Lista para a semana"
)

// Tipos disponíveis
ListaCompraType.Regular    // Lista Regular
ListaCompraType.Weekly     // Lista Semanal
ListaCompraType.Monthly    // Lista Mensal
ListaCompraType.Emergency  // Lista de Emergência
ListaCompraType.Recurrent  // Lista Recorrente
```

## 2. Strategy Pattern (Padrão Estratégia)

### Implementação:
- **`ItemSortingStrategy`** - Interface para estratégias de ordenação
- **Múltiplas estratégias** implementadas
- **`ItemSorter`** - Contexto que usa as estratégias

### Estratégias Disponíveis:
1. **`AlphabeticalSortStrategy`** - Ordenação alfabética
2. **`PriceSortStrategy`** - Ordenação por preço (menor para maior)
3. **`PriceDescendingSortStrategy`** - Ordenação por preço (maior para menor)
4. **`QuantitySortStrategy`** - Ordenação por quantidade
5. **`TotalPriceSortStrategy`** - Ordenação por preço total
6. **`StatusSortStrategy`** - Ordenação por status (comprado/não comprado)
7. **`CategorySortStrategy`** - Ordenação por categoria

### Uso:
```kotlin
// Carregar itens ordenados por preço
viewModel.carregarItensListaOrdenados(
    listaId = 1,
    strategy = PriceSortStrategy()
)

// Obter todas as estratégias disponíveis
val estrategias = viewModel.estrategiasOrdenacao.value
```

## 3. Builder Pattern (Padrão Construtor)

### Implementação:
- **`ListaCompraBuilder`** - Builder para construção complexa de listas
- **Métodos fluentes** para configuração
- **Valores padrão** automáticos

### Funcionalidades:
- Construção de listas com múltiplos itens
- Configuração de tipo, nome, descrição
- Adição de produtos e itens
- Cálculo automático de total estimado

### Uso:
```kotlin
// Criar lista complexa com builder
val builder = ListaCompraBuilder()
    .withTipo(ListaCompraType.Weekly)
    .withNome("Lista da Semana")
    .withDescricao("Compras essenciais")
    .addProduto(produto1, quantidade = 2)
    .addProduto(produto2, quantidade = 1)
    .addItem("Item personalizado", 3, "unidades", 5.99)

viewModel.createListaWithBuilder(builder)

// Usar builders pré-configurados
val weeklyBuilder = ListaCompraBuilder.createWeeklyLista()
val emergencyBuilder = ListaCompraBuilder.createEmergencyLista()
```

## 4. Estrutura de Arquivos Criada

```
app/src/main/java/com/lourenc/trolly/
├── domain/
│   ├── model/
│   │   └── ListaCompraType.kt           # Tipos de lista
│   ├── factory/
│   │   ├── ListaCompraFactory.kt        # Interface da factory
│   │   └── ListaCompraFactoryImpl.kt    # Implementação da factory
│   ├── strategy/
│   │   └── ItemSortingStrategy.kt       # Estratégias de ordenação
│   └── builder/
│       └── ListaCompraBuilder.kt        # Builder para listas
├── di/
│   └── AppModule.kt                     # Módulo atualizado
└── viewmodel/
    └── ListaCompraViewModel.kt          # ViewModel atualizado
```

## 5. Integração com a Arquitetura Existente

### Use Cases Atualizados:
- **`ListaCompraUseCase`** - Novos métodos para factory e builder
- **`ItemListaUseCase`** - Novos métodos para strategy pattern

### ViewModel Atualizado:
- **Novos LiveData** para estratégias e tipos
- **Métodos integrados** para usar os padrões
- **Tratamento de erros** mantido

### Injeção de Dependência:
- **`AppModule`** atualizado com novos providers
- **Configuração centralizada** de todos os padrões

## 6. Exemplos de Uso Prático

### Criando Lista com Factory:
```kotlin
// Na UI
viewModel.createListaWithType(
    type = ListaCompraType.Monthly,
    nome = "Compras de Janeiro",
    descricao = "Lista mensal de compras"
)
```

### Ordenando Itens com Strategy:
```kotlin
// Na UI
val strategy = AlphabeticalSortStrategy()
viewModel.carregarItensListaOrdenados(listaId, strategy)
```

### Construindo Lista Complexa com Builder:
```kotlin
// Na UI
val builder = ListaCompraBuilder()
    .withTipo(ListaCompraType.Emergency)
    .withNome("Lista de Emergência")
    .addProduto(produto1)
    .addProduto(produto2, quantidade = 3)
    .addItem("Item urgente", 1, "unidade", 15.99)

viewModel.createListaWithBuilder(builder)
```

## 7. Benefícios dos Padrões Implementados

### Factory Pattern:
- ✅ Criação flexível de diferentes tipos de lista
- ✅ Nomes e descrições automáticas baseadas no tipo
- ✅ Fácil extensão para novos tipos

### Strategy Pattern:
- ✅ Ordenação flexível de itens
- ✅ Fácil adição de novas estratégias
- ✅ Separação de responsabilidades

### Builder Pattern:
- ✅ Construção complexa de listas
- ✅ API fluente e legível
- ✅ Valores padrão automáticos

## 8. Próximos Passos Sugeridos

1. **Implementar Observer Pattern**: Para notificações de mudanças
2. **Adicionar Command Pattern**: Para operações desfazer/refazer
3. **Implementar Decorator Pattern**: Para funcionalidades adicionais
4. **Criar testes unitários**: Para todos os padrões implementados
5. **Adicionar validações**: Nos builders e factories
6. **Implementar cache**: Para estratégias de ordenação

## 9. Como Usar na Prática

### Configuração:
```kotlin
// No seu Activity ou Fragment
val listaDao = // obter DAO do banco
val itemDao = // obter DAO do banco

val listaRepository = AppModule.provideListaCompraRepository(listaDao)
val itemRepository = AppModule.provideItemListaRepository(itemDao)
val factory = AppModule.provideListaCompraFactory()

val listaUseCase = AppModule.provideListaCompraUseCase(listaRepository, itemRepository, factory)
val itemUseCase = AppModule.provideItemListaUseCase(itemRepository)

val viewModelFactory = AppModule.provideListaCompraViewModelFactory(listaUseCase, itemUseCase)
val viewModel: ListaCompraViewModel by viewModels { viewModelFactory }
```

### Uso na UI:
```kotlin
// Observar dados
val tiposLista by viewModel.tiposLista.observeAsState()
val estrategias by viewModel.estrategiasOrdenacao.observeAsState()

// Usar padrões
viewModel.createListaWithType(ListaCompraType.Weekly, "Minha Lista")
viewModel.carregarItensListaOrdenados(1, AlphabeticalSortStrategy())
```

Estes padrões de design tornam o código mais flexível, testável e manutenível, seguindo as melhores práticas de desenvolvimento Android e Clean Architecture. 