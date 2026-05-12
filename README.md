# CRUD Generator com Framework XYZ

Este projeto esta organizado como um workspace Maven multi-modulo. O objetivo e manter o framework reutilizavel separado do gerador de CRUDs, sem copiar as classes do framework para dentro do modulo do gerador.

## 1. Estrutura do projeto

```text
projeto_oo/
├── pom.xml
├── framework/
│   ├── pom.xml
│   └── src/main/java/framework/
│       ├── dao/
│       │   ├── AbstractDao.java
│       │   ├── DBConnection.java
│       │   ├── DatabaseCreator.java
│       │   └── IDao.java
│       ├── exceptions/
│       ├── ui/
│       └── util/
│
├── crud-generator/
│   ├── pom.xml
│   └── src/main/java/generator/
│       ├── CrudGenerator.java
│       ├── GeneratorMain.java
│       ├── input/
│       ├── reader/
│       ├── model/
│       ├── writer/
│       ├── sample/
│       ├── example/
│       └── ui/
│
└── vetclinic-example/
    ├── pom.xml
    └── src/main/java/app/
```

### Modulos

#### `framework`

Modulo reutilizavel. Contem:

- `DBConnection`: singleton para abrir conexao JDBC;
- `DatabaseCreator`: utilitario para criar bancos SQLite a partir de comandos SQL;
- `AbstractDao<T, ID>` e `IDao<T, ID>`;
- excecoes do framework;
- utilitarios de console;
- componentes simples de UI de terminal (`Menu`, `Form`, `Field`, `Action`).

#### `crud-generator`

Modulo do gerador de CRUDs. Ele depende do `framework` via Maven:

```xml
<dependency>
    <groupId>br.com.xyz</groupId>
    <artifactId>framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

Esse modulo nao contem copia do framework. Ele apenas usa o framework como dependencia.

#### `vetclinic-example`

Modulo com a aplicacao de exemplo original do framework. Ele preserva a funcionalidade de abrir/criar um banco SQLite e criar as tabelas da clinica veterinaria usando `SchemaInitializer`.

Esse modulo tambem depende do `framework` via Maven.

## 2. Como abrir no VS Code

Abra a pasta raiz do projeto no VS Code:

```text
projeto_oo
```

Nao abra diretamente `crud-generator/`, porque o VS Code pode nao reconhecer o projeto multi-modulo inteiro.

A raiz correta contem:

```text
pom.xml
framework/
crud-generator/
vetclinic-example/
```

## 3. Requisitos

Verifique no terminal do VS Code:

```powershell
java -version
mvn -v
```

Use Java 17 ou superior.

## 4. Compilar o projeto inteiro

Na pasta raiz:

```powershell
mvn clean package
```

Esse comando compila os modulos nesta ordem:

```text
framework
crud-generator
vetclinic-example
```

O JAR principal do gerador sera criado em:

```text
crud-generator/target/crud-generator-2.0.0.jar
```

## 5. Executar o gerador no modo interativo

Na pasta raiz:

```powershell
java -jar .\crud-generator\target\crud-generator-2.0.0.jar
```

O menu possui estas opcoes:

```text
[1] Gerar CRUDs a partir de qualquer banco SQLite/JDBC
[2] Pre-visualizar tabelas de qualquer banco
[3] Criar banco SQLite de exemplo
[4] Executar exemplo DatabaseMetaData estilo JavaCodeGeeks
[5] Sobre o gerador
[0] Sair
```

## 6. Criar um banco de exemplo

A opcao 3 cria um banco SQLite de exemplo com as tabelas:

```text
owners
vets
pets
appointments
```

Essas tabelas possuem:

- chaves primarias;
- chaves estrangeiras;
- campos `TEXT`, `INTEGER` etc.;
- dados iniciais para teste.

No menu, escolha:

```text
3
```

Informe um caminho como:

```text
C:\crud-test\banco.db
```

Ou pressione ENTER para usar:

```text
./sample-data/vetclinic.db
```

Tambem e possivel criar pelo terminal, sem abrir o menu:

```powershell
java -jar .\crud-generator\target\crud-generator-2.0.0.jar --create-sample C:\crud-test\banco.db
```

Se nenhum caminho for informado:

```powershell
java -jar .\crud-generator\target\crud-generator-2.0.0.jar --create-sample
```

sera criado:

```text
sample-data/vetclinic.db
```

## 7. Pre-visualizar tabelas do banco

Depois de criar o banco de exemplo, escolha a opcao:

```text
2
```

Informe o caminho:

```text
C:\crud-test\banco.db
```

ou:

```text
.\sample-data\vetclinic.db
```

A pre-visualizacao mostra:

- nome da tabela;
- nome da classe que sera gerada;
- colunas;
- tipo SQL;
- tipo Java;
- chave primaria;
- chave estrangeira;
- nulidade.

## 8. Gerar os CRUDs

Escolha a opcao:

```text
1
```

Informe o caminho do banco:

```text
C:\crud-test\banco.db
```

Depois informe a pasta de saida:

```text
C:\crud-test\saida
```

Para cada tabela, o gerador cria:

```text
Entidade.java
EntidadeDao.java
EntidadeExemplo.java
```

Exemplo para a tabela `owners`:

```text
Owner.java
OwnerDao.java
OwnerExemplo.java
```

## 9. Executar em modo batch

Tambem e possivel gerar diretamente pelo terminal:

```powershell
java -jar .\crud-generator\target\crud-generator-2.0.0.jar C:\crud-test\banco.db C:\crud-test\saida
```

O primeiro argumento e o banco de dados.
O segundo argumento e a pasta de saida.

## 10. Exemplo DatabaseMetaData estilo JavaCodeGeeks

Foi adicionado ao projeto um exemplo didatico inspirado no artigo:

```text
https://examples.javacodegeeks.com/core-java/sql/jdbc-databasemetadata-example/
```

Classe adicionada:

```text
crud-generator/src/main/java/generator/example/JavaCodeGeeksMetadataExample.java
```

Ela demonstra o uso de:

```java
Connection connection = DriverManager.getConnection(jdbcUrl);
DatabaseMetaData metadata = connection.getMetaData();
metadata.getDatabaseProductName();
metadata.getDriverName();
metadata.getTables(null, null, "%", new String[] {"TABLE"});
metadata.getColumns(null, null, tableName, "%");
```

Para executar pelo menu, escolha:

```text
4
```

Informe um banco existente ou pressione ENTER para criar/usar automaticamente:

```text
./sample-data/vetclinic.db
```

Tambem e possivel executar pelo terminal:

```powershell
java -jar .\crud-generator\target\crud-generator-2.0.0.jar --metadata-example C:\crud-test\banco.db
```

Ou usando o banco de exemplo padrao:

```powershell
java -jar .\crud-generator\target\crud-generator-2.0.0.jar --metadata-example
```

## 11. Como o gerador le os metadados

O fluxo principal e:

```text
Banco SQLite ou URL JDBC
        ↓
JdbcDatabaseMetadataInputSource
        ↓
DatabaseMetaDataReader
        ↓
TableInfo / ColumnInfo
        ↓
EntityWriter / DaoWriter / ExampleWriter
        ↓
Arquivos Java gerados
```

A classe principal de leitura e:

```text
crud-generator/src/main/java/generator/reader/DatabaseMetaDataReader.java
```

Ela usa:

```java
connection.getMetaData();
metaData.getTables(...);
metaData.getColumns(...);
metaData.getPrimaryKeys(...);
metaData.getImportedKeys(...);
```

Com isso, o gerador consegue descobrir:

- tabelas;
- colunas;
- tipos SQL;
- tipos Java equivalentes;
- chaves primarias;
- chaves estrangeiras;
- colunas nullable;
- colunas autoincremento.

## 12. Ler qualquer outro banco SQLite

As opcoes 1, 2 e 4 aceitam qualquer caminho de banco SQLite existente:

```text
C:\meus-bancos\loja.db
D:\faculdade\trabalho\sistema.db
.\sample-data\vetclinic.db
```

Tambem aceitam URL JDBC completa:

```text
jdbc:sqlite:C:/crud-test/banco.db
```

Importante: se voce passar apenas um caminho de arquivo, o arquivo precisa existir. Para criar um banco de exemplo antes, use a opcao 3 ou o comando `--create-sample`.

## 13. Executar a aplicacao de exemplo do framework

O modulo `vetclinic-example` preserva a aplicacao de exemplo do framework.

Depois de compilar:

```powershell
mvn clean package
```

execute:

```powershell
java -jar .\vetclinic-example\target\vetclinic-example-1.0.0.jar
```

Essa aplicacao abre/cria o banco:

```text
vetclinic.db
```

e cria as tabelas usando:

```text
vetclinic-example/src/main/java/app/SchemaInitializer.java
```

## 14. Problemas comuns no VS Code

Se os imports ficarem vermelhos:

1. Pressione `Ctrl + Shift + P`.
2. Execute:

```text
Java: Clean Java Language Server Workspace
```

3. Reinicie o VS Code.
4. Execute:

```text
Java: Import Java Projects into Workspace
```

## 15. Problemas com caminhos no Windows

Evite testar inicialmente em caminhos com acento, como:

```text
Área de Trabalho
```

Prefira:

```text
C:\crud-test\banco.db
C:\crud-test\saida
```

Se necessario, antes de executar no PowerShell:

```powershell
chcp 65001
java -Dfile.encoding=UTF-8 -jar .\crud-generator\target\crud-generator-2.0.0.jar
```
