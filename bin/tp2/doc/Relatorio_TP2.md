# MSSN – Projeto 2 (versão mínima para o Word)

A50738 · A52708

Só o necessário: o que está entre aspas copia-se para o Word. **Só há 2 prints, ambos no B.** No fim, guarda como PDF em `TP2_A50738_A52708/doc/`.

---

## B. Queda livre com atrito (Sheetless)

**B.1** – Diagrama de Stocks & Flows; existe algum ciclo de feedback? Qual a natureza? Relação com o comportamento?

> **[Print 1]** `src/tp2/mod/Queda_Livre_Atrito_B1.png` (o diagrama).

"Os stocks são a posição e a velocidade. Existe um ciclo de feedback **negativo**: Velocidade → Speed → Drag_Force → Ft → Acc → Velocidade. Quanto maior a velocidade, maior o atrito e menor a aceleração. Por isso o sistema tende para um equilíbrio em que o atrito iguala o peso e o corpo cai a velocidade constante (velocidade terminal)."

**B.2** – 70 kg, raio 0,4 m: quanto tempo demora a chegar ao solo e a que velocidade embate? Comente.

"Demora cerca de **25 s** e embate a cerca de **46 m/s**. Sem atrito seriam 14,3 s e 140 m/s. Com atrito, a velocidade estabiliza em poucos segundos na velocidade terminal e o resto da queda é a velocidade constante."

**B.3** – Paraquedas (10 kg, 6 m de diâmetro) aberto aos 300 m: tempo, velocidade de embate, comentário e sensações.

"Com massa total de 80 kg e área de π·3² ≈ 28 m² a partir dos 300 m, a queda demora cerca de **62 s** e chega ao solo a cerca de **6,6 m/s**, como saltar de cerca de 2 m. Antes da abertura, o paraquedista sente que flutua, porque o atrito equilibra o peso. Na abertura, sente um puxão forte, porque o atrito passa a ser muito maior do que o peso. Depois desce devagar e aterra suavemente."

**B.4** – Cai no Tejo (densidade 1000 kg/m³): força no embate e velocidade com que submerge. Comente.

> **[Print 2]** `src/tp2/mod/Queda_Livre_Atrito_B4.png` (Velocidade inicial −46; o Drag_Force mostra 531,5 mil).

"Chega à água a 46 m/s. A água é cerca de 775 vezes mais densa do que o ar, por isso a força no embate é de cerca de **531 500 N**, cerca de 775 vezes o peso. A velocidade cai quase de imediato e o paraquedista submerge a cerca de **1,65 m/s**. Um embate destes não é sobrevivível."

---

## C. Sistema Solar & Sistemas de Partículas

(O enunciado não pede respostas; basta um parágrafo.)

"No SistemaSolarApp, a tecla 1 mostra um planeta em órbita circular à volta de um Sol 200 000 vezes mais pesado (C.1). A tecla 2 mostra cinco planetas atraídos só pelo Sol (C.2a). A tecla 3 mostra todos os corpos a atraírem-se, com planetas muito mais pesados, o que deforma as órbitas e faz o Sol oscilar (C.2b). Usámos sistemas de partículas para a coroa do Sol e para a cauda do cometa, que aponta sempre para longe do Sol (C.3). Usámos o Euler semi-implícito porque mantém as órbitas estáveis."

---

## D. Comportamentos individuais (ComportamentosApp, teclas 1 a 5)

**D.1** "Teclas: b trava; + e − mudam a velocidade máxima; f e v mudam a força máxima. Ao travar, o Boid abranda aos poucos. Com pouca força e muita velocidade faz curvas largas e passa o alvo; com muita força reage logo."

**D.2** "Usámos a função da aula com R = 150 px; + e − mudam o k. Com k = 1 a travagem é linear. Com k > 1 trava mais cedo e chega devagar. Com k < 1 trava tarde e de forma brusca, podendo passar o alvo."

**D.3** "Os pontos gravam-se com cliques e a tecla p usa o método definePath(), que os devolve como caminho. O Boid percorre-os em ciclo, corta os cantos e dá voltas quando os ângulos são apertados."

**D.4** "Usámos o wander de Reynolds: o alvo é um ponto de um círculo à frente do Boid que muda um pouco ao acaso. O movimento é aleatório mas suave."

**D.5** "O Boid vagueia e, quando vê o inimigo (que segue o rato), foge e fica amarelo. Só volta a vaguear quando o inimigo está longe. Com uma só distância, ao virar-se deixava de o ver e voltava logo para trás."

---

## E. Comportamentos de grupo (FlockingApp, teclas 1 a 4; EcossistemaApp)

**E.1** "O predador persegue a presa mais próxima e aponta para onde ela vai estar. As presas fogem quando ele se aproxima. O bando abre-se e volta a juntar-se, e as apanhadas são as isoladas ou da borda."

**E.2** "Cada Boid tem um temporizador próprio (3 a 20 s), fica vermelho nos últimos 3 s e rebenta em partículas; também rebenta com um clique. À medida que desaparecem, o bando parte-se em grupos pequenos."

**E.3** "O líder é controlado com WASD e os outros seguem-no com arrive. O bando segue com atraso: nas viragens, os de trás fazem curvas mais largas."

**E.4** "O Boid observado (verde) mostra o campo de visão e a velocidade; os que ele vê ficam amarelos, e um clique escolhe outro. Os que estão mesmo atrás dele nunca ficam amarelos, porque estão na zona cega."

**E.5** "Enunciado inventado: um ecossistema predador-presa. As presas fogem e reproduzem-se; os predadores caçam, gastam energia, dividem-se quando comem o suficiente e morrem de fome. Um gráfico mostra as duas populações. O resultado foi sempre o colapso: os predadores multiplicam-se, acabam com as presas e morrem. Concluímos que, sem mecanismos como refúgios para as presas, o sistema não forma ciclos."

---

## Entrega

1. `src/tp2/doc/Relatorio.docx` com o texto acima e os 2 prints → **Guardar como PDF** → copia o PDF para `TP2_A50738_A52708/doc/`.
2. Copia a pasta `src/tp2/mod` (com `Queda_Livre_Atrito_B1.png` e `Queda_Livre_Atrito_B4.png`) para `TP2_A50738_A52708/mod`. Neste momento a pasta de entrega **não tem `mod/`**.
3. Zip **TP2_A50738_A52708.zip** só com `doc/`, `java/` e `mod/` → Moodle até às 23:59.
