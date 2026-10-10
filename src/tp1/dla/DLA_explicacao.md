# DLA (Diffusion-Limited Aggregation): explicação simples

Código: [Walker.java](Walker.java) (uma partícula) e [DLA.java](DLA.java) (a simulação).

---

## 1. O que é o DLA

**DLA = agregação limitada por difusão.** É um modelo de crescimento:

1. Existem partículas **paradas** (sementes) que formam o agregado inicial.
2. Existem partículas **em movimento** que andam ao acaso.
3. Quando uma partícula em movimento **toca** numa parada, pára também e passa a fazer parte do agregado.
4. Repete-se milhares de vezes e o agregado cresce em **ramos**.

- Foi proposto por **Witten e Sander em 1981**.
- O resultado chama-se **árvore browniana**. É um **fractal**: os ramos têm ramos, que têm ramos, e assim por diante.
- Em 2D a **dimensão fractal é cerca de 1,71**. Fica entre uma linha (dimensão 1) e uma superfície cheia (dimensão 2), porque o agregado ocupa mais do que uma linha mas deixa muitos buracos.

### Porque se formam ramos (a ideia mais importante)

Uma partícula que vem de fora, a andar ao acaso, tem muito mais probabilidade de bater primeiro numa **ponta saliente** do que de entrar num "vale" entre dois ramos sem tocar em nada. As pontas crescem mais depressa e tapam ainda mais o interior. É um ciclo que se reforça a si próprio, e o resultado são ramos finos e abertos.

### Onde aparece na natureza

- cristais e depósitos minerais (dendrites);
- eletrodeposição (metal a crescer num elétrodo);
- figuras de Lichtenberg (as marcas de um relâmpago ou de uma descarga elétrica);
- corais, líquenes, flocos de neve (formas parecidas).

---

## 2. Matéria à volta

### 2.1 Passeio aleatório (movimento browniano)

Em cada passo a partícula anda uma distância fixa numa **direção ao acaso**. Não tem memória, e o passo seguinte não depende do anterior. Isto imita o **movimento browniano**: o movimento de uma partícula num líquido, empurrada ao acaso pelas moléculas à volta. É a "difusão" do nome DLA.

No código, isto é uma linha só: `pos.add(PVector.random2D())`.

### 2.2 Stickiness (aderência)

É a **probabilidade de uma partícula parar quando toca no agregado**.

| Stickiness | O que acontece | Aspeto |
|---|---|---|
| 1 (100%) | pára sempre ao primeiro toque | ramos **finos e abertos**, crescimento rápido |
| ~0,1 a 0,2 | muitas vezes "falha" e continua a andar | ramos **mais grossos**, agregado mais denso |
| ~0,01 | quase sempre falha | agregado **compacto e "peludo"**, cresce devagar |

**Porquê:** com stickiness baixa, a partícula pode tocar numa ponta, não parar e continuar a andar para dentro dos vales entre ramos. Os vales deixam de ficar vazios e o agregado fica mais cheio.

> No relatório: corre com 1, 0,5, 0,1 e 0,02 (com a mesma forma), tira um print de cada e compara. Confirma tu o que vês.

### 2.3 Formas iniciais (inicializações)

A forma das sementes muda o resultado:

- **Ponto:** o DLA clássico, uma "árvore" que cresce para todos os lados.
- **Linha:** cresce para cima e para baixo da linha, parecido com uma floresta ou com musgo.
- **Círculo:** cresce para dentro e para fora do anel.
- **Quadrado:** igual ao círculo, mas a partir de 4 lados retos.

### 2.4 Ligação aos autómatos celulares (otimização que não foi feita)

No nosso código cada partícula em movimento compara a distância com **todas** as outras. Quantas mais partículas paradas há, mais lento fica.

O enunciado sugere pôr as partículas numa **grelha 2D**, como no Jogo da Vida. Para saber se tocou, bastaria olhar para as **8 células vizinhas (vizinhança de Moore)** em vez de para a lista toda. Com isso o custo deixa de crescer com o tamanho do agregado. Este facultativo **não foi feito**, mas convém saber explicá-lo.

---

## 3. O enunciado e o que foi feito

| Ponto do enunciado | Feito? | Onde |
|---|---|---|
| Número de partículas em movimento sempre constante | Sim | `DLA.step()`: quando uma pára, cria-se logo outra |
| Cor das partículas com um critério | Sim | `Walker.setState()`: cor pela **ordem de paragem** |
| Inicializações: linha, círculo, quadrado, GUI | Sim | `DLA.createSeeds()` + `mousePressed()` |
| Grelha 2D para otimizar | Não | (ver 2.4) |
| Stickiness variável e comentar resultados | Sim (falta comentar) | `Walker.updateState()` + teclas `+` e `-` |
| Ensaio sobre o DLA | Não | — |

---

## 4. Walker.java: uma partícula

### Estados

```java
public enum State { STOPPED, WANDER }
```
- `WANDER`: está a vaguear.
- `STOPPED`: está parada e faz parte do agregado.

Um `enum` é um tipo com valores fixos. É mais claro do que um `boolean`.

### Atributos

| Atributo | Para que serve |
|---|---|
| `RADIUS = 2` | Raio de todas as partículas. `static final` porque é igual para todas e não muda. |
| `num_wanders`, `num_stopped` | Contadores **partilhados por todas** as partículas (`static`). Mostram quantas há em cada estado. |
| `pos` | Posição (x, y). |
| `state` | Estado atual. |
| `colour` | Cor (no Processing as cores são `int`). |

> `static` significa "um para a classe toda" e não um por partícula. Por isso lê-se `Walker.num_stopped`.

### Construtores

```java
new Walker(p)                       // em movimento, numa posição ao acaso da janela
new Walker(p, new PVector(x, y))    // parada nessa posição (semente)
```

As partículas nascem em **qualquer sítio da janela**. Na aula nasciam num círculo à volta do centro, mas isso não funciona com as outras formas (por exemplo uma linha ou um círculo grande), porque aí o agregado não está só no centro.

### `setState(p, state)`: o único sítio onde o estado muda

- Se a partícula estava a vaguear, desconta 1 a `num_wanders`.
- Se fica **parada**, recebe a cor e soma 1 a `num_stopped`.
- Se fica **em movimento**, fica branca e soma 1 a `num_wanders`.

Ter tudo num só método garante que **os contadores e a cor estão sempre certos**.

**Critério de cor:**
```java
p.color((num_stopped * 0.5f) % 360, 80, 100)
```
- O DLA usa o modo **HSB** (matiz, saturação, brilho). O 1.º valor é o **ângulo no círculo de cores**, de 0 a 360°.
- Cada partícula que pára fica 0,5° à frente da anterior, e o `% 360` faz dar a volta.
- Resultado: **anéis de cor que mostram a idade** do agregado, ou seja, por que ordem cresceu.
- As partículas em movimento são brancas: `color(0, 0, 100, 100)`, um pouco transparentes.

### `updateState(p, walkers, stickiness)`: tocou?

```java
for (Walker w : walkers)
    if (w.state == STOPPED && PVector.dist(pos, w.pos) < 2 * RADIUS) {
        if (p.random(1) < stickiness) setState(p, STOPPED);
        return;
    }
```
- **Tocar** quer dizer que a distância entre os centros é menor do que a soma dos raios (`2 * RADIUS`).
- Só conta tocar em partículas **paradas**.
- `p.random(1)` dá um número entre 0 e 1. Se for menor do que a stickiness, pára. Com 1, pára sempre.
- `return` sai logo, porque não é preciso ver as outras.

### `wander(p)`: um passo ao acaso

```java
pos.add(PVector.random2D());                    // passo de tamanho 1, direção ao acaso
pos.x = PApplet.constrain(pos.x, 0, p.width);   // não sai da janela
pos.y = PApplet.constrain(pos.y, 0, p.height);
```

### `display(p)` e `getDiameter()`

- `display` desenha um círculo com a cor da partícula.
- `getDiameter` devolve `2 * RADIUS`, para o DLA espaçar as sementes.

---

## 5. DLA.java: a simulação

### Constantes e atributos

| Nome | Valor | Significado |
|---|---|---|
| `NUM_WALKERS` | 200 | Partículas **sempre** em movimento. |
| `STEP_TIME` | 0,001 s | Um passo a cada milésimo de segundo (1000 passos/s). |
| `walkers` | lista | **Todas** as partículas, paradas e em movimento. |
| `shape` | `POINT` | Forma inicial atual (`POINT`, `LINE`, `CIRCLE`, `SQUARE`). |
| `stickiness` | 1 | Probabilidade de parar ao tocar. |
| `timer` | | Acumula tempo real para saber quantos passos dar. |

### `setup` e `restart`

- `setup`: põe as cores em HSB (`colorMode(HSB, 360, 100, 100)`) e chama `restart`.
- `restart`: cria uma lista nova, põe os contadores a 0, cria as sementes e cria 200 partículas em movimento.

O `restart` é chamado no início, na tecla `r` e ao mudar de forma, por isso está num método à parte.

> Os contadores `static` **têm de ser postos a 0** no `restart`. Como são `static`, não "morrem" quando a simulação recomeça.

### `createSeeds`: as formas

Todas as formas são **filas de sementes encostadas**, à distância de um diâmetro (`d`) umas das outras, para não haver buracos. `(cx, cy)` é o centro da janela.

- **POINT:** uma semente no centro.
- **LINE:** sementes de `x = 0` até à largura da janela, todas com `y = cy`. É uma **linha horizontal a meio do ecrã** e o agregado cresce para cima e para baixo.
- **CIRCLE:** circunferência de raio 200.
  - O perímetro é `2πr`, por isso cabem `n = 2πr / d` sementes.
  - A semente `i` fica no ângulo `a = 2π·i/n`, na posição `(cx + r·cos a, cy + r·sin a)`. São coordenadas polares convertidas para x e y.
- **SQUARE:** quadrado com metade do lado igual a 150. Para cada `t` entre −150 e 150 põe uma semente em cada um dos 4 lados.

### `addSeed` e `mousePressed`

- `addSeed(p, x, y)` cria uma partícula **parada** em (x, y).
- **Clicar** com o rato chama `addSeed` na posição do rato. Vários cliques desenham uma **forma arbitrária**, que é a parte "definida através da GUI" do enunciado.

### `draw`: o tempo com dt

```java
timer += Math.min(dt, 0.25f);
while (timer >= STEP_TIME) { timer -= STEP_TIME; step(p); }
```
- O tempo conta-se em **segundos reais (dt)**, não em frames. É o mesmo padrão do Jogo da Vida.
- Se um frame demorou 16 ms, dão-se cerca de 16 passos. A simulação anda ao mesmo ritmo em qualquer computador.
- `Math.min(dt, 0.25f)` evita milhares de passos de uma vez se a janela bloquear.
- No fim desenha o fundo preto, as partículas e o texto com a forma, a stickiness e os contadores.

### `step`: um passo (o coração do código)

```java
for (int i = 0; i < walkers.size(); i++) {
    Walker w = walkers.get(i);
    if (w.getState() == WANDER) {
        w.wander(p);                            // 1. anda
        w.updateState(p, walkers, stickiness);  // 2. vê se tocou
        if (w.getState() == STOPPED)            // 3. se parou...
            walkers.add(new Walker(p));         //    ...cria logo outra
    }
}
```
- **Número constante:** quem pára é substituído **no mesmo instante**, por isso há sempre 200 em movimento.
- **Porquê `for` com índice e não `for (Walker w : walkers)`:** acrescentar à lista durante um for-each dá `ConcurrentModificationException`. Com índice, a lista pode crescer à vontade.

### Teclas

| Tecla | Efeito |
|---|---|
| `1` / `2` / `3` / `4` | Ponto / linha / círculo / quadrado (recomeça) |
| `r` | Recomeça com a mesma forma |
| `+` | Stickiness ×2 (máx. 1) |
| `-` | Stickiness ÷2 (mín. 0,01) |
| clique | Acrescenta uma semente |

A stickiness muda a multiplicar e a dividir por 2 para chegar depressa aos valores pequenos: 1 → 0,5 → 0,25 → 0,13 → 0,06 → 0,03 → 0,02 → 0,01.

---

## 6. Fluxo do programa

```
ProcessingSetup.main → app = new DLA()
  setup()  → cores HSB → restart → sementes + 200 partículas em movimento
  draw()   (cada frame)
     timer += dt → vários step()
        cada partícula em movimento: anda → tocou? → se parou, nasce outra
     desenha tudo + texto
  tecla / clique → muda forma, stickiness, recomeça ou acrescenta semente
```

---

## 7. Perguntas prováveis e respostas curtas

1. **Como garantes que o número de partículas em movimento é constante?**
   Quando uma pára, crio logo outra no mesmo passo (`walkers.add(new Walker(p))` em `step`). O ecrã mostra sempre "em movimento: 200".
2. **Qual é o critério de cor?**
   A ordem de paragem. O ângulo HSB aumenta 0,5° por cada partícula parada, e os anéis de cor mostram como o agregado cresceu.
3. **O que é a stickiness e que efeito tem?**
   É a probabilidade de parar ao tocar. Com valor baixo as partículas entram nos vales entre ramos e o agregado fica mais denso e compacto. Com 1 fica com ramos finos e abertos.
4. **Porque se formam ramos?**
   As pontas apanham as partículas antes de elas chegarem ao interior, por isso as pontas crescem mais depressa.
5. **Porque nascem as partículas em qualquer sítio?**
   Porque com várias formas iniciais o agregado não está só no centro.
6. **Porque fica lento ao fim de algum tempo?**
   Cada partícula em movimento compara a distância com todas as outras. Com uma grelha 2D bastaria ver as 8 vizinhas (Moore).
7. **Porque usas dt?**
   Para a velocidade depender do tempo real e não do número de frames por segundo.

---

## 8. Limitações

- **Desempenho:** o custo cresce com o número de partículas paradas (ver 2.4).
- **Bordas:** `constrain` prende as partículas à borda da janela. Ficam lá "encostadas" até voltarem para dentro.
- **Cantos do quadrado:** há sementes repetidas no mesmo sítio. Não afeta o resultado.
- **Contadores `static` e `public`:** vêm da estrutura da aula. Funcionam, mas têm de ser postos a 0 no `restart`.
