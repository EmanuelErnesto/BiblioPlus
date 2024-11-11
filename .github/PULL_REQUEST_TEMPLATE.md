## Título do PR

- Descreva brevemente o que foi feito no PR.

---

## Descrição

**O que foi feito?**  
Explique de forma clara o que foi implementado ou modificado neste PR. Isso pode incluir novos recursos, correções de bugs ou refatoração de código.

**Por que foi feito?**  
Informe a motivação para a alteração. Quais problemas ou melhorias a mudança resolve?

---

## Checklist

Antes de submeter o PR, verifique se:

- [ ] O código está devidamente documentado.
- [ ] Todos os testes unitários/funcionais foram atualizados ou criados para cobrir as mudanças.
- [ ] Não há código comentado no repositório.
- [ ] O código foi formatado conforme o estilo de código do repositório (ex: linters, Prettier, etc.).
- [ ] Não há dependências ou pacotes desnecessários.
- [ ] O código foi testado localmente.
- [ ] A API foi testada (caso aplicável).
- [ ] O Changelog foi atualizado, caso necessário.
- [ ] O PR não contém informações sensíveis (como credenciais, tokens, etc.).

---

## Tipos de Mudanças

Marque as opções que se aplicam:

- [ ] Nova feature
- [ ] Correção de bug
- [ ] Refatoração
- [ ] Alteração na documentação
- [ ] Melhoria de performance
- [ ] Testes
- [ ] Outros (especificar): ___________

---

## Como testar

**Quais etapas podem ser seguidas para testar essa mudança?**

1. Explique os passos para testar localmente.
2. Informe os comandos de API que devem ser executados (se for o caso).
3. Se relevante, forneça exemplos de requisições HTTP (GET, POST, PUT, DELETE) e respostas esperadas.

Exemplo de requisição:
```bash
curl -X GET "http://api.exemplo.com/endpoint" -H "Authorization: Bearer <token>"
