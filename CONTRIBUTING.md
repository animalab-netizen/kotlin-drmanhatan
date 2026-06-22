# Contributing

## Principios

- manter a biblioteca desacoplada de providers especificos
- preservar API pequena, previsivel e facil de testar
- evitar dependencia de Android no nucleo
- preferir eventos imutaveis e composicao sobre heranca

## Fluxo sugerido

1. Abra uma issue descrevendo o problema ou proposta.
2. Mantenha mudancas pequenas e focadas.
3. Adicione ou atualize testes para comportamento publico.
4. Atualize o `README.md` quando a API mudar.
5. Registre mudancas relevantes no `CHANGELOG.md`.

## Escopo

Mudancas aceitas com maior probabilidade:

- novos enrichers genericos
- novas conveniencias de construcao de eventos
- melhorias de thread-safety
- adapters opcionais em modulos separados

Mudancas a evitar no nucleo:

- acoplamento direto com Firebase, Datadog, Sentry ou similares
- semantica dependente de Android
- reflection para descoberta ou instanciacao automatica

