# Fluxo de Trabalho Git

Este repositório segue um fluxo de trabalho baseado em branches para garantir organização, qualidade e segurança no desenvolvimento.

## Estrutura de Branches

- **`main`**: Branch principal que contém o código em produção.
- **`dev`**: Branch de desenvolvimento onde as features são integradas antes de irem para produção.
- **Branches de feature**: Branches criados para desenvolvimento de novas funcionalidades.
- **Branches de bugfix**: Branches criados para correção de bugs.
- **Branches de hotfix**: Branches criados para correções urgentes em produção.
- **Branches de release**: Branches criados para preparação de releases.

## Convenção de Nomes de Branches

- Para novas funcionalidades: `feature/nome-da-feature`
- Para correção de bugs: `bugfix/nome-do-bug`
- Para correções urgentes: `hotfix/nome-do-hotfix`
- Para releases: `release/x.y.z`

## Fluxo de Desenvolvimento

1. **Crie um novo branch a partir do `dev`**:
   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feature/nome-da-feature
   ```

2. **Desenvolva e faça commits frequentes**:
   ```bash
   git add .
   git commit -m "tipo: mensagem do commit"
   ```

3. **Mantenha seu branch atualizado com `dev`**:
   ```bash
   git checkout feature/nome-da-feature
   git pull --rebase origin dev
   ```

4. **Faça push do seu branch para o repositório remoto**:
   ```bash
   git push -u origin feature/nome-da-feature
   ```

5. **Abra um Pull Request (PR) para o branch `dev`**:
    - Acesse o repositório no GitHub
    - Clique em "Pull Requests" > "New Pull Request"
    - Selecione seu branch como origem e `dev` como destino
    - Preencha o título e descrição explicando o que foi feito
    - Solicite revisores

## Convenção de Commits

Utilizamos o padrão [Conventional Commits](https://www.conventionalcommits.org/) para padronização das mensagens de commit:

```
<tipo>[escopo opcional]: <descrição>
```

Tipos comuns:
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Alterações na documentação
- `style`: Formatação de código
- `refactor`: Refatoração de código
- `test`: Adição ou modificação de testes
- `chore`: Alterações em arquivos de build, configurações, etc.

Exemplos:
- `feat: adiciona autenticação de usuários`
- `fix: corrige cálculo de totais na página de checkout`
- `docs: atualiza instruções de instalação`

## Estratégia de Merge

- Pull Requests para o branch `dev` são mergeados usando a estratégia **squash merge**.
- Pull Requests para o branch `main` são mergeados usando a estratégia **merge commit**.

## Proteções e Regras

- Os branches `main` e `dev` estão protegidos contra push direto.
- Todo código deve ser integrado através de Pull Requests.
- Pull Requests requerem pelo menos 1 aprovação de revisão antes de serem mergeados.
- Revisores devem verificar qualidade, estilo de código e testabilidade.

## Lançamento de Versões

1. Crie um branch `release/x.y.z` a partir de `dev`.
2. Faça as correções finais e ajustes de versão neste branch.
3. Abra um PR do branch `release/x.y.z` para `main`.
4. Após aprovação e merge, crie uma tag de versão em `main`.
5. Faça merge do `main` de volta para o `dev`.
